/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.spi.IGrpcChannel;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

/**
 * Management wrapper for a GRPC channel.
 *
 * @param <B>
 * @param <A>
 */
public abstract class GrpcChannel<B, A> extends TenantEngineLifecycleComponent implements IGrpcChannel<B, A> {

    /** Number of seconds between DNS checks */
    protected static final int DNS_CHECK_INTERVAL_SECS = 5;

    /** Numer of retries for DNS checks */
    protected static final int DNS_CHECK_RETRIES = 8;

    /** Instance settings */
    protected IInstanceSettings instanceSettings;

    /** Function identifier */
    protected IFunctionIdentifier functionIdentifier;

    /** gRPC service identifier */
    protected IGrpcServiceIdentifier grpcServiceIdentifier;

    /** Remote host */
    protected String hostname;

    /** Remote port */
    protected int port;

    /** GRPC managed channe */
    protected ManagedChannel channel;

    /** Blocking stub */
    protected B blockingStub;

    /** Asynchronous stub */
    protected A asyncStub;

    /** Indicates whether DNS is currently verifying */
    protected CountDownLatch dnsVerifying = new CountDownLatch(2);

    /** Tracks whether DNS is verification succeeded */
    protected AtomicBoolean dnsVerified = new AtomicBoolean(false);

    /** Client interceptor for adding JWT from Spring Security context */
    protected JwtClientInterceptor jwtInterceptor;

    /** Thread for DNS resolution */
    protected ExecutorService dnsExecutor = Executors.newSingleThreadExecutor();

    public GrpcChannel(IInstanceSettings instanceSettings, IFunctionIdentifier functionIdentifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	this.instanceSettings = instanceSettings;
	this.functionIdentifier = functionIdentifier;
	this.grpcServiceIdentifier = grpcServiceIdentifier;
	this.hostname = GrpcChannel.computeHostname(instanceSettings, functionIdentifier);
	this.port = port;

	this.jwtInterceptor = new JwtClientInterceptor();
    }

    /**
     * Compute service hostname based on instance settings and functional
     * indentifier.
     * 
     * @param settings
     * @param identifier
     * @return
     */
    public static String computeHostname(IInstanceSettings settings, IFunctionIdentifier identifier) {
	String instanceId = "sitewhere".equals(settings.getInstanceId()) ? "sitewhere-"
		: settings.getInstanceId() + "-sitewhere-";
	String namespace = settings.getKubernetesNamespace().isPresent() ? settings.getKubernetesNamespace().get()
		: "default";
	if (settings.isGrpcResolveFQDN()) {
	    return instanceId + identifier.getPath() + "-svc." + namespace + ".svc.cluster.local";
	} else {
	    return instanceId + identifier.getPath() + "-svc";
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    NettyChannelBuilder builder = NettyChannelBuilder.forAddress(getHostname(), getPort());
	    builder.defaultServiceConfig(buildServiceConfiguration()).enableRetry().disableServiceConfigLookUp();
	    builder.usePlaintext().intercept(getJwtInterceptor());
	    this.channel = builder.build();
	    this.blockingStub = createBlockingStub();
	    this.asyncStub = createAsyncStub();
	    getLogger().info(
		    String.format("Creating gRPC client channel connected to %s:%d ...", getHostname(), getPort()));
	} catch (Throwable t) {
	    throw new SiteWhereException("Unhandled exception starting gRPC channel.", t);
	}
    }

    /**
     * Build service configuration that enables retry support.
     * 
     * @return
     */
    protected Map<String, Object> buildServiceConfiguration() {
	Map<String, Object> serviceConfig = new HashMap<>();
	serviceConfig.put("methodConfig", Collections.<Object>singletonList(buildMethodConfiguration()));
	getLogger().info(
		"Channel using service configuration:\n\n" + MarshalUtils.marshalJsonAsPrettyString(serviceConfig));
	return serviceConfig;
    }

    /**
     * Build service configuration that enables retry support.
     * 
     * @return
     */
    protected Map<String, Object> buildMethodConfiguration() {
	Map<String, Object> methodConfig = new HashMap<>();
	Map<String, Object> name = new HashMap<>();
	name.put("service", getGrpcServiceIdentifier().getGrpcServiceName());
	methodConfig.put("name", Collections.<Object>singletonList(name));
	methodConfig.put("retryPolicy", buildRetryPolicy());
	return methodConfig;
    }

