/*
 * IDeviceNetworkCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Interface for arguments needed to create a device network.
 * 
 * @author Derek
 */
public interface IDeviceNetworkCreateRequest extends IMetadataProvider {

	/**
	 * Get the unique token.
	 * 
	 * @return
	 */
	public String getToken();

	/**
	 * Get the network name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get the network description.
	 * 
	 * @return
	 */
	public String getDescription();
}