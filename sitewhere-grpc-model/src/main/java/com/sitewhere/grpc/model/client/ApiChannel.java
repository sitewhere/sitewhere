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
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
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
public abstract class ApiChannel<T extends GrpcChannel<?, ?>> extends TenantEngineLifecycleComponent implements IApiChannel {

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
	waitForApiAvailable(5 * 60, TimeUnit.SECONDS, 20);
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
}