/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiChannel;

/**
 * Extends {@link ApiChannel} with methods for dealing with multitenant
 * microservices.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class MultitenantApiChannel<T extends MultitenantGrpcChannel<?, ?>> extends ApiChannel<T>
	implements IMultitenantApiChannel<T> {

    public MultitenantApiChannel(String hostname, int port) {
	super(hostname, port);
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IMultitenantApiChannel#
     * waitForTenantApiAvailable(java.util.UUID)
     */
    @Override
    public void waitForTenantApiAvailable(UUID tenantId) throws ApiChannelNotAvailableException {
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IMultitenantApiChannel#
     * waitForTenantApiAvailable(java.util.UUID, long,
     * java.util.concurrent.TimeUnit, long)
     */
    @Override
    public void waitForTenantApiAvailable(UUID tenantId, long duration, TimeUnit unit, long logMessageDelay)
	    throws ApiChannelNotAvailableException {
    }

    /*
     * @see com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiChannel#
     * checkTenantEngineAvailable()
     */
    @Override
    public boolean checkTenantEngineAvailable() {
	return getGrpcChannel().checkTenantEngineAvailable();
    }
}