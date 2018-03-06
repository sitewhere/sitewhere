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
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

    /** Device id (null if nested group supplied) */
    private UUID deviceId;

    /** Nested group id (null if device supplied) */
    private UUID nestedGroupId;

    /** List of roles for element */
    private List<String> roles = new ArrayList<String>();

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest#getDeviceId
     * ()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest#
     * getNestedGroupId()
     */
    @Override
    public UUID getNestedGroupId() {
	return nestedGroupId;
    }

    public void setNestedGroupId(UUID nestedGroupId) {
	this.nestedGroupId = nestedGroupId;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest#getRoles()
     */
    @Override
    public List<String> getRoles() {
	return roles;
    }

    public void setRoles(List<String> roles) {
	this.roles = roles;
    }

    public static class Builder {

	/** Request being built */
	private DeviceGroupElementCreateRequest request = new DeviceGroupElementCreateRequest();

	public Builder(UUID deviceId) {
	    request.setDeviceId(deviceId);
	}

	public Builder asGroup() {
	    request.setNestedGroupId(request.getDeviceId());
	    request.setDeviceId(null);
	    return this;
	}

	public Builder withRole(String role) {
	    request.getRoles().add(role);
	    return this;
	}

	public DeviceGroupElementCreateRequest build() {
	    return request;
	}
    }
}