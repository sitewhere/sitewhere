/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration.router.devicetypemapping;

/**
 * Data structure for a device type mapping.
 */
public class DeviceTypeMapping {

    /** Device type token */
    private String deviceTypeToken;

    /** Destination id */
    private String destinationId;

    public String getDeviceTypeToken() {
	return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
	this.deviceTypeToken = deviceTypeToken;
    }

    public String getDestinationId() {
	return destinationId;
    }

    public void setDestinationId(String destinationId) {
	this.destinationId = destinationId;
    }
}
