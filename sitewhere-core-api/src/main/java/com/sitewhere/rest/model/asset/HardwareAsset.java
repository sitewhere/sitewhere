/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IHardwareAsset;
import java.io.Serializable;

/**
 * Model class for a hardware asset.
 * 
 * @author dadams
 */
public class HardwareAsset extends Asset implements IHardwareAsset, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = -9215203722090581894L;

    /** SKU */
    private String sku;

    /** Asset description */
    private String description;

    public HardwareAsset() {
	setType(AssetType.Hardware);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IHardwareAsset#getSku()
     */
    @Override
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
    @Override
    public String getDescription() {
	return this.description;
    }

    public void setDescription(String description) {
	this.description = description;
    }
}