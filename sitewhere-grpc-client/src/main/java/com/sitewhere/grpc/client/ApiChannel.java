/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.concurrent.TimeUnit;

import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.ExponentialBackOff;

import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.ConnectivityState;

/**
 * Base class for channels that uses SiteWhere APIs to communicate with GRPC
 * services.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class ApiChannel<T extends GrpcChannel<?, ?>> extends TenantEngineLifecycleComponent
	implements IApiChannel<T> {

    /** Min interval at which GRPC connection will be checked */
    private static final long CONNECTION_CHECK_INTERVAL_MIN_MS = 100;

    /** Max interval at which GRPC connection will be checked */
    private static final long CONNECTION_CHECK_INTERVAL_MAX_MS = 60 * 1000;

    /** Hostname */
    private String hostname;

    /** GRPC Port */
    private int port;

    /** Underlying GRPC channel */
    private T grpcChannel;

    public ApiChannel(IInstanceSettings settings, IFunctionIdentifier identifier, int port) {
	String instanceId = "sitewhere".equals(settings.getInstanceId()) ? "sitewhere-"
		: settings.getInstanceId() + "-sitewhere-";
	String namespace = "default";
	this.hostname = instanceId + identifier.getPath() + "-svc." + namespace + ".svc.cluster.local";
	this.port = port;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiChannel#getGrpcChannel()
     */
    @Override
    public T getGrpcChannel() {
	return grpcChannel;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("Initializing API channel to '%s:%d'", getHostname(), getPort()));
	this.grpcChannel = createGrpcChannel(getHostname(), getPort());
	initializeNestedComponent(getGrpcChannel(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("Starting API channel to '%s:%d'", getHostname(), getPort()));
	startNestedComponent(getGrpcChannel(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("Stopping API channel to '%s:%d'", getHostname(), getPort()));
	stopNestedComponent(getGrpcChannel(), monitor);
    }

    public void exampleOfExponentialBackoff(long duration, TimeUnit unit, long logMessageDelay)
	    throws ApiChannelNotAvailableException {
	if (getGrpcChannel() == null) {
	    throw new ApiChannelNotAvailableException("GRPC channel not found. Unable to access API.");
	}

	getLogger().info("Waiting a maximum of " + unit.toSeconds(duration) + " seconds for connection to '"
		+ getHostname() + ":" + getPort() + "'...");

	long start = System.currentTimeMillis();
	long deadline = start + unit.toMillis(duration);
	long logAfter = start + unit.toMillis(logMessageDelay);

	ExponentialBackOff backOff = new ExponentialBackOff(CONNECTION_CHECK_INTERVAL_MIN_MS, 1.5);
	backOff.setMaxInterval(CONNECTION_CHECK_INTERVAL_MAX_MS);
	BackOffExecution backOffExec = backOff.start();
	while ((System.currentTimeMillis() - deadline) < 0) {
	    long waitPeriod = backOffExec.nextBackOff();
	    try {
		if (getGrpcChannel().getChannel() == null) {
		    if ((System.currentTimeMillis() - logAfter) > 0) {
			getLogger().info("Waiting for GRPC channel to be initialized.");
		    }
		    Thread.sleep(waitPeriod);
		} else {
		    ConnectivityState state = getGrpcChannel().getChannel().getState(true);
		    if (ConnectivityState.READY != state) {
			if ((System.currentTimeMillis() - logAfter) > 0) {
			    getLogger().info("Waiting for GRPC service to become available on '" + getHostname()
				    + "'. (status:" + state.name() + ")");
			}
			Thread.sleep(waitPeriod);
		    } else {
			getLogger().info(
				String.format("Detected '%s' state for gRPC channel.", ConnectivityState.READY.name()));
			return;
		    }
		}
	    } catch (Exception e) {
		throw new ApiChannelNotAvailableException("Unhandled exception waiting for API to become available.",
			e);
	    }
	}
	ApiChannelNotAvailableException e = new ApiChannelNotAvailableException(
		"API not available within timeout period.");
	throw e;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#getHostname()
     */
    @Override
    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#getPort()
     */
    @Override
    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }
}