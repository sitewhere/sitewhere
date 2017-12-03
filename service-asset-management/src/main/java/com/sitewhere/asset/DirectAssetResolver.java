/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset;

import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManagement;
import com.sitewhere.spi.asset.IAssetResolver;

/**
 * Asset resolver that uses the persistence APIs directly.
 * 
 * @author Derek
 */
public class DirectAssetResolver implements IAssetResolver {

    /** Asset management implementation */
    private IAssetManagement assetManagement;

    /** Asset module management implementation */
    private IAssetModuleManagement assetModuleManagement;

    public DirectAssetResolver(IAssetManagement assetManagement, IAssetModuleManagement assetModuleManagement) {
	this.assetManagement = assetManagement;
	this.assetModuleManagement = assetModuleManagement;
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetResolver#getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetResolver#getAssetModuleManagement()
     */
    @Override
    public IAssetModuleManagement getAssetModuleManagement() {
	return assetModuleManagement;
    }
}