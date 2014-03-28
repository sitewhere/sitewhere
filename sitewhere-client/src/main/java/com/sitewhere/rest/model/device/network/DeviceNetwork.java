/*
 * DeviceNetwork.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.network;

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.device.network.IDeviceNetwork;

/**
 * Model object for a device network.
 * 
 * @author Derek
 */
public class DeviceNetwork extends MetadataProviderEntity implements IDeviceNetwork {

	/** Unique token */
	private String token;

	/** Network name */
	private String name;

	/** Network description */
	private String description;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.network.IDeviceNetwork#getToken()
	 */
	@Override
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.network.IDeviceNetwork#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.network.IDeviceNetwork#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static DeviceNetwork copy(IDeviceNetwork input) {
		DeviceNetwork result = new DeviceNetwork();
		result.setToken(input.getToken());
		result.setName(input.getName());
		result.setDescription(input.getDescription());
		MetadataProviderEntity.copy(input, result);
		return result;
	}
}