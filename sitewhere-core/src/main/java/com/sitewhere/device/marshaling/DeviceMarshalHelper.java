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
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.marshaling.MarshaledDevice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.ISite;

/**
 * Configurable helper class that allows {@link Device} model objects to be
 * created from {@link IDevice} SPI objects.
 * 
 * @author dadams
 */
public class DeviceMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant */
    private IDeviceManagement deviceManagement;

    /** Indicates whether device type asset information is to be included */
    private boolean includeAsset = true;

    /** Indicates whether device type information is to be included */
    private boolean includeDeviceType = true;

    /** Indicates whether device assignment information is to be copied */
    private boolean includeAssignment = false;

    /** Indicates whether site information is to be copied */
    private boolean includeSite = false;

    /**
     * Indicates whether device element mappings should include device details
     */
    private boolean includeNested = false;

    /** Helper for marshaling device type information */
    private DeviceTypeMarshalHelper deviceTypeHelper;

    /** Helper for marshaling device assignement information */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    /** Helper for marshaling nested devices */
    private DeviceMarshalHelper nestedHelper;

    public DeviceMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert an IDevice SPI object into a model object for marshaling.
     * 
     * @param source
     * @param manager
     * @return
     * @throws SiteWhereException
     */
    public MarshaledDevice convert(IDevice source, IAssetResolver assetResolver) throws SiteWhereException {
	MarshaledDevice result = new MarshaledDevice();
	result.setId(source.getId());
	result.setHardwareId(source.getHardwareId());
	result.setSiteId(source.getSiteId());
	result.setDeviceTypeId(source.getDeviceTypeId());
	result.setDeviceAssignmentId(source.getDeviceAssignmentId());
	result.setParentDeviceId(source.getParentDeviceId());
	result.setComments(source.getComments());
	MetadataProviderEntity.copy(source, result);

	// Copy device element mappings.
	for (IDeviceElementMapping mapping : source.getDeviceElementMappings()) {
	    DeviceElementMapping cnvMapping = DeviceElementMapping.copy(mapping);
	    if (isIncludeNested()) {
		IDevice device = getDeviceManagement().getDeviceByHardwareId(mapping.getHardwareId());
		cnvMapping.setDevice(getNestedHelper().convert(device, assetResolver));
	    }
	    result.getDeviceElementMappings().add(cnvMapping);
	}

	// Look up device type information.
	if (source.getDeviceTypeId() != null) {
	    IDeviceType deviceType = getDeviceManagement().getDeviceType(source.getDeviceTypeId());
	    if (deviceType == null) {
		throw new SiteWhereException("Device references non-existent device type.");
	    }
	    if (isIncludeDeviceType()) {
		result.setDeviceType(getDeviceTypeHelper().convert(deviceType, assetResolver));
	    } else {
		HardwareAsset asset = (HardwareAsset) assetResolver.getAssetModuleManagement()
			.getAsset(deviceType.getAssetReference());
		if (asset != null) {
		    result.setAssetId(asset.getId());
		    result.setAssetName(asset.getName());
		    result.setAssetImageUrl(asset.getImageUrl());
		} else {
		    throw new SiteWhereException("Specification references non-existent asset.");
		}
	    }
	}
	if ((source.getDeviceAssignmentId() != null) && (isIncludeAssignment())) {
	    try {
		IDeviceAssignment assignment = getDeviceManagement()
			.getDeviceAssignment(source.getDeviceAssignmentId());
		if (assignment == null) {
		    throw new SiteWhereException("Device contains an invalid assignment reference.");
		}
		result.setAssignment(getAssignmentHelper().convert(assignment, assetResolver));
	    } catch (SiteWhereException e) {
		LOGGER.warn("Device has token for non-existent assignment.");
	    }
	}
	if ((source.getSiteId() != null) && (isIncludeSite())) {
	    if (includeSite) {
		ISite site = getDeviceManagement().getSite(source.getSiteId());
		if (site == null) {
		    throw new SiteWhereException("Device contains an invalid site reference.");
		}
		result.setSite((Site) site);
	    }
	}
	return result;
    }

    /**
     * Get helper class for marshaling device types .
     * 
     * @return
     */
    protected DeviceTypeMarshalHelper getDeviceTypeHelper() {
	if (deviceTypeHelper == null) {
	    deviceTypeHelper = new DeviceTypeMarshalHelper(getDeviceManagement());
	    deviceTypeHelper.setIncludeAsset(isIncludeAsset());
	}
	return deviceTypeHelper;
    }

    /**
     * Get helper class for marshaling assignment.
     * 
     * @return
     */
    protected DeviceAssignmentMarshalHelper getAssignmentHelper() {
	if (assignmentHelper == null) {
	    assignmentHelper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	    assignmentHelper.setIncludeAsset(false);
	    assignmentHelper.setIncludeDevice(false);
	    assignmentHelper.setIncludeSite(false);
	}
	return assignmentHelper;
    }

    /**
     * Get helper class for marshaling nested devices.
     * 
     * @return
     */
    protected DeviceMarshalHelper getNestedHelper() {
	if (nestedHelper == null) {
	    nestedHelper = new DeviceMarshalHelper(getDeviceManagement());
	}
	return nestedHelper;
    }

    public boolean isIncludeAsset() {
	return includeAsset;
    }

    public DeviceMarshalHelper setIncludeAsset(boolean includeAsset) {
	this.includeAsset = includeAsset;
	return this;
    }

    public boolean isIncludeDeviceType() {
	return includeDeviceType;
    }

    public DeviceMarshalHelper setIncludeDeviceType(boolean includeDeviceType) {
	this.includeDeviceType = includeDeviceType;
	return this;
    }

    public boolean isIncludeAssignment() {
	return includeAssignment;
    }

    public DeviceMarshalHelper setIncludeAssignment(boolean includeAssignment) {
	this.includeAssignment = includeAssignment;
	return this;
    }

    public boolean isIncludeSite() {
	return includeSite;
    }

    public void setIncludeSite(boolean includeSite) {
	this.includeSite = includeSite;
    }

    public boolean isIncludeNested() {
	return includeNested;
    }

    public void setIncludeNested(boolean includeNested) {
	this.includeNested = includeNested;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}