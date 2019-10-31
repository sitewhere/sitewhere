/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.marshaling.MarshaledDevice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Configurable helper class that allows {@link Device} model objects to be
 * created from {@link IDevice} SPI objects.
 */
public class DeviceMarshalHelper {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(DeviceMarshalHelper.class);

    /** Tenant */
    private IDeviceManagement deviceManagement;

    /** Indicates whether device type information is to be included */
    private boolean includeDeviceType = true;

    /** Indicates whether device assignment information is to be copied */
    private boolean includeAssignment = false;

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
    public MarshaledDevice convert(IDevice source, IAssetManagement assetManagement) throws SiteWhereException {
	MarshaledDevice result = new MarshaledDevice();
	result.setDeviceTypeId(source.getDeviceTypeId());
	result.setActiveDeviceAssignmentIds(source.getActiveDeviceAssignmentIds());
	result.setParentDeviceId(source.getParentDeviceId());
	result.setStatus(source.getStatus());
	result.setComments(source.getComments());
	PersistentEntity.copy(source, result);

	// Copy device element mappings.
	for (IDeviceElementMapping mapping : source.getDeviceElementMappings()) {
	    DeviceElementMapping cnvMapping = DeviceElementMapping.copy(mapping);
	    if (isIncludeNested()) {
		IDevice device = getDeviceManagement().getDeviceByToken(mapping.getDeviceToken());
		cnvMapping.setDevice(getNestedHelper().convert(device, assetManagement));
	    }
	    result.getDeviceElementMappings().add(cnvMapping);
	}

	// Look up device type information.
	if ((source.getDeviceTypeId() != null) && (isIncludeDeviceType())) {
	    IDeviceType deviceType = getDeviceManagement().getDeviceType(source.getDeviceTypeId());
	    if (deviceType == null) {
		throw new SiteWhereException("Device references non-existent device type.");
	    }
	    if (isIncludeDeviceType()) {
		result.setDeviceType(getDeviceTypeHelper().convert(deviceType));
	    }
	}
	if ((source.getActiveDeviceAssignmentIds().size() > 0) && (isIncludeAssignment())) {
	    try {
		List<IDeviceAssignment> assignments = getDeviceManagement().getActiveDeviceAssignments(source.getId());
		List<DeviceAssignment> converted = new ArrayList<>();
		for (IDeviceAssignment assignment : assignments) {
		    converted.add(getAssignmentHelper().convert(assignment, assetManagement));
		}
		result.setActiveDeviceAssignments(converted);
	    } catch (SiteWhereException e) {
		LOGGER.warn("Device has token for non-existent assignment.");
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
	    assignmentHelper.setIncludeDevice(false);
	    assignmentHelper.setIncludeCustomer(true);
	    assignmentHelper.setIncludeArea(true);
	    assignmentHelper.setIncludeAsset(true);
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