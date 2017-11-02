/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.state.StateChangeCategory;
import com.sitewhere.spi.device.event.state.StateChangeType;

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

    /** State change category */
    private StateChangeCategory category;

    /** State change type */
    private StateChangeType type;

    /** Previous or expected state */
    private String previousState;

    /** New state */
    private String newState;

    /** Data associated with the state change */
    private Map<String, String> data = new HashMap<String, String>();

    public DeviceStateChangeCreateRequest(StateChangeCategory category, StateChangeType type, String previousState,
	    String newState) {
	this.category = category;
	this.type = type;
	this.previousState = previousState;
	this.newState = newState;
	setEventType(DeviceEventType.StateChange);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceStateChangeRequest#
     * getCategory()
     */
    public StateChangeCategory getCategory() {
	return category;
    }

    public void setCategory(StateChangeCategory category) {
	this.category = category;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeRequest#getType(
     * )
     */
    public StateChangeType getType() {
	return type;
    }

    public void setType(StateChangeType type) {
	this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceStateChangeRequest#
     * getPreviousState()
     */
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
    public String getNewState() {
	return newState;
    }

    public void setNewState(String newState) {
	this.newState = newState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeRequest#getData(
     * )
     */
    public Map<String, String> getData() {
	return data;
    }

    public void setData(Map<String, String> data) {
	this.data = data;
    }
}