/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import com.sitewhere.grpc.client.ApiDemux;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;

/**
 * Demultiplexes asset management requests across one or more API channels.
 * 
 * @author Derek
 *
 * @param <IAssetManagementApiChannel>
 */
public class AssetManagementApiDemux extends ApiDemux<IAssetManagementApiChannel<?>>
	implements IAssetManagementApiDemux {

    /*
     * @see com.sitewhere.grpc.client.spi.IApiDemux#getTargetIdentifier()
     */
    @Override
    public MicroserviceIdentifier getTargetIdentifier() {
	return MicroserviceIdentifier.AssetManagement;
    }

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiDemux#createApiChannel(java.lang.String)
     */
    @Override
    public IAssetManagementApiChannel<?> createApiChannel(String host) throws SiteWhereException {
	return new CachedAssetManagementApiChannel(this, getMicroservice(), host);
    }
}