/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceElementMapping;

/**
 * Model implementation of {@link IDeviceElementMapping}.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceElementMapping implements IDeviceElementMapping {

    /** Serialization version identifier */
    private static final long serialVersionUID = 2668063520841302094L;

    /** Path in device element schema being mapped */
    private String deviceElementSchemaPath;

    /** Token of device being mapped */
    private String deviceToken;

    /** FIELDS BELOW DEPEND ON MARSHALING PARAMETERS */

    /** Device info if populated by marshaller */
    private IDevice device;

    public DeviceElementMapping() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceElementMapping#getDeviceElementSchemaPath ()
     */
    @Override
    public String getDeviceElementSchemaPath() {
	return deviceElementSchemaPath;
    }

    public void setDeviceElementSchemaPath(String deviceElementSchemaPath) {
	this.deviceElementSchemaPath = deviceElementSchemaPath;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceElementMapping#getDeviceToken()
     */
    @Override
    public String getDeviceToken() {
	return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    public IDevice getDevice() {
	return device;
    }

    public void setDevice(IDevice device) {
	this.device = device;
    }

    public static DeviceElementMapping copy(IDeviceElementMapping input) {
	DeviceElementMapping result = new DeviceElementMapping();
	result.setDeviceElementSchemaPath(input.getDeviceElementSchemaPath());
	result.setDeviceToken(input.getDeviceToken());
	return result;
    }
}