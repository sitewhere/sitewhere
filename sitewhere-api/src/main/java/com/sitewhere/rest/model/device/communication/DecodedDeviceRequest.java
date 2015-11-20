/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.communication;

import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;

/**
 * Default implementation of {@link IDecodedDeviceRequest}.
 * 
 * @author Derek
 */
public class DecodedDeviceRequest<T> implements IDecodedDeviceRequest<T> {

	/** Serial version UID */
	private static final long serialVersionUID = 4280270339471220181L;

	/** Hardware id the request applies to */
	private String hardwareId;

	/** Originating invocation if available */
	private String originator;

	/** Event create request */
	private T request;

	public DecodedDeviceRequest() {
	}

	public DecodedDeviceRequest(String hardwareId, String originator, T request) {
		setHardwareId(hardwareId);
		setOriginator(originator);
		setRequest(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IDecodedDeviceRequest#getHardwareId()
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
	 * @see com.sitewhere.spi.device.communication.IDecodedDeviceRequest#getOriginator()
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
	 * @see com.sitewhere.spi.device.communication.IDecodedDeviceRequest#getRequest()
	 */
	public T getRequest() {
		return request;
	}

	public void setRequest(T request) {
		this.request = request;
	}
}