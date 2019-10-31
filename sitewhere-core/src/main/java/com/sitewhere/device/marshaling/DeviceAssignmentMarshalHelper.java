/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.asset.marshaling.AssetMarshalHelper;
import com.sitewhere.device.marshaling.invalid.InvalidAsset;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceAssignment;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;

/**
 * Configurable helper class that allows DeviceAssignment model objects to be
 * created from IDeviceAssignment SPI objects.
 */
public class DeviceAssignmentMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceAssignmentMarshalHelper.class);

    /** Indicates whether device asset information is to be included */
    private boolean includeAsset = true;

    /** Indicates whether to include device information */
    private boolean includeDevice = false;

    /** Indicates whether to include customer information */
    private boolean includeCustomer = true;

    /** Indicates whether to include area information */
    private boolean includeArea = true;

    /** Indicates whether to include device type */
    private boolean includeDeviceType = true;

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Used to control marshaling of devices */
    private DeviceMarshalHelper deviceHelper;

    public DeviceAssignmentMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert the SPI object into a model object for marshaling.
     * 
     * @param source
     * @param manager
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDeviceAssignment convert(IDeviceAssignment source, IAssetManagement assetManagement)
	    throws SiteWhereException {
	MarshaledDeviceAssignment result = new MarshaledDeviceAssignment();
	result.setActiveDate(source.getActiveDate());
	result.setReleasedDate(source.getReleasedDate());
	result.setStatus(source.getStatus());
	PersistentEntity.copy(source, result);

	// If asset is assigned, look it up.
	result.setAssetId(source.getAssetId());
	if (source.getAssetId() != null) {
	    IAsset asset = assetManagement.getAsset(source.getAssetId());
	    if (asset == null) {
		LOGGER.warn("Device assignment has reference to non-existent asset.");
		asset = new InvalidAsset();
	    }
	    result.setAssetName(asset.getName());
	    result.setAssetImageUrl(asset.getImageUrl());
	    if (isIncludeAsset()) {
		result.setAsset(new AssetMarshalHelper(assetManagement).convert(asset));
	    }
	}

	// If customer is assigned, look it up.
	result.setCustomerId(source.getCustomerId());
	if ((isIncludeCustomer()) && (source.getCustomerId() != null)) {
	    ICustomer customer = getDeviceManagement().getCustomer(source.getCustomerId());
	    result.setCustomer(new CustomerMarshalHelper(deviceManagement, assetManagement).convert(customer));
	}

	// If area is assigned, look it up.
	result.setAreaId(source.getAreaId());
	if ((isIncludeArea()) && (source.getAreaId() != null)) {
	    IArea area = getDeviceManagement().getArea(source.getAreaId());
	    result.setArea(new AreaMarshalHelper(deviceManagement, assetManagement).convert(area));
	}

	// Add device information.
	result.setDeviceId(source.getDeviceId());
	if (isIncludeDevice()) {
	    IDevice device = getDeviceManagement().getDevice(source.getDeviceId());
	    if (device != null) {
		result.setDevice(getDeviceHelper().convert(device, assetManagement));
	    } else {
		LOGGER.error("Assignment references invalid device id.");
	    }
	}
	return result;
    }

    /**
     * Get the helper for marshaling device information.
     * 
     * @return
     */
    protected DeviceMarshalHelper getDeviceHelper() {
	if (deviceHelper == null) {
	    deviceHelper = new DeviceMarshalHelper(getDeviceManagement());
	    deviceHelper.setIncludeAssignment(false);
	    deviceHelper.setIncludeDeviceType(isIncludeDeviceType());
	}
	return deviceHelper;
    }

    public boolean isIncludeAsset() {
	return includeAsset;
    }

    public DeviceAssignmentMarshalHelper setIncludeAsset(boolean includeAsset) {
	this.includeAsset = includeAsset;
	return this;
    }

    public boolean isIncludeDevice() {
	return includeDevice;
    }

    public DeviceAssignmentMarshalHelper setIncludeDevice(boolean includeDevice) {
	this.includeDevice = includeDevice;
	return this;
    }

    public boolean isIncludeCustomer() {
	return includeCustomer;
    }

    public void setIncludeCustomer(boolean includeCustomer) {
	this.includeCustomer = includeCustomer;
    }

    public boolean isIncludeArea() {
	return includeArea;
    }

    public DeviceAssignmentMarshalHelper setIncludeArea(boolean includeArea) {
	this.includeArea = includeArea;
	return this;
    }

    public boolean isIncludeDeviceType() {
	return includeDeviceType;
    }

    public DeviceAssignmentMarshalHelper setIncludeDeviceType(boolean includeDeviceType) {
	this.includeDeviceType = includeDeviceType;
	return this;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}