/*
 * DeviceElementMapping.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import com.sitewhere.spi.device.IDeviceElementMapping;

/**
 * Model implementation of {@link IDeviceElementMapping}.
 * 
 * @author Derek
 */
public class DeviceElementMapping implements IDeviceElementMapping {

	/** Path in device element schema being mapped */
	private String deviceElementSchemaPath;

	/** Hardware id of device being mapped */
	private String hardwareId;

	public DeviceElementMapping() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceElementMapping#getDeviceElementSchemaPath()
	 */
	public String getDeviceElementSchemaPath() {
		return deviceElementSchemaPath;
	}

	public void setDeviceElementSchemaPath(String deviceElementSchemaPath) {
		this.deviceElementSchemaPath = deviceElementSchemaPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceElementMapping#getHardwareId()
	 */
	public String getHardwareId() {
		return hardwareId;
	}

	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}

	public static DeviceElementMapping copy(IDeviceElementMapping input) {
		DeviceElementMapping result = new DeviceElementMapping();
		result.setDeviceElementSchemaPath(input.getDeviceElementSchemaPath());
		result.setHardwareId(input.getHardwareId());
		return result;
	}
}
