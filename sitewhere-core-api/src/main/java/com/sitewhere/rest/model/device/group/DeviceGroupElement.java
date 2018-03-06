/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;

/**
 * Model object for an element in an {@link IDeviceGroup}.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceGroupElement implements IDeviceGroupElement, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -5565956152579362877L;

    /** Unqiue id */
    private UUID id;

    /** Parent group id */
    private UUID groupId;

    /** Device id (null if nested group id specified) */
    private UUID deviceId;

    /** Nested group id (null if device id specified) */
    private UUID nestedGroupId;

    /** List of roles for the element */
    private List<String> roles = new ArrayList<String>();

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getGroupId()
     */
    @Override
    public UUID getGroupId() {
	return groupId;
    }

    public void setGroupId(UUID groupId) {
	this.groupId = groupId;
    }

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getNestedGroupId()
     */
    @Override
    public UUID getNestedGroupId() {
	return nestedGroupId;
    }

    public void setNestedGroupId(UUID nestedGroupId) {
	this.nestedGroupId = nestedGroupId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getRoles()
     */
    @Override
    public List<String> getRoles() {
	return roles;
    }

    public void setRoles(List<String> roles) {
	this.roles = roles;
    }
}