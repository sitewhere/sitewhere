/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.io.Serializable;

import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;

/**
 * Represents a change in state for a device. This may be a statement of the
 * current state (i.e. a piece of hardware is unavailable) or a request for
 * change of system state (i.e the device requests registration in the system).
 * 
 * @author Derek
 */
public class DeviceStateChangeCreateRequest extends DeviceEventCreateRequest
	implements IDeviceStateChangeCreateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -2789928889465310950L;

    /** Attribute being updated */
    private String attribute;

    /** State change type */
    private String type;

    /** Previous or expected state */
    private String previousState;

    /** New state */
    private String newState;

    public DeviceStateChangeCreateRequest() {
	setEventType(DeviceEventType.StateChange);
    }

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest#
     * getAttribute()
     */
    @Override
    public String getAttribute() {
	return attribute;
    }

    public void setAttribute(String attribute) {
	this.attribute = attribute;
    }

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest#
     * getType()
     */
    @Override
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceStateChangeRequest#
     * getPreviousState()
     */
    @Override
    public String getPreviousState() {
	return previousState;
    }

    public void setPreviousState(String previousState) {
	this.previousState = previousState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceStateChangeRequest#
     * getNewState()
     */
    @Override
    public String getNewState() {
	return newState;
    }

    public void setNewState(String newState) {
	this.newState = newState;
    }
}