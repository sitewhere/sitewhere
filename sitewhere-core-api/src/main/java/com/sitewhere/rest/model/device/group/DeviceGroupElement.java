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
import com.sitewhere.spi.device.group.GroupElementType;
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

    /** Parent group id */
    private UUID groupId;

    /** Element index */
    private Long index;

    /** Element type */
    private GroupElementType type;

    /** Element type */
    private UUID elementId;

    /** List of roles for the element */
    private List<String> roles = new ArrayList<String>();

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
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getIndex()
     */
    @Override
    public Long getIndex() {
	return index;
    }

    public void setIndex(Long index) {
	this.index = index;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getType()
     */
    @Override
    public GroupElementType getType() {
	return type;
    }

    public void setType(GroupElementType type) {
	this.type = type;
    }

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getElementId()
     */
    @Override
    public UUID getElementId() {
	return elementId;
    }

    public void setElementId(UUID elementId) {
	this.elementId = elementId;
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