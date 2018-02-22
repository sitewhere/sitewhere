/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.spi.asset.IAsset;

/**
 * Extends {@link DeviceSpecification} to support fields that can be included on
 * REST calls.
 * 
 * @author Derek
 */
public class MarshaledDeviceType extends DeviceType {

    /** Serial version UID */
    private static final long serialVersionUID = 8391781508712506005L;

    /** Asset name */
    private String assetName;

    /** Asset image url */
    private String assetImageUrl;

    /** Device asset */
    private IAsset asset;

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

    public IAsset getAsset() {
	return asset;
    }

    public void setAsset(IAsset asset) {
	this.asset = asset;
    }
}