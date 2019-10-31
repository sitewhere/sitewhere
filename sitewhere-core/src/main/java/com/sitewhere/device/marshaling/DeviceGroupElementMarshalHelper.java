/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceGroupElement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;

/**
 * Configurable helper class that allows {@link DeviceGroupElement} model
 * objects to be created from {@link IDeviceGroupElement} SPI objects.
 */
public class DeviceGroupElementMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceGroupElementMarshalHelper.class);

    /** Device Management */
    private IDeviceManagement deviceManagement;

    /**
     * Indicates whether detailed device or device group information is to be
     * included
     */
    private boolean includeDetails = false;

    /** Helper class for enriching device information */
    private DeviceMarshalHelper deviceHelper;

    /** Helper class for enriching group information */
    private DeviceGroupMarshalHelper groupHelper;

    public DeviceGroupElementMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
	this.deviceHelper = new DeviceMarshalHelper(deviceManagement).setIncludeDeviceType(true)
		.setIncludeAssignment(true);
	this.groupHelper = new DeviceGroupMarshalHelper();
    }

    /**
     * Convert the SPI object to a model object for marshaling.
     * 
     * @param source
     * @param manager
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDeviceGroupElement convert(IDeviceGroupElement source, IAssetManagement assetManagement)
	    throws SiteWhereException {
	MarshaledDeviceGroupElement result = new MarshaledDeviceGroupElement();
	result.setId(source.getId());
	result.setGroupId(source.getGroupId());
	result.setDeviceId(source.getDeviceId());
	result.setNestedGroupId(source.getNestedGroupId());
	result.getRoles().addAll(source.getRoles());
	if (isIncludeDetails()) {
	    if (source.getDeviceId() != null) {
		IDevice device = getDeviceManagement().getDevice(source.getDeviceId());
		if (device != null) {
		    Device inflated = getDeviceHelper().convert(device, assetManagement);
		    result.setDevice(inflated);
		} else {
		    LOGGER.warn("Group references invalid device: " + source.getDeviceId());
		}
	    } else if (source.getNestedGroupId() != null) {
		IDeviceGroup group = getDeviceManagement().getDeviceGroup(source.getNestedGroupId());
		if (group != null) {
		    DeviceGroup inflated = getGroupHelper().convert(group);
		    result.setDeviceGroup(inflated);
		} else {
		    LOGGER.warn("Group references invalid nested group: " + source.getNestedGroupId());
		}
	    }
	}
	return result;
    }

    public boolean isIncludeDetails() {
	return includeDetails;
    }

    public DeviceGroupElementMarshalHelper setIncludeDetails(boolean includeDetails) {
	this.includeDetails = includeDetails;
	return this;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public DeviceMarshalHelper getDeviceHelper() {
	return deviceHelper;
    }

    public void setDeviceHelper(DeviceMarshalHelper deviceHelper) {
	this.deviceHelper = deviceHelper;
    }

    public DeviceGroupMarshalHelper getGroupHelper() {
	return groupHelper;
    }

    public void setGroupHelper(DeviceGroupMarshalHelper groupHelper) {
	this.groupHelper = groupHelper;
    }
}