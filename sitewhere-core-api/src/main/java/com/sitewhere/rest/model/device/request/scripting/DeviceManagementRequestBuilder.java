/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request.scripting;

import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;

/**
 * Builder that supports creating device management entities.
 * 
 * @author Derek
 */
public class DeviceManagementRequestBuilder {

	/** Device management implementation */
	private IDeviceManagement deviceManagement;

	public DeviceManagementRequestBuilder(IDeviceManagement deviceManagement) {
		this.deviceManagement = deviceManagement;
	}

	public SiteCreateRequest.Builder newSite(String token, String name, String description, String imageUrl) {
		return new SiteCreateRequest.Builder(token, name, description, imageUrl);
	}

	public void persist(SiteCreateRequest.Builder builder) throws SiteWhereException {
		getDeviceManagement().createSite(builder.build());
	}

	public DeviceCreateRequest.Builder newDevice(String siteToken, String specificationToken,
			String hardwareId) {
		return new DeviceCreateRequest.Builder(siteToken, specificationToken, hardwareId);
	}

	public void persist(DeviceCreateRequest.Builder builder) throws SiteWhereException {
		getDeviceManagement().createDevice(builder.build());
	}

	public IDeviceManagement getDeviceManagement() {
		return deviceManagement;
	}

	public void setDeviceManagement(IDeviceManagement deviceManagement) {
		this.deviceManagement = deviceManagement;
	}
}