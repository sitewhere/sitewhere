/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.element.IDeviceElementSchema;

/**
 * Model object for device specification information.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceSpecification extends MetadataProviderEntity implements IDeviceSpecification, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -843279944125910257L;

    /** Unique token */
    private String token;

    /** Specificaiton name */
    private String name;

    /** Asset module id */
    private String assetModuleId;

    /** Asset id of device hardware */
    private String assetId;

    /** Asset name */
    private String assetName;

    /** Asset image url */
    private String assetImageUrl;

    /** Asset representing device hardware */
    private HardwareAsset asset;

    /** Device container policy */
    private DeviceContainerPolicy containerPolicy = DeviceContainerPolicy.Standalone;

    /** Schema that specifies allowable locations of nested devices */
    private DeviceElementSchema deviceElementSchema;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceSpecification#getToken()
     */
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceSpecification#getName()
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
     * @see com.sitewhere.spi.device.IDeviceSpecification#getAssetModuleId()
     */
    public String getAssetModuleId() {
	return assetModuleId;
    }

    public void setAssetModuleId(String assetModuleId) {
	this.assetModuleId = assetModuleId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceSpecification#getAssetId()
     */
    public String getAssetId() {
	return assetId;
    }

    public void setAssetId(String assetId) {
	this.assetId = assetId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceSpecification#getContainerPolicy()
     */
    public DeviceContainerPolicy getContainerPolicy() {
	return containerPolicy;
    }

    public void setContainerPolicy(DeviceContainerPolicy containerPolicy) {
	this.containerPolicy = containerPolicy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceSpecification#getDeviceElementSchema()
     */
    public IDeviceElementSchema getDeviceElementSchema() {
	return deviceElementSchema;
    }

    public void setDeviceElementSchema(DeviceElementSchema deviceElementSchema) {
	this.deviceElementSchema = deviceElementSchema;
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

    public HardwareAsset getAsset() {
	return asset;
    }

    public void setAsset(HardwareAsset asset) {
	this.asset = asset;
    }
}