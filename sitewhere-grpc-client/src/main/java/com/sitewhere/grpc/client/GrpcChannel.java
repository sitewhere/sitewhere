/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.sitewhere.grpc.client.spi.IGrpcChannel;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

/**
 * Management wrapper for a GRPC channel.
 * 
 * @author Derek
 *
 * @param <B>
 * @param <A>
 */
public abstract class GrpcChannel<B, A> extends TenantEngineLifecycleComponent implements IGrpcChannel<B, A> {

    /** Max threads used for executing GPRC requests */
    private static final int THREAD_POOL_SIZE = 25;

    /** Function identifier */
    protected IFunctionIdentifier functionIdentifier;

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

    /** Client interceptor for adding JWT from Spring Security context */
    private JwtClientInterceptor jwtInterceptor;

    /** Executor service used to handle GRPC requests */
    private ExecutorService serverExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE,
	    new GrpcClientThreadFactory());

    public GrpcChannel(IInstanceSettings settings, IFunctionIdentifier functionIdentifier, int port) {
	this.functionIdentifier = functionIdentifier;
	this.hostname = GrpcChannel.computeHostname(settings, functionIdentifier);
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
	String namespace = "default";
	return instanceId + identifier.getPath() + "-svc." + namespace + ".svc.cluster.local";
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
	    builder.defaultServiceConfig(buildServiceConfiguration()).enableRetry();
	    builder.executor(getServerExecutor());
	    builder.usePlaintext().intercept(getJwtInterceptor());
	    this.channel = builder.build();
	    this.blockingStub = createBlockingStub();
	    this.asyncStub = createAsyncStub();
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
	name.put("service", getFunctionIdentifier().getGrpcServiceName());
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
	retryPolicy.put("maxAttempts", 5D);
	retryPolicy.put("initialBackoff", "10s");
	retryPolicy.put("maxBackoff", "300s");
	retryPolicy.put("backoffMultiplier", 2D);
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
	return blockingStub;
    }

    public void setBlockingStub(B blockingStub) {
	this.blockingStub = blockingStub;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getAsyncStub()
     */
    @Override
    public A getAsyncStub() {
	return asyncStub;
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

    /** Used for naming gRPC client executor threads */
    private class GrpcClientThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "gRPC Client " + counter.incrementAndGet());
	}
    }

    public JwtClientInterceptor getJwtInterceptor() {
	return jwtInterceptor;
    }

    public void setJwtInterceptor(JwtClientInterceptor jwtInterceptor) {
	this.jwtInterceptor = jwtInterceptor;
    }

    public IFunctionIdentifier getFunctionIdentifier() {
	return functionIdentifier;
    }

    public void setFunctionIdentifier(IFunctionIdentifier functionIdentifier) {
	this.functionIdentifier = functionIdentifier;
    }

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public ExecutorService getServerExecutor() {
	return serverExecutor;
    }

    public void setServerExecutor(ExecutorService serverExecutor) {
	this.serverExecutor = serverExecutor;
    }
}