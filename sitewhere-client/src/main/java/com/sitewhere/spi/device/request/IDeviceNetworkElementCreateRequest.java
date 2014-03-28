/*
 * IDeviceNetworkEntryCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import com.sitewhere.spi.device.network.NetworkElementType;

/**
 * Interface for arguments needed to create a device network element.
 * 
 * @author Derek
 */
public interface IDeviceNetworkElementCreateRequest {

	/**
	 * Get the element type.
	 * 
	 * @return
	 */
	public NetworkElementType getType();

	/**
	 * Get the element id (relative to element type).
	 * 
	 * @return
	 */
	public String getElementId();
}