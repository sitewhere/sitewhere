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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;

/**
 * Model object used to create a new {@link DeviceAlert} via REST APIs.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAlertCreateRequest extends DeviceEventCreateRequest
	implements IDeviceAlertCreateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = 7660473778731839384L;

    /** Alert source */
    private AlertSource source = AlertSource.Device;

    /** Alert level */
    private AlertLevel level = AlertLevel.Info;

    /** Alert type */
    private String type;

    /** Alert message */
    private String message;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest#
     * getSource()
     */
    public AlertSource getSource() {
	return source;
    }

    public void setSource(AlertSource source) {
	this.source = source;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.request.IDeviceAlertCreateRequest#getLevel()
     */
    public AlertLevel getLevel() {
	return level;
    }

    public void setLevel(AlertLevel level) {
	this.level = level;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceAlertCreateRequest#getType()
     */
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.request.IDeviceAlertCreateRequest#getMessage()
     */
    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public static class Builder extends DeviceEventCreateRequest.Builder<DeviceAlertCreateRequest> {

	private DeviceAlertCreateRequest request = new DeviceAlertCreateRequest();

	public Builder(String type, String message) {
	    request.setType(type);
	    request.setMessage(message);
	    request.setSource(AlertSource.Device);
	    request.setLevel(AlertLevel.Info);
	}

	public Builder asSystemAlert() {
	    request.setSource(AlertSource.System);
	    return this;
	}

	public Builder warning() {
	    request.setLevel(AlertLevel.Warning);
	    return this;
	}

	public Builder error() {
	    request.setLevel(AlertLevel.Error);
	    return this;
	}

	public Builder critical() {
	    request.setLevel(AlertLevel.Critical);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.request.
	 * DeviceEventCreateRequest.Builder# getRequest()
	 */
	@Override
	public DeviceAlertCreateRequest getRequest() {
	    return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.request.
	 * DeviceEventCreateRequest.Builder# build()
	 */
	@Override
	public DeviceAlertCreateRequest build() {
	    return request;
	}
    }
}