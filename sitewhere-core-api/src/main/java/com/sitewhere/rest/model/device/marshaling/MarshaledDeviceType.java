/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.spi.asset.IAssetType;

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
    private String assetTypeName;

    /** Asset image url */
    private String assetTypeImageUrl;

    /** Device asset type */
    private IAssetType assetType;

    public String getAssetTypeName() {
	return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
	this.assetTypeName = assetTypeName;
    }

    public String getAssetTypeImageUrl() {
	return assetTypeImageUrl;
    }

    public void setAssetTypeImageUrl(String assetTypeImageUrl) {
	this.assetTypeImageUrl = assetTypeImageUrl;
    }

    public IAssetType getAssetType() {
	return assetType;
    }

    public void setAssetType(IAssetType assetType) {
	this.assetType = assetType;
    }
}