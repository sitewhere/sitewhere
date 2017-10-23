/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.scripting;

import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceMeasurements;

/**
 * Provides support for common operations on device events in scripting
 * operations.
 * 
 * @author Derek
 */
public class DeviceEventSupport {

    /** Device assignment */
    private IDeviceAssignment deviceAssignment;

    /** Wrapped event */
    private IDeviceEvent event;

    public DeviceEventSupport(IDeviceAssignment deviceAssignment, IDeviceEvent event) {
	this.deviceAssignment = deviceAssignment;
	this.event = event;
    }

    /**
     * Indicates a location event.
     * 
     * @return
     */
    public boolean isLocation() {
	return event.getEventType() == DeviceEventType.Location;
    }

    /**
     * Indicates a measurements event.
     * 
     * @return
     */
    public boolean isMeasurements() {
	return event.getEventType() == DeviceEventType.Measurements;
    }

    /**
     * Indicates a command invocation event.
     * 
     * @return
     */
    public boolean isCommandInvocation() {
	return event.getEventType() == DeviceEventType.CommandInvocation;
    }

    /**
     * Indicates a command response event.
     * 
     * @return
     */
    public boolean isCommandResponse() {
	return event.getEventType() == DeviceEventType.CommandResponse;
    }

    /**
     * Indicates if event has a measurement with the given name.
     * 
     * @param name
     * @return
     */
    public boolean hasMeasurement(String name) {
	return (getMeasurement(name) != null);
    }

    /**
     * Attempts to get the named measurement if the event is a measurements
     * event.
     * 
     * @param name
     * @return
     */
    public Double getMeasurement(String name) {
	if (event.getEventType() != DeviceEventType.Measurements) {
	    return null;
	}
	IDeviceMeasurements mxs = (IDeviceMeasurements) event;
	Double mx = mxs.getMeasurement(name);
	if (mx == null) {
	    return null;
	}
	return mx;
    }

    /**
     * Returns true if event is an alert.
     * 
     * @return
     */
    public boolean isAlert() {
	return event.getEventType() == DeviceEventType.Alert;
    }

    /**
     * Returns true if event is an alert and has the given type.
     * 
     * @param type
     * @return
     */
    public boolean isAlertOfType(String type) {
	return (isAlert() && ((IDeviceAlert) event).getType().equals(type));
    }

    public IDeviceAssignment getDeviceAssignment() {
	return deviceAssignment;
    }

    public void setDeviceAssignment(IDeviceAssignment deviceAssignment) {
	this.deviceAssignment = deviceAssignment;
    }

    public IDeviceEvent data() {
	return event;
    }

    public IDeviceEvent getData() {
	return event;
    }
}