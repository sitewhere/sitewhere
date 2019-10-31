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

import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Configurable helper class that allows {@link DeviceType} model objects to be
 * created from {@link IDeviceType} SPI objects.
 */
public class DeviceTypeMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceTypeMarshalHelper.class);

    /** Device Management */
    private IDeviceManagement deviceManagement;

    public DeviceTypeMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert a device type for marshaling.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public DeviceType convert(IDeviceType source) throws SiteWhereException {
	DeviceType deviceType = new DeviceType();
	deviceType.setName(source.getName());
	deviceType.setDescription(source.getDescription());
	deviceType.setContainerPolicy(source.getContainerPolicy());
	deviceType.setDeviceElementSchema((DeviceElementSchema) source.getDeviceElementSchema());
	BrandedEntity.copy(source, deviceType);
	return deviceType;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}