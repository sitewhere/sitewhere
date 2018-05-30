/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiChannel;
import com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiDemux;

/**
 * Extends {@link ApiDemux} with functionality required to support multitenant
 * APIs.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class MultitenantApiDemux<T extends IMultitenantApiChannel<?>> extends ApiDemux<T>
	implements IMultitenantApiDemux<T> {

    /**
     * Indicates if the given API channel should be considered a match for a given
     * request.
     * 
     * @param channel
     * @return
     */
    protected boolean isApiChannelMatch(T channel) {
	getLogger().info("Checking for tenant engine available.");
	return channel.checkTenantEngineAvailable();
    }
}