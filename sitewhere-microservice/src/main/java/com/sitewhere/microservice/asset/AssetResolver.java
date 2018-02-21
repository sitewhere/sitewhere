/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.asset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.spi.client.IAssetManagementApiDemux;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManagement;
import com.sitewhere.spi.asset.IAssetResolver;

/**
 * Default {@link IAssetResolver} implementation.
 * 
 * @author Derek
 */
public class AssetResolver implements IAssetResolver {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(AssetResolver.class);

    /** Asset management demux */
    private IAssetManagementApiDemux assetManagementDemux;

    public AssetResolver(IAssetManagementApiDemux assetManagementDemux) {
	this.assetManagementDemux = assetManagementDemux;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetResolver#getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return getAssetManagementDemux().getApiChannel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetResolver#getAssetModuleManagement()
     */
    @Override
    public IAssetModuleManagement getAssetModuleManagement() {
	return getAssetManagementDemux().getApiChannel();
    }

    public IAssetManagementApiDemux getAssetManagementDemux() {
	return assetManagementDemux;
    }

    public void setAssetManagementDemux(IAssetManagementApiDemux assetManagementDemux) {
	this.assetManagementDemux = assetManagementDemux;
    }
}