/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import java.util.UUID;

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.asset.AssetCategory;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Model object for an asset type.
 * 
 * @author Derek
 */
public class AssetType extends MetadataProviderEntity implements IAssetType {

    /** Serial version UID */
    private static final long serialVersionUID = -112992823230126026L;

    /** Unique id */
    private UUID id;

    /** Reference token */
    private String token;

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** Image URL */
    private String imageUrl;

    /** Asset category */
    private AssetCategory assetCategory = AssetCategory.Device;

    /*
     * @see com.sitewhere.spi.asset.IAssetType#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.asset.IAssetType#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

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
     * @see com.sitewhere.spi.asset.IAssetType#getImageUrl()
     */
    @Override
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
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