/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Extends {@link DeviceState} to support fields that can be included on REST
 * calls.
 * 
 * @author Derek
 */
public class MarshaledDeviceState extends DeviceState {

    /** Serial version UID */
    private static final long serialVersionUID = -699681075037936315L;

    /** Device */
    private IDevice device;

    /** Device type */
    private IDeviceType deviceType;

    /** Assigned customer */
    private ICustomer customer;

    /** Assigned area */
    private IArea area;

    /** Associated asset */
    private IAsset asset;

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
}
