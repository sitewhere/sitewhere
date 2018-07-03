/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import com.sitewhere.device.marshaling.invalid.InvalidArea;
import com.sitewhere.device.marshaling.invalid.InvalidAsset;
import com.sitewhere.device.marshaling.invalid.InvalidCustomer;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceState;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.state.IDeviceState;

/**
 * Configurable helper class that allows {@link DeviceState} model objects to be
 * created from {@link IDeviceState} SPI objects.
 * 
 * @author dadams
 */
public class DeviceStateMarshalHelper {

    /** Indicates whether to include device information */
    private boolean includeDevice = false;

    /** Indicates whether to include device type */
    private boolean includeDeviceType = true;

    /** Indicates whether to include customer information */
    private boolean includeCustomer = true;

    /** Indicates whether to include area information */
    private boolean includeArea = true;

    /** Indicates whether device asset information is to be included */
    private boolean includeAsset = true;

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Used to control marshaling of devices */
    private DeviceMarshalHelper deviceHelper;

    public DeviceStateMarshalHelper(IDeviceManagement deviceManagement) {
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
    public MarshaledDeviceState convert(IDeviceState source, IAssetManagement assetManagement)
	    throws SiteWhereException {
	MarshaledDeviceState result = new MarshaledDeviceState();
	result.setId(source.getId());
	result.setDeviceId(source.getDeviceId());
	result.setDeviceTypeId(source.getDeviceTypeId());
	result.setDeviceAssignmentId(source.getDeviceAssignmentId());
	result.setCustomerId(source.getCustomerId());
	result.setAreaId(source.getAreaId());
	result.setAssetId(source.getAssetId());
	result.setLastInteractionDate(source.getLastInteractionDate());
	result.setPresenceMissingDate(source.getPresenceMissingDate());
	result.setLastLocationEventId(source.getLastLocationEventId());
	result.getLastMeasurementEventIds().putAll(source.getLastMeasurementEventIds());
	result.getLastAlertEventIds().putAll(source.getLastAlertEventIds());

	// Add device information.
	result.setDeviceId(source.getDeviceId());
	if (isIncludeDevice()) {
	    IDevice device = getDeviceManagement().getDevice(source.getDeviceId());
	    if (device != null) {
		result.setDevice(getDeviceHelper().convert(device, assetManagement));
	    }
	}

	// If customer is assigned, look it up.
	if ((isIncludeCustomer()) && (source.getCustomerId() != null)) {
	    ICustomer customer = getDeviceManagement().getCustomer(source.getCustomerId());
	    if (customer == null) {
		customer = new InvalidCustomer();
	    }
	    result.setCustomer(customer);
	}

	// If area is assigned, look it up.
	if ((isIncludeArea()) && (source.getAreaId() != null)) {
	    IArea area = getDeviceManagement().getArea(source.getAreaId());
	    if (area == null) {
		area = new InvalidArea();
	    }
	    result.setArea(area);
	}

	// If asset is assigned, look it up.
	if (isIncludeAsset() && (source.getAssetId() != null)) {
	    IAsset asset = assetManagement.getAsset(source.getAssetId());
	    if (asset == null) {
		asset = new InvalidAsset();
	    }
	    result.setAsset(asset);
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

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public boolean isIncludeDevice() {
	return includeDevice;
    }

    public void setIncludeDevice(boolean includeDevice) {
	this.includeDevice = includeDevice;
    }

    public boolean isIncludeDeviceType() {
	return includeDeviceType;
    }

    public void setIncludeDeviceType(boolean includeDeviceType) {
	this.includeDeviceType = includeDeviceType;
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

    public void setIncludeArea(boolean includeArea) {
	this.includeArea = includeArea;
    }

    public boolean isIncludeAsset() {
	return includeAsset;
    }

    public void setIncludeAsset(boolean includeAsset) {
	this.includeAsset = includeAsset;
    }
}