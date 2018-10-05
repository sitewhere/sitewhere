/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.microservice;

import com.sitewhere.grpc.client.ApiDemux;
import com.sitewhere.grpc.client.spi.client.IMicroserviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IMicroserviceManagementApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;

/**
 * Demultiplexes microservice management requests across one or more API
 * channels.
 * 
 * @author Derek
 *
 * @param <IAssetManagementApiChannel>
 */
public class MicroserviceManagementApiDemux extends ApiDemux<IMicroserviceManagementApiChannel<?>>
	implements IMicroserviceManagementApiDemux {

    /** Target identifier */
    private IFunctionIdentifier targetIdentifier;

    public MicroserviceManagementApiDemux(IFunctionIdentifier targetIdentifier) {
	this.targetIdentifier = targetIdentifier;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiDemux#getTargetIdentifier()
     */
    @Override
    public IFunctionIdentifier getTargetIdentifier() {
	return targetIdentifier;
    }

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiDemux#createApiChannel(java.lang.String)
     */
    @Override
    public IMicroserviceManagementApiChannel<?> createApiChannel(String host) throws SiteWhereException {
	return new MicroserviceManagementApiChannel(this, host,
		getMicroservice().getInstanceSettings().getManagementGrpcPort());
    }
}