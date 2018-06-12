/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.multitenant;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.sitewhere.grpc.client.ApiChannelNotAvailableException;
import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.client.spi.IApiChannel;

/**
 * Extends {@link IApiChannel} with support for interacting with external
 * microservices that support multiple tenants.
 * 
 * @author Derek
 *
 * @param <T>
 */
public interface IMultitenantApiChannel<T extends MultitenantGrpcChannel<?, ?>> extends IApiChannel<T> {

    /**
     * Checks whether engine for tenant is available.
     * 
     * @return true if tenant is available
     */
    public boolean checkTenantEngineAvailable();

    /**
     * Wait the default amount of time for API to become available for a given
     * tenant.
     * 
     * @param tenantId
     * @throws ApiChannelNotAvailableException
     */
    public void waitForTenantApiAvailable(UUID tenantId) throws ApiChannelNotAvailableException;

    /**
     * Wait for a maximum amount of time for the API to become available for a given
     * tenant. Displays 'waiting' messages to log after a specified delay.
     * 
     * @param tenantId
     * @param duration
     * @param unit
     * @param logMessageDelay
     * @throws ApiChannelNotAvailableException
     */
    public void waitForTenantApiAvailable(UUID tenantId, long duration, TimeUnit unit, long logMessageDelay)
	    throws ApiChannelNotAvailableException;
}