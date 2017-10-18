/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.modules;

import com.sitewhere.asset.spi.modules.IAssetModule;
import com.sitewhere.spi.asset.AssetType;

/**
 * Model object used to show asset module information externally.
 * 
 * @author dadams
 */
public class AssetModule {

    /** Module id */
    private String id;

    /** Module name */
    private String name;

    /** Asset type */
    private AssetType assetType;

    public AssetModule() {
    }

    public AssetModule(String id, String name, AssetType assetType) {
	this.id = id;
	this.name = name;
	this.assetType = assetType;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public AssetType getAssetType() {
	return assetType;
    }

    public void setAssetType(AssetType assetType) {
	this.assetType = assetType;
    }

    /**
     * Create a copy of an SPI object. Used by web services for marshaling.
     * 
     * @param input
     * @return
     */
    public static AssetModule copy(IAssetModule<?> input) {
	AssetModule module = new AssetModule();
	module.setId(input.getId());
	module.setName(input.getName());
	module.setAssetType(input.getAssetType());
	return module;
    }
}