/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request;

import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest;

/**
 * REST model implementation of {@link IAssetCategoryCreateRequest}.
 * 
 * @author Derek
 */
public class AssetCategoryCreateRequest implements IAssetCategoryCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 8950287661317676377L;

    /** Category id */
    private String id;

    /** Category name */
    private String name;

    /** Category asset type */
    private AssetType assetType;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest#getId()
     */
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest#getName()
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
     * @see
     * com.sitewhere.spi.asset.request.IAssetCategoryCreateRequest#getAssetType(
     * )
     */
    public AssetType getAssetType() {
	return assetType;
    }

    public void setAssetType(AssetType assetType) {
	this.assetType = assetType;
    }

    public static class Builder {

	/** Request being built */
	private AssetCategoryCreateRequest request = new AssetCategoryCreateRequest();

	public Builder(IAssetCategory category) {
	    this(category.getId(), category.getName());
	    request.setAssetType(category.getAssetType());
	}

	public Builder(String id, String name) {
	    request.setId(id);
	    request.setName(name);
	    request.setAssetType(AssetType.Hardware);
	}

	public Builder forHardwareAssets() {
	    request.setAssetType(AssetType.Hardware);
	    return this;
	}

	public Builder forDeviceAssets() {
	    request.setAssetType(AssetType.Device);
	    return this;
	}

	public Builder forLocationAssets() {
	    request.setAssetType(AssetType.Location);
	    return this;
	}

	public Builder forPersonAssets() {
	    request.setAssetType(AssetType.Person);
	    return this;
	}

	public AssetCategoryCreateRequest build() {
	    return request;
	}
    }
}