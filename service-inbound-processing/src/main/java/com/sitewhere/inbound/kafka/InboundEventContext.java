/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.kafka;

import java.util.List;

import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;

/**
 * Context built by processing inbound events.
 */
public class InboundEventContext {

    /** Decoded event payload */
    private GDecodedEventPayload decodedEventPayload;

    /** Device */
    private IDevice device;

    /** Device assignments list */
    private List<? extends IDeviceAssignment> deviceAssignments;

    /** Exception in processing */
    private Exception exception;

    public InboundEventContext(GDecodedEventPayload decodedEventPayload) {
	this.decodedEventPayload = decodedEventPayload;
    }

    public GDecodedEventPayload getDecodedEventPayload() {
	return decodedEventPayload;
    }

    public void setDecodedEventPayload(GDecodedEventPayload decodedEventPayload) {
	this.decodedEventPayload = decodedEventPayload;
    }

    public IDevice getDevice() {
	return device;
    }

    public void setDevice(IDevice device) {
	this.device = device;
    }

    public List<? extends IDeviceAssignment> getDeviceAssignments() {
	return deviceAssignments;
    }

    public void setDeviceAssignments(List<? extends IDeviceAssignment> deviceAssignments) {
	this.deviceAssignments = deviceAssignments;
    }

    public Exception getException() {
	return exception;
    }

    public void setException(Exception exception) {
	this.exception = exception;
    }
}
