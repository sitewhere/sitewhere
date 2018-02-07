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

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceType;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Configurable helper class that allows {@link DeviceType} model objects to be
 * created from {@link IDeviceType} SPI objects.
 * 
 * @author dadams
 */
public class DeviceTypeMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Device Management */
    private IDeviceManagement deviceManagement;

    /**
     * Indicates whether device type asset information is to be included
     */
    private boolean includeAsset = true;

    public DeviceTypeMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert a device type for marshaling.
     * 
     * @param source
     * @param manager
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDeviceType convert(IDeviceType source, IAssetResolver assetResolver) throws SiteWhereException {
	MarshaledDeviceType deviceType = new MarshaledDeviceType();
	MetadataProviderEntity.copy(source, deviceType);
	deviceType.setId(source.getId());
	deviceType.setToken(source.getToken());
	deviceType.setName(source.getName());
	deviceType.setAssetReference(source.getAssetReference());

	// Look up asset reference and handle asset not found.
	HardwareAsset asset = (HardwareAsset) assetResolver.getAssetModuleManagement()
		.getAsset(source.getAssetReference());
	if (asset == null) {
	    LOGGER.warn("Device specification has reference to non-existent asset.");
	    asset = new InvalidAsset();
	}

	deviceType.setAssetName(asset.getName());
	deviceType.setAssetImageUrl(asset.getImageUrl());
	if (isIncludeAsset()) {
	    deviceType.setAsset(asset);
	}
	deviceType.setContainerPolicy(source.getContainerPolicy());
	deviceType.setDeviceElementSchema((DeviceElementSchema) source.getDeviceElementSchema());
	return deviceType;
    }

    public boolean isIncludeAsset() {
	return includeAsset;
    }

    public DeviceTypeMarshalHelper setIncludeAsset(boolean includeAsset) {
	this.includeAsset = includeAsset;
	return this;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}