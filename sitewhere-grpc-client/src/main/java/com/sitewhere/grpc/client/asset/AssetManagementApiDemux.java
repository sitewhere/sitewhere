/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import com.sitewhere.grpc.client.MultitenantApiDemux;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;

/**
 * Demultiplexes asset management requests across one or more API channels.
 * 
 * @author Derek
 *
 * @param <IAssetManagementApiChannel>
 */
public class AssetManagementApiDemux extends MultitenantApiDemux<IAssetManagementApiChannel<?>>
	implements IAssetManagementApiDemux {

    public AssetManagementApiDemux(boolean cacheEnabled) {
	super(cacheEnabled);
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiDemux#getTargetIdentifier()
     */
    @Override
    public IFunctionIdentifier getTargetIdentifier() {
	return MicroserviceIdentifier.AssetManagement;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiDemux#createApiChannel(java.lang.String,
     * boolean)
     */
    @Override
    public IAssetManagementApiChannel<?> createApiChannel(String host, boolean cacheEnabled) throws SiteWhereException {
	CachedAssetManagementApiChannel.CacheSettings settings = new CachedAssetManagementApiChannel.CacheSettings();
	if (!cacheEnabled) {
	    settings.getAssetTypeConfiguration().setEnabled(false);
	    settings.getAssetConfiguration().setEnabled(false);
	}
	return new CachedAssetManagementApiChannel(this, host, getMicroservice().getInstanceSettings().getGrpcPort(),
		settings);
    }
}