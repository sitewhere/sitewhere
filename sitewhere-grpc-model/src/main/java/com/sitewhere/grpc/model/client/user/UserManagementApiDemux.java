/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client.user;

import com.sitewhere.grpc.model.client.ApiDemux;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceIdentifiers;

/**
 * Demultiplexes user management requests across one or more API channels.
 * 
 * @author Derek
 *
 * @param <IAssetManagementApiChannel>
 */
public class UserManagementApiDemux extends ApiDemux<IUserManagementApiChannel> implements IUserManagementApiDemux {

    public UserManagementApiDemux(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiDemux#getTargetIdentifier()
     */
    @Override
    public String getTargetIdentifier() {
	return IMicroserviceIdentifiers.USER_MANAGEMENT;
    }

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiDemux#createApiChannel(java.lang.String)
     */
    @Override
    public IUserManagementApiChannel createApiChannel(String host) throws SiteWhereException {
	return new UserManagementApiChannel(getMicroservice(), host);
    }
}