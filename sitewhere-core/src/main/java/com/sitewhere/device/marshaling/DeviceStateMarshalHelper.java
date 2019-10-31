/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.state.IDeviceState;

/**
 * Configurable helper class that allows {@link DeviceState} model objects to be
 * created from {@link IDeviceState} SPI objects.
 */
public class DeviceStateMarshalHelper {

    /** Indicates whether to include device information */
    private boolean includeDevice = false;

    /** Indicates whether to include device type */
    private boolean includeDeviceType = false;

    /** Indicates whether to include device assignment information */
    private boolean includeDeviceAssignment = false;

    /** Indicates whether to include customer information */
    private boolean includeCustomer = false;

    /** Indicates whether to include area information */
    private boolean includeArea = false;

    /** Indicates whether device asset information is to be included */
    private boolean includeAsset = false;

    /** Indicates whether event details should be included */
    private boolean includeEventDetails = false;

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Device event management */
    private IDeviceEventManagement deviceEventManagement;

    /** Used to control marshaling of devices */
    private DeviceMarshalHelper deviceHelper;

    public DeviceStateMarshalHelper(IDeviceManagement deviceManagement, IDeviceEventManagement deviceEventManagement) {
	this.deviceManagement = deviceManagement;
	this.deviceEventManagement = deviceEventManagement;
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

	addAssignmentDetail(source, assetManagement, result);
	addEventDetail(source, assetManagement, result);

	return result;
    }

    protected void addAssignmentDetail(IDeviceState source, IAssetManagement assetManagement,
	    MarshaledDeviceState result) throws SiteWhereException {
	// Add device information.
	if (isIncludeDevice()) {
	    IDevice device = getDeviceManagement().getDevice(source.getDeviceId());
	    if (device != null) {
		result.setDevice(getDeviceHelper().convert(device, assetManagement));
	    }
	}

	// Add device type information.
	if (isIncludeDeviceType()) {
	    IDeviceType deviceType = getDeviceManagement().getDeviceType(source.getDeviceTypeId());
	    if (deviceType != null) {
		result.setDeviceType(deviceType);
	    }
	}

	// Add device assignment information.
	if (isIncludeDeviceAssignment()) {
	    IDeviceAssignment deviceAssignment = getDeviceManagement()
		    .getDeviceAssignment(source.getDeviceAssignmentId());
	    if (deviceAssignment != null) {
		result.setDeviceAssignment(deviceAssignment);
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
    }

    protected void addEventDetail(IDeviceState source, IAssetManagement assetManagement, MarshaledDeviceState result)
	    throws SiteWhereException {
	if (isIncludeEventDetails()) {
	    if (source.getLastLocationEventId() != null) {
		IDeviceLocation location = (IDeviceLocation) getDeviceEventManagement()
			.getDeviceEventById(source.getLastLocationEventId());
		result.setLastLocationEvent(location);
	    }

	    if (source.getLastMeasurementEventIds() != null) {
		Map<String, IDeviceMeasurement> mxs = new HashMap<>();
		for (String key : source.getLastMeasurementEventIds().keySet()) {
		    UUID id = source.getLastMeasurementEventIds().get(key);
		    IDeviceMeasurement mx = (IDeviceMeasurement) getDeviceEventManagement().getDeviceEventById(id);
		    mxs.put(key, mx);
		}
		result.setLastMeasurementEvents(mxs);
	    }
	    if (source.getLastAlertEventIds() != null) {
		Map<String, IDeviceAlert> alerts = new HashMap<>();
		for (String key : source.getLastAlertEventIds().keySet()) {
		    UUID id = source.getLastAlertEventIds().get(key);
		    IDeviceAlert alert = (IDeviceAlert) getDeviceEventManagement().getDeviceEventById(id);
		    alerts.put(key, alert);
		}
		result.setLastAlertEvents(alerts);
	    }
	}
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
	    deviceHelper.setIncludeDeviceType(false);
	}
	return deviceHelper;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IDeviceEventManagement getDeviceEventManagement() {
	return deviceEventManagement;
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
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

    public boolean isIncludeDeviceAssignment() {
	return includeDeviceAssignment;
    }

    public void setIncludeDeviceAssignment(boolean includeDeviceAssignment) {
	this.includeDeviceAssignment = includeDeviceAssignment;
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

    public boolean isIncludeEventDetails() {
	return includeEventDetails;
    }

    public void setIncludeEventDetails(boolean includeEventDetails) {
	this.includeEventDetails = includeEventDetails;
    }
}