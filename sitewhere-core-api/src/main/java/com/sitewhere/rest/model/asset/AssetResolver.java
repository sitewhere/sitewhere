/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManagement;
import com.sitewhere.spi.asset.IAssetResolver;

/**
 * Default {@link IAssetResolver} implementation.
 * 
 * @author Derek
 */
public class AssetResolver implements IAssetResolver {

    /** Asset management */
    private IAssetManagement assetManagement;

    /** Asset module management */
    private IAssetModuleManagement assetModuleManagement;

    public AssetResolver(IAssetManagement assetManagement, IAssetModuleManagement assetModuleManagement) {
	this.assetManagement = assetManagement;
	this.assetModuleManagement = assetModuleManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetResolver#getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetResolver#getAssetModuleManagement()
     */
    @Override
    public IAssetModuleManagement getAssetModuleManagement() {
	return assetModuleManagement;
    }

    public void setAssetModuleManagement(IAssetModuleManagement assetModuleManagement) {
	this.assetModuleManagement = assetModuleManagement;
    }
}