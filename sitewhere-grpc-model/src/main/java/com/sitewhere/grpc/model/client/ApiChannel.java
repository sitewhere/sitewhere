/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client;

import java.util.concurrent.TimeUnit;

import com.sitewhere.grpc.model.spi.ApiNotAvailableException;
import com.sitewhere.grpc.model.spi.IApiChannel;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.server.lifecycle.TracerUtils;

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
public abstract class ApiChannel<T extends GrpcChannel<?, ?>> extends TenantLifecycleComponent implements IApiChannel {

    /** Interval at which GRPC connection will be checked */
    private static final long CONNECTION_CHECK_INTERVAL = 2 * 1000;

    /**
     * Get underlying GRPC channel.
     * 
     * @return
     */
    public abstract T getGrpcChannel();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.spi.IApiChannel#waitForApiAvailable()
     */
    @Override
    public void waitForApiAvailable() throws ApiNotAvailableException {
	waitForApiAvailable(60, TimeUnit.SECONDS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.spi.IApiChannel#waitForApiAvailable(long,
     * java.util.concurrent.TimeUnit)
     */
    @Override
    public void waitForApiAvailable(long duration, TimeUnit unit) throws ApiNotAvailableException {
	if (getGrpcChannel() == null) {
	    throw new ApiNotAvailableException("GRPC channel not found. Unable to access API.");
	}

	ActiveSpan span = null;
	try {
	    getGrpcChannel().getTracer().buildSpan("Wait for " + getGrpcChannel().getComponentName()).startActive();

	    if (getGrpcChannel().getChannel() == null) {
		ApiNotAvailableException e = new ApiNotAvailableException(
			"GRPC channel not initialized. Unable to access API.");
		TracerUtils.handleErrorInTracerSpan(span, e);
		throw e;
	    }

	    long deadline = System.currentTimeMillis() + unit.toMillis(duration);
	    while ((System.currentTimeMillis() - deadline) < 0) {
		try {
		    ConnectivityState state = getGrpcChannel().getChannel().getState(true);
		    if (ConnectivityState.READY != state) {
			getLogger().info("Waiting for GRPC service to become available. (status:" + state.name() + ")");
			Thread.sleep(CONNECTION_CHECK_INTERVAL);
		    } else {
			return;
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
}