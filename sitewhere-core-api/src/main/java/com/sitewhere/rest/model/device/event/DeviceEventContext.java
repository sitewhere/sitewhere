/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.util.Map;

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

    /** Hardware id */
    private String hardwareId;

    /** Device specification token */
    private String specificationToken;

    /** Parent device hardware id */
    private String parentHardwareId;

    /** Device status */
    private String deviceStatus;

    /** Device metadata */
    private Map<String, String> deviceMetadata;

    /** Device assignment status */
    private DeviceAssignmentStatus assignmentStatus;

    /** Device assignment metadata */
    private Map<String, String> assignmentMetadata;

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getHardwareId()
     */
    public String getHardwareId() {
	return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
	this.hardwareId = hardwareId;
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventContext#getSpecificationToken()
     */
    public String getSpecificationToken() {
	return specificationToken;
    }

    public void setSpecificationToken(String specificationToken) {
	this.specificationToken = specificationToken;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getParentHardwareId()
     */
    public String getParentHardwareId() {
	return parentHardwareId;
    }

    public void setParentHardwareId(String parentHardwareId) {
	this.parentHardwareId = parentHardwareId;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getDeviceStatus()
     */
    public String getDeviceStatus() {
	return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
	this.deviceStatus = deviceStatus;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getDeviceMetadata()
     */
    public Map<String, String> getDeviceMetadata() {
	return deviceMetadata;
    }

    public void setDeviceMetadata(Map<String, String> deviceMetadata) {
	this.deviceMetadata = deviceMetadata;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventContext#getAssignmentStatus()
     */
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
    public Map<String, String> getAssignmentMetadata() {
	return assignmentMetadata;
    }

    public void setAssignmentMetadata(Map<String, String> assignmentMetadata) {
	this.assignmentMetadata = assignmentMetadata;
    }
}