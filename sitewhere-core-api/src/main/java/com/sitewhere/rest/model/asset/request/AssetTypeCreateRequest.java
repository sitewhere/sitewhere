/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.request.BrandedEntityCreateRequest;
import com.sitewhere.spi.asset.AssetCategory;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;

/**
 * REST model implementation of {@link IAssetTypeCreateRequest}.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class AssetTypeCreateRequest extends BrandedEntityCreateRequest implements IAssetTypeCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 4980888004733279548L;

    /** Name */
    private String name;

    /** Description */
    private String description;

    /** Asset category */
    private AssetCategory assetCategory;

    /*
     * @see com.sitewhere.spi.asset.request.IAssetTypeCreateRequest#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.asset.request.IAssetTypeCreateRequest#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see
     * com.sitewhere.spi.asset.request.IAssetTypeCreateRequest#getAssetCategory()
     */
    @Override
    public AssetCategory getAssetCategory() {
	return assetCategory;
    }

    public void setAssetCategory(AssetCategory assetCategory) {
	this.assetCategory = assetCategory;
    }

    public static class Builder {

	/** Request being built */
	private AssetTypeCreateRequest request = new AssetTypeCreateRequest();

	public Builder(String token, String name) {
	    request.setToken(token);
	    request.setName(name);
	    request.setAssetCategory(AssetCategory.Hardware);
	}

	public Builder asHardware() {
	    request.setAssetCategory(AssetCategory.Hardware);
	    return this;
	}

	public Builder asPerson() {
	    request.setAssetCategory(AssetCategory.Person);
	    return this;
	}

	public Builder asDevice() {
	    request.setAssetCategory(AssetCategory.Device);
	    return this;
	}

	public Builder withDescription(String description) {
	    request.setDescription(description);
	    return this;
	}

	public Builder withImageUrl(String imageUrl) {
	    request.setImageUrl(imageUrl);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public AssetTypeCreateRequest build() {
	    return request;
	}
    }
}