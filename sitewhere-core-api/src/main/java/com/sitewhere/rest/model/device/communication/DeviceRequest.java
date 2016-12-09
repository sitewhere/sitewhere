/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.communication;

/**
 * Used for marshaling device request data to JSON.
 * 
 * @author Derek
 */
public class DeviceRequest {

    /** Hardware id the request applies to */
    private String hardwareId;

    /** Originating invocation if available */
    private String originator;

    /** Request type */
    private Type type;

    /** Event create request */
    private Object request;

    public String getHardwareId() {
	return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
	this.hardwareId = hardwareId;
    }

    public String getOriginator() {
	return originator;
    }

    public void setOriginator(String originator) {
	this.originator = originator;
    }

    public Type getType() {
	return type;
    }

    public void setType(Type type) {
	this.type = type;
    }

    public Object getRequest() {
	return request;
    }

    public void setRequest(Object request) {
	this.request = request;
    }

    /**
     * Enumerates types of expected requests.
     * 
     * @author Derek
     */
    public static enum Type {

	/** Register a device */
	RegisterDevice,

	/** Send a device location event */
	DeviceLocation,

	/** Send a device alert event */
	DeviceAlert,

	/** Send a device measurements event */
	DeviceMeasurements,

	/** Send a device steam create request */
	DeviceStream,

	/** Send a device stream data event */
	DeviceStreamData,

	/** Send a command acknowledgement */
	Acknowledge,

	/** Map a device to a slot on a composite device */
	MapDevice,
    }
}