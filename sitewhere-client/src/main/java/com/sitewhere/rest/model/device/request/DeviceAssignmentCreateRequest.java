/*
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;

/**
 * Holds fields needed to create a device assignment.
 * 
 * @author Derek Adams
 */
public class DeviceAssignmentCreateRequest extends MetadataProvider implements IDeviceAssignmentCreateRequest {

	/** Device hardware id */
	private String deviceHardwareId;

	/** Unique site token */
	private String siteToken;

	/** Type of assignment */
	private DeviceAssignmentType assignmentType;

	/** Asset module id */
	private String assetModuleId;

	/** Unique asset id */
	private String assetId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#getDeviceHardwareId
	 * ()
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
	 * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#getSiteToken()
	 */
	public String getSiteToken() {
		return siteToken;
	}

	public void setSiteToken(String siteToken) {
		this.siteToken = siteToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#getAssignmentType()
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
	 * @see
	 * com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#getAssetModuleId()
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
	 * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#getAssetId()
	 */
	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
}