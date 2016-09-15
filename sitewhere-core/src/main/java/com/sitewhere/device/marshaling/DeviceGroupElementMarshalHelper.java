/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Configurable helper class that allows {@link DeviceGroupElement} model
 * objects to be created from {@link IDeviceGroupElement} SPI objects.
 * 
 * @author dadams
 */
public class DeviceGroupElementMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant */
    private ITenant tenant;

    /**
     * Indicates whether detailed device or device group information is to be
     * included
     */
    private boolean includeDetails = false;

    /** Helper class for enriching device information */
    private DeviceMarshalHelper deviceHelper;

    public DeviceGroupElementMarshalHelper(ITenant tenant) {
	this.tenant = tenant;
	this.deviceHelper = new DeviceMarshalHelper(tenant).setIncludeSpecification(true).setIncludeAsset(true)
		.setIncludeAssignment(true);
    }

    /**
     * Convert the SPI object to a model object for marshaling.
     * 
     * @param source
     * @param manager
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroupElement convert(IDeviceGroupElement source, IAssetModuleManager manager)
	    throws SiteWhereException {
	DeviceGroupElement result = new DeviceGroupElement();
	result.setGroupToken(source.getGroupToken());
	result.setIndex(source.getIndex());
	result.setType(source.getType());
	result.setElementId(source.getElementId());
	result.getRoles().addAll(source.getRoles());
	if (isIncludeDetails()) {
	    switch (source.getType()) {
	    case Device: {
		IDevice device = SiteWhere.getServer().getDeviceManagement(tenant)
			.getDeviceByHardwareId(source.getElementId());
		if (device != null) {
		    Device inflated = deviceHelper.convert(device, manager);
		    result.setDevice(inflated);
		} else {
		    LOGGER.warn("Group references invalid device: " + source.getElementId());
		}
		break;
	    }
	    case Group: {
		IDeviceGroup group = SiteWhere.getServer().getDeviceManagement(tenant)
			.getDeviceGroup(source.getElementId());
		if (group != null) {
		    DeviceGroup inflated = DeviceGroup.copy(group);
		    result.setDeviceGroup(inflated);
		} else {
		    LOGGER.warn("Group references invalid subgroup: " + source.getElementId());
		}
		break;
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
}