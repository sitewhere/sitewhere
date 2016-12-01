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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;

/**
 * Holds fields needed to create a new device group element.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceGroupElementCreateRequest implements IDeviceGroupElementCreateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = 652319724175005277L;

    /** Element type */
    private GroupElementType type;

    /** Element id */
    private String elementId;

    /** List of roles for element */
    private List<String> roles = new ArrayList<String>();

    public GroupElementType getType() {
	return type;
    }

    public void setType(GroupElementType type) {
	this.type = type;
    }

    public String getElementId() {
	return elementId;
    }

    public void setElementId(String elementId) {
	this.elementId = elementId;
    }

    public List<String> getRoles() {
	return roles;
    }

    public void setRoles(List<String> roles) {
	this.roles = roles;
    }

    public static class Builder {

	/** Request being built */
	private DeviceGroupElementCreateRequest request = new DeviceGroupElementCreateRequest();

	public Builder(IDeviceGroupElement api) {
	    request.setElementId(api.getElementId());
	    request.setType(api.getType());
	    if (api.getRoles() != null) {
		request.setRoles(new ArrayList<String>());
		request.getRoles().addAll(api.getRoles());
	    }
	}

	public Builder(String id) {
	    request.setElementId(id);
	    request.setType(GroupElementType.Device);
	}

	public Builder withRole(String role) {
	    request.getRoles().add(role);
	    return this;
	}

	public Builder asGroup() {
	    request.setType(GroupElementType.Group);
	    return this;
	}

	public DeviceGroupElementCreateRequest build() {
	    return request;
	}
    }
}