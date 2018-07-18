/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Extends {@link DeviceState} to support fields that can be included on REST
 * calls.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class MarshaledDeviceState extends DeviceState {

    /** Serial version UID */
    private static final long serialVersionUID = -699681075037936315L;

    /** Device */
    private IDevice device;

    /** Device type */
    private IDeviceType deviceType;

    /** Device assignment */
    private IDeviceAssignment deviceAssignment;

    /** Assigned customer */
    private ICustomer customer;

    /** Assigned area */
    private IArea area;

    /** Associated asset */
    private IAsset asset;

    /** Last device location */
    private IDeviceLocation lastLocationEvent;

    /** Map of last measurement events by measurement id */
    private Map<String, IDeviceMeasurement> lastMeasurementEvents;

    /** Map of last alert events by alert type */
    private Map<String, IDeviceAlert> lastAlertEvents;

    public IDevice getDevice() {
	return device;
    }

    public void setDevice(IDevice device) {
	this.device = device;
    }

    public IDeviceType getDeviceType() {
	return deviceType;
    }

    public void setDeviceType(IDeviceType deviceType) {
	this.deviceType = deviceType;
    }

    public IDeviceAssignment getDeviceAssignment() {
	return deviceAssignment;
    }

    public void setDeviceAssignment(IDeviceAssignment deviceAssignment) {
	this.deviceAssignment = deviceAssignment;
    }

    public ICustomer getCustomer() {
	return customer;
    }

    public void setCustomer(ICustomer customer) {
	this.customer = customer;
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

    public IDeviceLocation getLastLocationEvent() {
	return lastLocationEvent;
    }

    public void setLastLocationEvent(IDeviceLocation lastLocationEvent) {
	this.lastLocationEvent = lastLocationEvent;
    }

    public Map<String, IDeviceMeasurement> getLastMeasurementEvents() {
	return lastMeasurementEvents;
    }

    public void setLastMeasurementEvents(Map<String, IDeviceMeasurement> lastMeasurementEvents) {
	this.lastMeasurementEvents = lastMeasurementEvents;
    }

    public Map<String, IDeviceAlert> getLastAlertEvents() {
	return lastAlertEvents;
    }

    public void setLastAlertEvents(Map<String, IDeviceAlert> lastAlertEvents) {
	this.lastAlertEvents = lastAlertEvents;
    }
}
