/*
 * DeviceNetworkElementCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import com.sitewhere.spi.device.network.NetworkElementType;
import com.sitewhere.spi.device.request.IDeviceNetworkElementCreateRequest;

/**
 * Holds fields needed to create a new device network element.
 * 
 * @author Derek
 */
public class DeviceNetworkElementCreateRequest implements IDeviceNetworkElementCreateRequest {

	/** Element type */
	private NetworkElementType type;

	/** Element id */
	private String elementId;

	public NetworkElementType getType() {
		return type;
	}

	public void setType(NetworkElementType type) {
		this.type = type;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
}