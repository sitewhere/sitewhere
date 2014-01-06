/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.rest.model.asset;

import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IHardwareAsset;

/**
 * Model class for a hardware asset.
 * 
 * @author dadams
 */
public class HardwareAsset extends Asset implements IHardwareAsset {

	/** SKU */
	private String sku;

	/** Asset description */
	private String description;

	/** Asset image URL */
	private String imageUrl;

	public HardwareAsset() {
		setType(AssetType.Hardware);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IHardwareAsset#getSku()
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
	 * @see com.sitewhere.spi.asset.IHardwareAsset#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IHardwareAsset#getImageUrl()
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String url) {
		this.imageUrl = url;
	}
}