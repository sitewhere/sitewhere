/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;

/**
 * Holds fields needed to create a device assignment.
 * 
 * @author Derek Adams
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAssignmentCreateRequest implements IDeviceAssignmentCreateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -6880578458870122016L;

    /** Device hardware id */
    private String deviceHardwareId;

    /** Type of assignment */
    private DeviceAssignmentType assignmentType;

    /** Asset module id */
    private String assetModuleId;

    /** Unique asset id */
    private String assetId;

    /** Metadata values */
    private Map<String, String> metadata;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#
     * getDeviceHardwareId ()
     */
    public String getDeviceHardwareId() {
	return deviceHardwareId;
    }

    public void setDeviceHardwareId(String deviceHardwareId) {
	this.deviceHardwareId = deviceHardwareId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#
     * getAssignmentType()
     */
    public DeviceAssignmentType getAssignmentType() {
	return assignmentType;
    }

    public void setAssignmentType(DeviceAssignmentType assignmentType) {
	this.assignmentType = assignmentType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#
     * getAssetModuleId()
     */
    public String getAssetModuleId() {
	return assetModuleId;
    }

    public void setAssetModuleId(String assetModuleId) {
	this.assetModuleId = assetModuleId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#
     * getAssetId()
     */
    public String getAssetId() {
	return assetId;
    }

    public void setAssetId(String assetId) {
	this.assetId = assetId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#
     * getMetadata()
     */
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static class Builder {

	/** Request being built */
	private DeviceAssignmentCreateRequest request = new DeviceAssignmentCreateRequest();

	public Builder(String hardwareId, String assetModuleId, String assetId) {
	    request.setDeviceHardwareId(hardwareId);
	    request.setAssetModuleId(assetModuleId);
	    request.setAssetId(assetId);
	    request.setAssignmentType(DeviceAssignmentType.Associated);
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public DeviceAssignmentCreateRequest build() {
	    return request;
	}
    }
}