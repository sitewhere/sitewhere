/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceType;

/**
 * Extends {@link Device} to support fields that can be included on REST calls.
 * 
 * @author Derek
 */
public class MarshaledDevice extends Device {

    /** Serial version UID */
    private static final long serialVersionUID = -7249138366647616811L;

    /** Device type */
    private DeviceType deviceType;

    /** Current device assignment */
    private DeviceAssignment assignment;

    public DeviceType getDeviceType() {
	return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
	this.deviceType = deviceType;
    }

    public DeviceAssignment getAssignment() {
	return assignment;
    }

    public void setAssignment(DeviceAssignment assignment) {
	this.assignment = assignment;
    }
}