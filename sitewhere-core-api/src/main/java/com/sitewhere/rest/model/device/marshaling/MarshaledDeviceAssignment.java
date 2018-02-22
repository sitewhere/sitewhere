/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.device.IDevice;

/**
 * Extends {@link DeviceAssignment} to support fields that can be included on
 * REST calls.
 * 
 * @author Derek
 */
public class MarshaledDeviceAssignment extends DeviceAssignment {

    /** Serial version UID */
    private static final long serialVersionUID = -6149550465354186892L;

    /** Device being assigned */
    private IDevice device;

    /** Assigned area */
    private IArea area;

    /** Associated asset */
    private IAsset asset;

    /** Associated asset name */
    private String assetName;

    /** Associated asset image */
    private String assetImageUrl;

    public IDevice getDevice() {
	return device;
    }

    public void setDevice(IDevice device) {
	this.device = device;
    }

    public IArea getArea() {
	return area;
    }

    public void setArea(IArea area) {
	this.area = area;
    }

    public IAsset getAsset() {
	return asset;
    }

    public void setAsset(IAsset asset) {
	this.asset = asset;
    }

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
}