/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.io.Serializable;

import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;

/**
 * Model object used to create a new {@link DeviceAlert} via REST APIs.
 * 
 * @author Derek
 */
public class DeviceAlertCreateRequest extends DeviceEventCreateRequest implements IDeviceAlertCreateRequest,
		Serializable {

	/** Serialization version identifier */
	private static final long serialVersionUID = 7660473778731839384L;

	/** Alert level */
	private AlertLevel level;

	/** Alert type */
	private String type;

	/** Alert message */
	private String message;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceAlertCreateRequest#getLevel()
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
	 * @see com.sitewhere.spi.device.request.IDeviceAlertCreateRequest#getMessage()
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}