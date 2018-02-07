/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.util.Map;
import java.util.UUID;

import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.event.IDeviceEventContext;

/**
 * Model object for device event context information. This information augments
 * the existing event information to provide extra context for processing
 * operations.
 * 
 * @author Derek
 */
public class DeviceEventContext implements IDeviceEventContext {

    /** Device id */
    private UUID deviceId;

    /** Device type id */
    private UUID deviceTypeId;

    /** Parent device id */
    private UUID parentDeviceId;

    /** Device status */
    private String deviceStatus;

    /** Device metadata */
    private Map<String, String> deviceMetadata;

    /** Device assignment status */
    private DeviceAssignmentStatus assignmentStatus;

    /** Device assignment metadata */
    private Map<String, String> assignmentMetadata;

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getDeviceTypeId()
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getParentDeviceId()
     */
    @Override
    public UUID getParentDeviceId() {
	return parentDeviceId;
    }

    public void setParentDeviceId(UUID parentDeviceId) {
	this.parentDeviceId = parentDeviceId;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getDeviceStatus()
     */
    @Override
    public String getDeviceStatus() {
	return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
	this.deviceStatus = deviceStatus;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getDeviceMetadata()
     */
    @Override
    public Map<String, String> getDeviceMetadata() {
	return deviceMetadata;
    }

    public void setDeviceMetadata(Map<String, String> deviceMetadata) {
	this.deviceMetadata = deviceMetadata;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getAssignmentStatus()
     */
    @Override
    public DeviceAssignmentStatus getAssignmentStatus() {
	return assignmentStatus;
    }

    public void setAssignmentStatus(DeviceAssignmentStatus assignmentStatus) {
	this.assignmentStatus = assignmentStatus;
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventContext#getAssignmentMetadata()
     */
    @Override
    public Map<String, String> getAssignmentMetadata() {
	return assignmentMetadata;
    }

    public void setAssignmentMetadata(Map<String, String> assignmentMetadata) {
	this.assignmentMetadata = assignmentMetadata;
    }
}