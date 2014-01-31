/*
 * DeviceRegistrationCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationCreateRequest;

/**
 * Model object used to create a new {@link DeviceMeasurements} via REST APIs.
 * 
 * @author Derek
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceRegistrationCreateRequest extends DeviceEventCreateRequest implements
		IDeviceRegistrationCreateRequest {

	/** Device hardware id */
	private String hardwareId;

	/** Hardware specification token */
	private String specificationToken;

	/** 'Reply to' address */
	private String replyTo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.request.IDeviceRegistrationCreateRequest#getHardwareId
	 * ()
	 */
	public String getHardwareId() {
		return hardwareId;
	}

	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.request.IDeviceRegistrationCreateRequest#
	 * getSpecificationToken()
	 */
	public String getSpecificationToken() {
		return specificationToken;
	}

	public void setSpecificationToken(String specificationToken) {
		this.specificationToken = specificationToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.request.IDeviceRegistrationCreateRequest#getReplyTo
	 * ()
	 */
	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
}