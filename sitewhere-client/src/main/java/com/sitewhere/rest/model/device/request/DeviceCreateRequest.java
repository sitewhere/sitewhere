/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.List;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.spi.device.DeviceStatus;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;

/**
 * Holds fields needed to create a new device.
 * 
 * @author Derek Adams
 */
public class DeviceCreateRequest extends MetadataProvider implements IDeviceCreateRequest {

	/** Hardware id for new device */
	private String hardwareId;

	/** Device specification token */
	private String specificationToken;

	/** Parent hardware id (if nested) */
	private String parentHardwareId;

	/** Indicates whether parent hardware id should be removed */
	private boolean removeParentHardwareId = false;

	/** List of device element mappings */
	private List<DeviceElementMapping> deviceElementMappings;

	/** Comments */
	private String comments;

	/** Device status indicator */
	private DeviceStatus status;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getHardwareId()
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
	 * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getSpecificationToken()
	 */
	public String getSpecificationToken() {
		return specificationToken;
	}

	public void setSpecificationToken(String specificationToken) {
		this.specificationToken = specificationToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getParentHardwareId()
	 */
	public String getParentHardwareId() {
		return parentHardwareId;
	}

	public void setParentHardwareId(String parentHardwareId) {
		this.parentHardwareId = parentHardwareId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.request.IDeviceCreateRequest#isRemoveParentHardwareId()
	 */
	public boolean isRemoveParentHardwareId() {
		return removeParentHardwareId;
	}

	public void setRemoveParentHardwareId(boolean removeParentHardwareId) {
		this.removeParentHardwareId = removeParentHardwareId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.request.IDeviceCreateRequest#getDeviceElementMappings()
	 */
	@SuppressWarnings("unchecked")
	public List<IDeviceElementMapping> getDeviceElementMappings() {
		return (List<IDeviceElementMapping>) (List<? extends IDeviceElementMapping>) deviceElementMappings;
	}

	public void setDeviceElementMappings(List<DeviceElementMapping> deviceElementMappings) {
		this.deviceElementMappings = deviceElementMappings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getComments()
	 */
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getStatus()
	 */
	public DeviceStatus getStatus() {
		return status;
	}

	public void setStatus(DeviceStatus status) {
		this.status = status;
	}
}