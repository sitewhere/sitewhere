/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.spi.device;

import java.util.Date;

import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Interface for a device that provides location information.
 * 
 * @author Derek
 */
public interface IDevice extends IMetadataProviderEntity {

	/**
	 * Get the asset id.
	 * 
	 * @return
	 */
	public String getAssetId();

	/**
	 * Get the unique hardware id of the device.
	 * 
	 * @return
	 */
	public String getHardwareId();

	/**
	 * Get device comments.
	 * 
	 * @return
	 */
	public String getComments();

	/**
	 * Get the create date.
	 * 
	 * @return
	 */
	public Date getCreatedDate();

	/**
	 * Get the current device assignment token if assigned.
	 * 
	 * @return
	 */
	public String getAssignmentToken();
}