/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.marshaling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.asset.Asset;
import com.sitewhere.rest.model.asset.marshaling.MarshaledAsset;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Configurable helper class that allows {@link Asset} model objects to be
 * created from {@link IAsset} SPI objects.
 */
public class AssetMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(AssetMarshalHelper.class);

    /** Asset management */
    private IAssetManagement assetManagement;

    /** Indicates whether asset type information should be included */
    private boolean includeAssetType;

    public AssetMarshalHelper(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledAsset convert(IAsset source) throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	MarshaledAsset asset = new MarshaledAsset();
	asset.setId(source.getId());
	asset.setToken(source.getToken());
	asset.setAssetTypeId(source.getAssetTypeId());
	asset.setName(source.getName());
	asset.setImageUrl(source.getImageUrl());
	PersistentEntity.copy(source, asset);

	if (isIncludeAssetType()) {
	    IAssetType assetType = getAssetManagement().getAssetType(source.getAssetTypeId());
	    if (assetType != null) {
		asset.setAssetType(new AssetTypeMarshalHelper(assetManagement).convert(assetType));
	    } else {
		LOGGER.warn("Asset references invalid asset type.");
	    }
	}
	return asset;
    }

    public boolean isIncludeAssetType() {
	return includeAssetType;
    }

    public void setIncludeAssetType(boolean includeAssetType) {
	this.includeAssetType = includeAssetType;
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }
}