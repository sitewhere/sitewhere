/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;

/**
 * Base model class for an asset.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset implements IAsset {

    /** Serial version UID */
    private static final long serialVersionUID = -853673101089583873L;

    /** Unique id */
    private String id;

    /** Asset name */
    private String name;

    /** Asset type indicator */
    private AssetType type;

    /** Asset category id */
    private String assetCategoryId;

    /** Asset image url */
    private String imageUrl;

    private Map<String, String> properties = new HashMap<String, String>();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAsset#getId()
     */
    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAsset#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAsset#getType()
     */
    public AssetType getType() {
	return type;
    }

    public void setType(AssetType type) {
	this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAsset#getAssetCategoryId()
     */
    public String getAssetCategoryId() {
	return assetCategoryId;
    }

    public void setAssetCategoryId(String assetCategoryId) {
	this.assetCategoryId = assetCategoryId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAsset#getImageUrl()
     */
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAsset#getProperties()
     */
    public Map<String, String> getProperties() {
	return properties;
    }

    public void setProperties(Map<String, String> properties) {
	this.properties = properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(IAsset other) {
	if (getName() != null) {
	    return getName().compareTo(other.getName());
	}
	return 0;
    }
}