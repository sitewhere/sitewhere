/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.device.DeviceSpecification;

/**
 * Extends {@link DeviceSpecification} to support fields that can be included on
 * REST calls.
 * 
 * @author Derek
 */
public class MarshaledDeviceSpecification extends DeviceSpecification {

    /** Serial version UID */
    private static final long serialVersionUID = 4141074574302862101L;

    /** Asset name */
    private String assetName;

    /** Asset image url */
    private String assetImageUrl;

    /** Asset representing device hardware */
    private HardwareAsset asset;

    public String getAssetName() {
	return assetName;
    }

    public void setAssetName(String assetName) {
	this.assetName = assetName;
    }

    public String getAssetImageUrl() {
	return assetImageUrl;
    }

    public void setAssetImageUrl(String assetImageUrl) {
	this.assetImageUrl = assetImageUrl;
    }

    public HardwareAsset getAsset() {
	return asset;
    }

    public void setAsset(HardwareAsset asset) {
	this.asset = asset;
    }
}