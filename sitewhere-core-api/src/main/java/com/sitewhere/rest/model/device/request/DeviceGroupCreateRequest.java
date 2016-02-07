/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;

/**
 * Holds fields needed to create a new device group.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceGroupCreateRequest implements IDeviceGroupCreateRequest, Serializable {

	/** Serialization version identifier */
	private static final long serialVersionUID = 1657559631108464556L;

	/** Unique token */
	private String token;

	/** Group name */
	private String name;

	/** Group description */
	private String description;

	/** List of roles */
	private List<String> roles;

	/** Metadata values */
	private Map<String, String> metadata;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceGroupCreateRequest#getToken()
	 */
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceGroupCreateRequest#getName()
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceGroupCreateRequest#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceGroupCreateRequest#getRoles()
	 */
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.request.IDeviceGroupCreateRequest#getMetadata()
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public static class Builder {

		/** Request being built */
		private DeviceGroupCreateRequest request = new DeviceGroupCreateRequest();

		public Builder(String token, String name) {
			request.setToken(token);
			request.setName(name);
		}

		public Builder withDescription(String description) {
			request.setDescription(description);
			return this;
		}

		public Builder withRole(String role) {
			if (request.getRoles() == null) {
				request.setRoles(new ArrayList<String>());
			}
			request.getRoles().add(role);
			return this;
		}

		public Builder metadata(String name, String value) {
			if (request.getMetadata() == null) {
				request.setMetadata(new HashMap<String, String>());
			}
			request.getMetadata().put(name, value);
			return this;
		}

		public DeviceGroupCreateRequest build() {
			return request;
		}
	}
}