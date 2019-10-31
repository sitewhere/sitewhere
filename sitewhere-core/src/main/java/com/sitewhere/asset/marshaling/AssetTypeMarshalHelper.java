/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.marshaling;

import com.sitewhere.rest.model.asset.AssetType;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Configurable helper class that allows {@link AssetType} model objects to be
 * created from {@link IAssetType} SPI objects.
 */
public class AssetTypeMarshalHelper {

    /** Asset management */
    private IAssetManagement assetManagement;

    public AssetTypeMarshalHelper(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public AssetType convert(IAssetType source) throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	AssetType type = new AssetType();
	type.setName(source.getName());
	type.setDescription(source.getDescription());
	type.setAssetCategory(source.getAssetCategory());
	BrandedEntity.copy(source, type);
	return type;
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }
}