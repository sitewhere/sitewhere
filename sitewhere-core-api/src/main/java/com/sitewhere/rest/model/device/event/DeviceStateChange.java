/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceStateChange;

/**
 * Model object for a state change reported by a remote device.
 * 
 * @author Derek
 */
public class DeviceStateChange extends DeviceEvent implements IDeviceStateChange {

    /** Serial version UID */
    private static final long serialVersionUID = 7635022335783458408L;

    /** State change category */
    private String category;

    /** State change type */
    private String type;

    /** Previous or expected state */
    private String previousState;

    /** New state */
    private String newState;

    public DeviceStateChange() {
	super(DeviceEventType.StateChange);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getCategory()
     */
    @Override
    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getType()
     */
    @Override
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getPreviousState()
     */
    @Override
    public String getPreviousState() {
	return previousState;
    }

    public void setPreviousState(String previousState) {
	this.previousState = previousState;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getNewState()
     */
    @Override
    public String getNewState() {
	return newState;
    }

    public void setNewState(String newState) {
	this.newState = newState;
    }
}