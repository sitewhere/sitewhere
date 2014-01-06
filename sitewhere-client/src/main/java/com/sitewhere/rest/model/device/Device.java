/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.rest.model.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.device.IDevice;

/**
 * Model object for device information.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class Device extends MetadataProviderEntity implements IDevice {

	/** Asset id of device hardware */
	private String assetId;

	/** Asset name */
	private String assetName;
	
	/** Asset image url */
	private String assetImageUrl;

	/** Unique hardware id for device */
	private String hardwareId;

	/** Comments */
	private String comments;

	/** Asset representing device hardware */
	private HardwareAsset deviceAsset;

	/** Current device assignment */
	private DeviceAssignment assignment;

	/** Token for current assignment */
	private String assignmentToken;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDevice#getAssetId()
	 */
	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getAssetImageUrl() {
		return assetImageUrl;
	}

	public void setAssetImageUrl(String assetImageUrl) {
		this.assetImageUrl = assetImageUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDevice#getHardwareId()
	 */
	public String getHardwareId() {
		return hardwareId;
	}

	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDevice#getComments()
	 */
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public HardwareAsset getDeviceAsset() {
		return deviceAsset;
	}

	public void setDeviceAsset(HardwareAsset deviceAsset) {
		this.deviceAsset = deviceAsset;
	}

	public DeviceAssignment getAssignment() {
		return assignment;
	}

	public void setAssignment(DeviceAssignment assignment) {
		this.assignment = assignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDevice#getAssignmentToken()
	 */
	public String getAssignmentToken() {
		return assignmentToken;
	}

	public void setAssignmentToken(String assignmentToken) {
		this.assignmentToken = assignmentToken;
	}
}