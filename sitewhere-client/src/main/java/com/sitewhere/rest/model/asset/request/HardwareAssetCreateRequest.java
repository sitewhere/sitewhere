/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request;

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
	 * @see com.sitewhere.spi.asset.request.IHardwareAssetCreateRequest#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}