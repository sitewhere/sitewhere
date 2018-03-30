/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.util.UUID;

import com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;

/**
 * Holds information required to create an event for a device assignment.
 * 
 * @author Derek
 */
public class DeviceAssignmentEventCreateRequest implements IDeviceAssignmentEventCreateRequest {

    /** Assignment id */
    private UUID deviceAssignmentId;

    /** Event create request */
    private IDeviceEventCreateRequest request;

    /*
     * @see
     * com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest#
     * getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return deviceAssignmentId;
    }

    public void setDeviceAssignmentId(UUID deviceAssignmentId) {
	this.deviceAssignmentId = deviceAssignmentId;
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest#
     * getRequest()
     */
    @Override
    public IDeviceEventCreateRequest getRequest() {
	return request;
    }

    public void setRequest(IDeviceEventCreateRequest request) {
	this.request = request;
    }
}