    /**
     * Configure retry policy.
     * 
     * @return
     */
    protected Map<String, Object> buildRetryPolicy() {
	Map<String, Object> retryPolicy = new HashMap<>();
	retryPolicy.put("maxAttempts", getInstanceSettings().getGrpcMaxRetryCount());
	retryPolicy.put("initialBackoff", String.format("%ds", getInstanceSettings().getGrpcInitialBackoffInSeconds()));
	retryPolicy.put("maxBackoff", String.format("%ds", getInstanceSettings().getGrpcMaxBackoffInSeconds()));
	retryPolicy.put("backoffMultiplier", getInstanceSettings().getGrpcBackoffMultiplier());
	retryPolicy.put("retryableStatusCodes", Arrays.<Object>asList("UNAVAILABLE"));
	return retryPolicy;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getChannel() != null) {
	    getChannel().shutdown();
	}
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getChannel()
     */
    @Override
    public ManagedChannel getChannel() {
	return channel;
    }

    public void setChannel(ManagedChannel channel) {
	this.channel = channel;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getBlockingStub()
     */
    @Override
    public B getBlockingStub() {
	if (isDnsValid()) {
	    return blockingStub;
	} else {
	    throw new RuntimeException("Unable to invoke gRPC operation. DNS check failed.");
	}
    }

    public void setBlockingStub(B blockingStub) {
	this.blockingStub = blockingStub;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getAsyncStub()
     */
    @Override
    public A getAsyncStub() {
	if (isDnsValid()) {
	    return asyncStub;
	} else {
	    throw new RuntimeException("Unable to invoke gRPC operation. DNS check failed.");
	}
    }

    public void setAsyncStub(A asyncStub) {
	this.asyncStub = asyncStub;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#createBlockingStub()
     */
    @Override
    public abstract B createBlockingStub();

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#createAsyncStub()
     */
    @Override
    public abstract A createAsyncStub();

    /**
     * Check whether DNS is valid.
     * 
     * @return
     */
    protected boolean isDnsValid() {
	if (getDnsVerified().get()) {
	    return true;
	}
	if (getDnsVerifying().getCount() == 2) {
	    getDnsExecutor().execute(new DnsVerifier());
	}
	try {
	    getDnsVerifying().await();
	} catch (InterruptedException e) {
	    return false;
	}
	return getDnsVerified().get();
    }

    /**
     * Attempts to check DNS with a given number of retries.
     */
    private class DnsVerifier implements Runnable {

	@Override
	public void run() {
	    getDnsVerifying().countDown();
	    try {
		int retries = DNS_CHECK_RETRIES;
		while (retries > 0) {
		    try {
			InetAddress.getByName(getHostname());
			getLogger().info(String.format("Resolved DNS for '%s'", getHostname()));
			getDnsVerified().set(true);
			return;
		    } catch (Throwable e) {
			getLogger().debug(String.format("Unable to resolve DNS for '%s'", getHostname()), e);
		    }
		    getLogger().info(String.format("Waiting for DNS to resolve '%s'", getHostname()));
		    try {
			Thread.sleep(5000);
		    } catch (InterruptedException e) {
			getDnsVerified().set(false);
			return;
		    }

		    retries--;
		}
		getDnsVerified().set(false);
	    } finally {
		getDnsVerifying().countDown();
	    }
	}
    }

    public ExecutorService getDnsExecutor() {
	return dnsExecutor;
    }

    public CountDownLatch getDnsVerifying() {
	return dnsVerifying;
    }

    public AtomicBoolean getDnsVerified() {
	return dnsVerified;
    }

    public JwtClientInterceptor getJwtInterceptor() {
	return jwtInterceptor;
    }

    public IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    public IFunctionIdentifier getFunctionIdentifier() {
	return functionIdentifier;
    }

    public IGrpcServiceIdentifier getGrpcServiceIdentifier() {
	return grpcServiceIdentifier;
    }

    public String getHostname() {
	return hostname;
    }

    public int getPort() {
	return port;
    }
}