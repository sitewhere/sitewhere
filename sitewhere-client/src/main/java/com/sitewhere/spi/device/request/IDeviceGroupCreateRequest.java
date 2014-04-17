/*
 * IDeviceGroupCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.List;

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Interface for arguments needed to create a device group.
 * 
 * @author Derek
 */
public interface IDeviceGroupCreateRequest extends IMetadataProvider {

	/**
	 * Get the unique token.
	 * 
	 * @return
	 */
	public String getToken();

	/**
	 * Get the group name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get the group description.
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * Get list of roles associated with group.
	 * 
	 * @return
	 */
	public List<String> getRoles();
}