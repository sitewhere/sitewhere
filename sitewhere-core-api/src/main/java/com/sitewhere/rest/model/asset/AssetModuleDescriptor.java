/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModuleDescriptor;

/**
 * Model object implementation of {@link IAssetModuleDescriptor}.
 * 
 * @author Derek
 */
public class AssetModuleDescriptor implements IAssetModuleDescriptor {

    /** Module id */
    private String id;

    /** Module name */
    private String name;

    /** Module asset type */
    private AssetType assetType;

    /*
     * @see com.sitewhere.spi.asset.IAssetModuleDescriptor#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetModuleDescriptor#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetModuleDescriptor#getAssetType()
     */
    @Override
    public AssetType getAssetType() {
	return assetType;
    }

    public void setAssetType(AssetType assetType) {
	this.assetType = assetType;
    }
}