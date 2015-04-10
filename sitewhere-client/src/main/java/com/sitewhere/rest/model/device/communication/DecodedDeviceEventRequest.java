/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.communication;

import com.sitewhere.spi.device.communication.IDecodedDeviceEventRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;

/**
 * Default implementation of {@link IDecodedDeviceEventRequest}.
 * 
 * @author Derek
 */
public class DecodedDeviceEventRequest implements IDecodedDeviceEventRequest {

	/** Hardware id the request applies to */
	private String hardwareId;

	/** Originating invocation if available */
	private String originator;

	/** Event create request */
	private IDeviceEventCreateRequest request;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDecodedDeviceEventRequest#getHardwareId()
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
	 * @see
	 * com.sitewhere.spi.device.communication.IDecodedDeviceEventRequest#getOriginator()
	 */
	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IDecodedDeviceEventRequest#getRequest()
	 */
	public IDeviceEventCreateRequest getRequest() {
		return request;
	}

	public void setRequest(IDeviceEventCreateRequest request) {
		this.request = request;
	}
}