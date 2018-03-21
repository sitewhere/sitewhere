/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;

/**
 * Implementation of {@link IDeviceCommandResponse}.
 * 
 * @author Derek
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceCommandResponse extends DeviceEvent implements IDeviceCommandResponse, Serializable {

    /** For Java serialization */
    private static final long serialVersionUID = 4448837178695704510L;

    /** Event id that generated response */
    private UUID originatingEventId;

    /** Event sent in response */
    private UUID responseEventId;

    /** Data sent for response */
    private String response;

    public DeviceCommandResponse() {
	super(DeviceEventType.CommandResponse);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceCommandResponse#getOriginatingEventId()
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
     * com.sitewhere.spi.device.event.IDeviceCommandResponse#getResponseEventId()
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
     * @see com.sitewhere.spi.device.event.IDeviceCommandResponse#getResponse()
     */
    @Override
    public String getResponse() {
	return response;
    }

    public void setResponse(String response) {
	this.response = response;
    }

    /**
     * Create a copy of an SPI object. Used by web services for marshaling.
     * 
     * @param input
     * @return
     */
    public static DeviceCommandResponse copy(IDeviceCommandResponse input) throws SiteWhereException {
	DeviceCommandResponse result = new DeviceCommandResponse();
	DeviceEvent.copy(input, result);
	result.setOriginatingEventId(input.getOriginatingEventId());
	result.setResponseEventId(input.getResponseEventId());
	result.setResponse(input.getResponse());
	return result;
    }
}