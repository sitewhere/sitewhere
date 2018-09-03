/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.asset.AssetCategory;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Model object for an asset type.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class AssetType extends BrandedEntity implements IAssetType {

    /** Serial version UID */
    private static final long serialVersionUID = -112992823230126026L;

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** Asset category */
    private AssetCategory assetCategory = AssetCategory.Device;

    /*
     * @see com.sitewhere.spi.asset.IAssetType#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetType#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetType#getAssetCategory()
     */
    @Override
    public AssetCategory getAssetCategory() {
	return assetCategory;
    }

    public void setAssetCategory(AssetCategory assetCategory) {
	this.assetCategory = assetCategory;
    }
}