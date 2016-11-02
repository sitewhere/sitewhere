/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request;

import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest;

/**
 * REST model implementation of {@link IHardwareAssetCreateRequest}.
 * 
 * @author Derek
 */
public class HardwareAssetCreateRequest extends AssetCreateRequest implements IHardwareAssetCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 3642557287516095012L;

    /** SKU */
    private String sku;

    /** Asset description */
    private String description;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest#getSku()
     */
    public String getSku() {
	return sku;
    }

    public void setSku(String sku) {
	this.sku = sku;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest#
     * getDescription()
     */
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public static class Builder {

	/** Request being built */
	private HardwareAssetCreateRequest request = new HardwareAssetCreateRequest();

	public Builder(IHardwareAsset asset) {
	    this(asset.getId(), asset.getName(), asset.getImageUrl());
	    request.setSku(asset.getSku());
	    request.setDescription(asset.getDescription());
	    request.getProperties().putAll(asset.getProperties());
	}

	public Builder(String id, String name, String imageUrl) {
	    request.setId(id);
	    request.setName(name);
	    request.setImageUrl(imageUrl);
	}

	public Builder withProperty(String name, String value) {
	    request.getProperties().put(name, value);
	    return this;
	}

	public Builder withSku(String sku) {
	    request.setSku(sku);
	    return this;
	}

	public Builder withDescription(String description) {
	    request.setDescription(description);
	    return this;
	}

	public HardwareAssetCreateRequest build() {
	    return request;
	}
    }
}