/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import com.sitewhere.grpc.client.ApiDemux;
import com.sitewhere.grpc.client.spi.client.IUserManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IUserManagementApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;

/**
 * Demultiplexes user management requests across one or more API channels.
 * 
 * @author Derek
 *
 * @param <IAssetManagementApiChannel>
 */
public class UserManagementApiDemux extends ApiDemux<IUserManagementApiChannel<?>> implements IUserManagementApiDemux {

    public UserManagementApiDemux(boolean cacheEnabled) {
	super(cacheEnabled);
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiDemux#getTargetIdentifier()
     */
    @Override
    public IFunctionIdentifier getTargetIdentifier() {
	return MicroserviceIdentifier.UserManagement;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiDemux#createApiChannel(java.lang.String,
     * boolean)
     */
    @Override
    public IUserManagementApiChannel<?> createApiChannel(String host, boolean cacheEnabled) throws SiteWhereException {
	CachedUserManagementApiChannel.CacheSettings settings = new CachedUserManagementApiChannel.CacheSettings();
	if (!cacheEnabled) {
	    settings.getUserConfiguration().setEnabled(false);
	}
	return new CachedUserManagementApiChannel(this, host, getMicroservice().getInstanceSettings().getGrpcPort(),
		settings);
    }
}