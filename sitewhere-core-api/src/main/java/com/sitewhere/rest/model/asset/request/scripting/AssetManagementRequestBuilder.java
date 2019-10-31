/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request.scripting;

import com.sitewhere.rest.model.asset.request.AssetCreateRequest;
import com.sitewhere.rest.model.asset.request.AssetTypeCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Builder that supports creating asset management entities.
 */
public class AssetManagementRequestBuilder {

    /** Asset management implementation */
    private IAssetManagement assetManagement;

    public AssetManagementRequestBuilder(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    public AssetTypeCreateRequest.Builder newAssetType(String token, String name) {
	return new AssetTypeCreateRequest.Builder(token, name);
    }

    public IAssetType persist(AssetTypeCreateRequest.Builder builder) throws SiteWhereException {
	return getAssetManagement().createAssetType(builder.build());
    }

    public AssetCreateRequest.Builder newAsset(String token, String assetTypeToken, String name) {
	return new AssetCreateRequest.Builder(token, assetTypeToken, name);
    }

    public IAsset persist(AssetCreateRequest.Builder builder) throws SiteWhereException {
	return getAssetManagement().createAsset(builder.build());
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }
}