/*
 * IDeviceSpecification.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Specifies details about a given type of device.
 * 
 * @author Derek
 */
public interface IDeviceSpecification extends IMetadataProviderEntity {

	/**
	 * Get unique device specification token.
	 * 
	 * @return
	 */
	public String getToken();

	/**
	 * Get name that describes specification.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get asset module id.
	 * 
	 * @return
	 */
	public String getAssetModuleId();

	/**
	 * Get id for specification asset type.
	 * 
	 * @return
	 */
	public String getAssetId();
}