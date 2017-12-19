/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.concurrent.TimeUnit;

import com.sitewhere.grpc.client.spi.ApiNotAvailableException;
import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.server.lifecycle.TracerUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.ConnectivityState;
import io.opentracing.ActiveSpan;

/**
 * Base class for channels that uses SiteWhere APIs to communicate with GRPC
 * services.
 * 
 * @author Derek
 *
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public abstract class ApiChannel<T extends GrpcChannel<?, ?>> extends TenantEngineLifecycleComponent
	implements IApiChannel {

    /** Interval at which GRPC connection will be checked */
    private static final long CONNECTION_CHECK_INTERVAL = 2 * 1000;

    /** Parent demux */
    private IApiDemux<?> demux;

    /** Microservice */
    private IMicroservice microservice;

    /** Hostname */
    private String hostname;

    /** GRPC Port */
    private int port;

    /** Underlying GRPC channel */
    private T grpcChannel;

    public ApiChannel(IApiDemux<?> demux, IMicroservice microservice, String hostname) {
	this(demux, microservice, hostname, microservice.getInstanceSettings().getGrpcPort());
    }

    public ApiChannel(IApiDemux<?> demux, IMicroservice microservice, String hostname, int port) {
	this.demux = demux;
	this.microservice = microservice;
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
    @SuppressWarnings("unchecked")
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.spi.IApiChannel#waitForApiAvailable()
     */
    @Override
    public void waitForApiAvailable() throws ApiNotAvailableException {
	waitForApiAvailable(5 * 60, TimeUnit.SECONDS, 60);
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiChannel#waitForApiAvailable(long,
     * java.util.concurrent.TimeUnit, long)
     */
    @Override
    public void waitForApiAvailable(long duration, TimeUnit unit, long logMessageDelay)
	    throws ApiNotAvailableException {
	if (getGrpcChannel() == null) {
	    throw new ApiNotAvailableException("GRPC channel not found. Unable to access API.");
	}

	ActiveSpan span = null;
	try {
	    span = getGrpcChannel().getTracer().buildSpan("Wait for " + getGrpcChannel().getComponentName())
		    .startActive();

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
		    TracerUtils.handleErrorInTracerSpan(span, e);
		    throw new ApiNotAvailableException("Unhandled exception waiting for API to become available.", e);
		}
	    }
	    ApiNotAvailableException e = new ApiNotAvailableException("API not available within timeout period.");
	    TracerUtils.handleErrorInTracerSpan(span, e);
	    throw e;
	} finally {
	    TracerUtils.finishTracerSpan(span);
	}
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

    /*
     * @see com.sitewhere.grpc.model.spi.IApiChannel#getMicroservice()
     */
    @Override
    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }
}