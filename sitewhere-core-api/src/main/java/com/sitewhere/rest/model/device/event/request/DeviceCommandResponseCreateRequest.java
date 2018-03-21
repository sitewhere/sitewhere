/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;

/**
 * Model object used to create a new {@link IDeviceCommandResponse} via REST
 * APIs.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceCommandResponseCreateRequest extends DeviceEventCreateRequest
	implements IDeviceCommandResponseCreateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -9170930846188888841L;

    /** Event id that generated response */
    private UUID originatingEventId;

    /** Event sent in response */
    private UUID responseEventId;

    /** Data sent for response */
    private String response;

    public DeviceCommandResponseCreateRequest() {
	setEventType(DeviceEventType.CommandResponse);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest#
     * getOriginatingEventId()
     */
    @Override
    public UUID getOriginatingEventId() {
	return originatingEventId;
    }

    public void setOriginatingEventId(UUID originatingEventId) {
	this.originatingEventId = originatingEventId;
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest#
     * getResponseEventId()
     */
    @Override
    public UUID getResponseEventId() {
	return responseEventId;
    }

    public void setResponseEventId(UUID responseEventId) {
	this.responseEventId = responseEventId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.
     * IDeviceCommandResponseCreateRequest#getResponse ()
     */
    @Override
    public String getResponse() {
	return response;
    }

    public void setResponse(String response) {
	this.response = response;
    }
}