/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.group;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.device.group.IDeviceGroup;

/**
 * Model object for a device group.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceGroup extends BrandedEntity implements IDeviceGroup {

    /** Serialization version identifier */
    private static final long serialVersionUID = -4993194128786517276L;

    /** Group name */
    private String name;

    /** Group description */
    private String description;

    /** List of roles */
    private List<String> roles = new ArrayList<String>();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.group.IDeviceGroup#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.group.IDeviceGroup#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.group.IDeviceGroup#getRoles()
     */
    public List<String> getRoles() {
	return roles;
    }

    public void setRoles(List<String> roles) {
	this.roles = roles;
    }
}