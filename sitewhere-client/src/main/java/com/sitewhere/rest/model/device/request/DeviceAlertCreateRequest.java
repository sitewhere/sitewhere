/*
 * DeviceAlertCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import com.sitewhere.rest.model.device.DeviceAlert;
import com.sitewhere.spi.device.AlertLevel;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;

/**
 * Model object used to create a new {@link DeviceAlert} via REST APIs.
 * 
 * @author Derek
 */
public class DeviceAlertCreateRequest extends DeviceEventCreateRequest implements IDeviceAlertCreateRequest {

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