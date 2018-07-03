/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.SiteWhereEntity;
import com.sitewhere.spi.asset.IAsset;

/**
 * Model object for an asset.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class Asset extends SiteWhereEntity implements IAsset {

    /** Serial version UID */
    private static final long serialVersionUID = -853673101089583873L;

    /** Unique id */
    private UUID id;

    /** Reference token */
    private String token;

    /** Asset type id */
    private UUID assetTypeId;

    /** Asset name */
    private String name;

    /** Asset image url */
    private String imageUrl;

    /*
     * @see com.sitewhere.spi.asset.IAsset#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.asset.IAsset#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see com.sitewhere.spi.asset.IAsset#getAssetTypeId()
     */
    @Override
    public UUID getAssetTypeId() {
	return assetTypeId;
    }

    public void setAssetTypeId(UUID assetTypeId) {
	this.assetTypeId = assetTypeId;
    }

    /*
     * @see com.sitewhere.spi.asset.IAsset#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.asset.IAsset#getImageUrl()
     */
    @Override
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }
}