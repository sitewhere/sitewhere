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
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceAssignment;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.ISite;

/**
 * Configurable helper class that allows DeviceAssignment model objects to be
 * created from IDeviceAssignment SPI objects.
 * 
 * @author dadams
 */
public class DeviceAssignmentMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Indicates whether device asset information is to be included */
    private boolean includeAsset = true;

    /** Indicates whether to include device information */
    private boolean includeDevice = false;

    /** Indicates whether to include site information */
    private boolean includeSite = false;

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Used to control marshaling of devices */
    private DeviceMarshalHelper deviceHelper;

    public DeviceAssignmentMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert the SPI object into a model object for marshaling.
     * 
     * @param source
     * @param manager
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDeviceAssignment convert(IDeviceAssignment source, IAssetResolver assetResolver)
	    throws SiteWhereException {
	MarshaledDeviceAssignment result = new MarshaledDeviceAssignment();
	result.setToken(source.getToken());
	result.setActiveDate(source.getActiveDate());
	result.setReleasedDate(source.getReleasedDate());
	result.setStatus(source.getStatus());
	result.setAssignmentType(source.getAssignmentType());
	result.setAssetModuleId(source.getAssetModuleId());
	result.setAssetId(source.getAssetId());
	MetadataProviderEntity.copy(source, result);
	if (source.getAssignmentType() != DeviceAssignmentType.Unassociated) {
	    IAsset asset = assetResolver.getAssetModuleManagement().getAssetById(source.getAssetModuleId(),
		    source.getAssetId());

	    // Handle case where referenced asset is not found.
	    if (asset == null) {
		LOGGER.warn("Device assignment has reference to non-existent asset.");
		asset = new InvalidAsset();
	    }
	    result.setAssetName(asset.getName());
	    result.setAssetImageUrl(asset.getImageUrl());
	    if (isIncludeAsset()) {
		if (asset instanceof HardwareAsset) {
		    result.setAssociatedHardware((HardwareAsset) asset);
		} else if (asset instanceof PersonAsset) {
		    result.setAssociatedPerson((PersonAsset) asset);
		} else if (asset instanceof LocationAsset) {
		    result.setAssociatedLocation((LocationAsset) asset);
		}
	    }
	}
	result.setSiteToken(source.getSiteToken());
	if (isIncludeSite()) {
	    ISite site = getDeviceManagement().getSiteByToken(source.getSiteToken());
	    result.setSite(Site.copy(site));
	}
	result.setDeviceHardwareId(source.getDeviceHardwareId());
	if (isIncludeDevice()) {
	    IDevice device = getDeviceManagement().getDeviceByHardwareId(source.getDeviceHardwareId());
	    if (device != null) {
		result.setDevice(getDeviceHelper().convert(device, assetResolver));
	    } else {
		LOGGER.error("Assignment references invalid hardware id.");
	    }
	}
	return result;
    }

    /**
     * Get the helper for marshaling device information.
     * 
     * @return
     */
    protected DeviceMarshalHelper getDeviceHelper() {
	if (deviceHelper == null) {
	    deviceHelper = new DeviceMarshalHelper(getDeviceManagement());
	    deviceHelper.setIncludeAsset(false);
	    deviceHelper.setIncludeAssignment(false);
	    deviceHelper.setIncludeSpecification(false);
	}
	return deviceHelper;
    }

    public boolean isIncludeAsset() {
	return includeAsset;
    }

    public DeviceAssignmentMarshalHelper setIncludeAsset(boolean includeAsset) {
	this.includeAsset = includeAsset;
	return this;
    }

    public boolean isIncludeDevice() {
	return includeDevice;
    }

    public DeviceAssignmentMarshalHelper setIncludeDevice(boolean includeDevice) {
	this.includeDevice = includeDevice;
	return this;
    }

    public boolean isIncludeSite() {
	return includeSite;
    }

    public DeviceAssignmentMarshalHelper setIncludeSite(boolean includeSite) {
	this.includeSite = includeSite;
	return this;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}