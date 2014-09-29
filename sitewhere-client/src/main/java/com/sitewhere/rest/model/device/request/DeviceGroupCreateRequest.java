/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;

/**
 * Holds fields needed to create a new device group.
 * 
 * @author Derek
 */
public class DeviceGroupCreateRequest extends MetadataProvider implements IDeviceGroupCreateRequest {

	/** Unique token */
	private String token;

	/** Group name */
	private String name;

	/** Group description */
	private String description;

	/** List of roles */
	private List<String> roles = new ArrayList<String>();

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}