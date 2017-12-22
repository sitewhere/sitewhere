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
import com.sitewhere.rest.model.asset.AssetReference;
import com.sitewhere.spi.asset.IAssetReference;
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

    /** Token */
    private String token;

    /** Device hardware id */
    private String deviceHardwareId;

    /** Type of assignment */
    private DeviceAssignmentType assignmentType;

    /** Asset reference */
    private IAssetReference assetReference;

    /** Metadata values */
    private Map<String, String> metadata;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#getToken( )
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#
     * getDeviceHardwareId ()
     */
    @Override
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
    @Override
    public DeviceAssignmentType getAssignmentType() {
	return assignmentType;
    }

    public void setAssignmentType(DeviceAssignmentType assignmentType) {
	this.assignmentType = assignmentType;
    }

    /*
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#
     * getAssetReference()
     */
    @Override
    public IAssetReference getAssetReference() {
	return assetReference;
    }

    public void setAssetReference(IAssetReference assetReference) {
	this.assetReference = assetReference;
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

	// public Builder(IDeviceAssignment api) {
	// request.setToken(api.getToken());
	// request.setDeviceHardwareId(api.getDeviceHardwareId());
	// request.setAssetReference(api.getAssetReference());
	// request.setAssignmentType(api.getAssignmentType());
	// if (api.getMetadata() != null) {
	// request.setMetadata(new HashMap<String, String>());
	// request.getMetadata().putAll(api.getMetadata());
	// }
	// }

	public Builder(String hardwareId, String moduleId, String assetId) {
	    request.setDeviceHardwareId(hardwareId);
	    request.setAssetReference(new AssetReference.Builder(moduleId, assetId).build());
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