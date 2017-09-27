package com.sitewhere.grpc.model.spi;

import java.util.concurrent.TimeUnit;

import com.sitewhere.spi.SiteWhereException;

/**
 * Common interface for GRPC channels that handle API calls.
 * 
 * @author Derek
 */
public interface IApiChannel {

    /**
     * Wait the default amount of time for API to become available.
     * 
     * @throws ApiNotAvailableException
     */
    public void waitForApiAvailable() throws ApiNotAvailableException;

    /**
     * Wait for a maximum amount of time for the API to become available.
     * 
     * @param duration
     * @param unit
     * @throws ApiNotAvailableException
     */
    public void waitForApiAvailable(long duration, TimeUnit unit) throws ApiNotAvailableException;
}