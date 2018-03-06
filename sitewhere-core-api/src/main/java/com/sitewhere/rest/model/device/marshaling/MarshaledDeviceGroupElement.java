/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;

/**
 * Extends {@link DeviceGroupElement} to support fields that can be included on
 * REST calls.
 * 
 * @author Derek
 */
public class MarshaledDeviceGroupElement extends DeviceGroupElement {

    /** Serial version UID */
    private static final long serialVersionUID = 4423004146092498880L;

    /** Referenced device */
    private Device device;

    /** Referenced device group */
    private DeviceGroup deviceGroup;

    public Device getDevice() {
	return device;
    }

    public void setDevice(Device device) {
	this.device = device;
    }

    public DeviceGroup getDeviceGroup() {
	return deviceGroup;
    }

    public void setDeviceGroup(DeviceGroup deviceGroup) {
	this.deviceGroup = deviceGroup;
    }
}