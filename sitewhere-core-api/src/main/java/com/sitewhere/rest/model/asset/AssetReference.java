/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import com.sitewhere.spi.asset.IAssetReference;

/**
 * Model object for a reference to a SiteWhere asset.
 * 
 * @author Derek
 */
public class AssetReference implements IAssetReference {

    /** Asset module id */
    private String module;

    /** Asset id */
    private String id;

    /*
     * @see com.sitewhere.spi.asset.IAssetReference#getModule()
     */
    @Override
    public String getModule() {
	return module;
    }

    public void setModule(String module) {
	this.module = module;
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetReference#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public static class Builder {

	/** Reference being built */
	private AssetReference assetReference = new AssetReference();

	public Builder(IAssetReference api) {
	    assetReference.setModule(api.getModule());
	    assetReference.setId(api.getId());
	}

	public Builder(String moduleId, String assetId) {
	    assetReference.setModule(moduleId);
	    assetReference.setId(assetId);
	}

	public AssetReference build() {
	    return assetReference;
	}
    }
}