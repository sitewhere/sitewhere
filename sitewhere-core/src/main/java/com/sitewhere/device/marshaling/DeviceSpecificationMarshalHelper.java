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
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceSpecification;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;

/**
 * Configurable helper class that allows {@link DeviceSpecification} model
 * objects to be created from {@link IDeviceSpecification} SPI objects.
 * 
 * @author dadams
 */
public class DeviceSpecificationMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Device Management */
    private IDeviceManagement deviceManagement;

    /**
     * Indicates whether device specification asset information is to be included
     */
    private boolean includeAsset = true;

    public DeviceSpecificationMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert a device specification for marshaling.
     * 
     * @param source
     * @param manager
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDeviceSpecification convert(IDeviceSpecification source, IAssetResolver assetResolver)
	    throws SiteWhereException {
	MarshaledDeviceSpecification spec = new MarshaledDeviceSpecification();
	MetadataProviderEntity.copy(source, spec);
	spec.setToken(source.getToken());
	spec.setName(source.getName());
	spec.setAssetReference(source.getAssetReference());

	// Look up asset reference and handle asset not found.
	HardwareAsset asset = (HardwareAsset) assetResolver.getAssetModuleManagement()
		.getAsset(source.getAssetReference());
	if (asset == null) {
	    LOGGER.warn("Device specification has reference to non-existent asset.");
	    asset = new InvalidAsset();
	}

	spec.setAssetName(asset.getName());
	spec.setAssetImageUrl(asset.getImageUrl());
	if (isIncludeAsset()) {
	    spec.setAsset(asset);
	}
	spec.setContainerPolicy(source.getContainerPolicy());
	spec.setDeviceElementSchema((DeviceElementSchema) source.getDeviceElementSchema());
	return spec;
    }

    public boolean isIncludeAsset() {
	return includeAsset;
    }

    public DeviceSpecificationMarshalHelper setIncludeAsset(boolean includeAsset) {
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