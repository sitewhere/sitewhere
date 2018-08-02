/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.concurrent.TimeUnit;

import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
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

    /** Interval at which GRPC connection will be checked */
    private static final long CONNECTION_CHECK_INTERVAL = 100;

    /** Parent demux */
    private IApiDemux<?> demux;

    /** Hostname */
    private String hostname;

    /** GRPC Port */
    private int port;

    /** Underlying GRPC channel */
    private T grpcChannel;

    public ApiChannel(IApiDemux<?> demux, String hostname, int port) {
	this.demux = demux;
	this.hostname = hostname;
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
	this.grpcChannel = (T) createGrpcChannel(getMicroservice(), getHostname(), getPort());
	initializeNestedComponent(getGrpcChannel(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getGrpcChannel(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getGrpcChannel(), monitor);
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#waitForChannelAvailable()
     */
    @Override
    public void waitForChannelAvailable() throws ApiChannelNotAvailableException {
	waitForChannelAvailable(5 * 60, TimeUnit.SECONDS, 60);
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#waitForChannelAvailable(long,
     * java.util.concurrent.TimeUnit, long)
     */
    @Override
    public void waitForChannelAvailable(long duration, TimeUnit unit, long logMessageDelay)
	    throws ApiChannelNotAvailableException {
	if (getGrpcChannel() == null) {
	    throw new ApiChannelNotAvailableException("GRPC channel not found. Unable to access API.");
	}

	long start = System.currentTimeMillis();
	long deadline = start + unit.toMillis(duration);
	long logAfter = start + unit.toMillis(logMessageDelay);
	while ((System.currentTimeMillis() - deadline) < 0) {
	    try {
		if (getGrpcChannel().getChannel() == null) {
		    if ((System.currentTimeMillis() - logAfter) > 0) {
			getLogger().info("Waiting for GRPC channel to be initialized.");
		    }
		    Thread.sleep(CONNECTION_CHECK_INTERVAL);
		} else {
		    ConnectivityState state = getGrpcChannel().getChannel().getState(true);
		    if (ConnectivityState.READY != state) {
			if ((System.currentTimeMillis() - logAfter) > 0) {
			    getLogger().info(
				    "Waiting for GRPC service to become available. (status:" + state.name() + ")");
			}
			Thread.sleep(CONNECTION_CHECK_INTERVAL);
		    } else {
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

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#getDemux()
     */
    @Override
    public IApiDemux<?> getDemux() {
	return demux;
    }

    public void setDemux(IApiDemux<?> demux) {
	this.demux = demux;
    }
}