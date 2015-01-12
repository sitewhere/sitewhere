/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentType;

/**
 * Interface for arguments needed to create a device assignment.
 * 
 * @author Derek
 */
public interface IDeviceAssignmentCreateRequest extends IMetadataProvider {

	/**
	 * Get the unique device hardware id.
	 * 
	 * @return
	 */
	public String getDeviceHardwareId();

	/**
	 * Get assignment type.
	 * 
	 * @return
	 */
	public DeviceAssignmentType getAssignmentType();

	/**
	 * Get asset module id.
	 * 
	 * @return
	 */
	public String getAssetModuleId();

	/**
	 * Get assigned asset id.
	 * 
	 * @return
	 */
	public String getAssetId();
}