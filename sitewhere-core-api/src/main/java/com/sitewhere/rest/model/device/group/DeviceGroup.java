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
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.device.group.IDeviceGroup;

/**
 * Model object for a device group.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceGroup extends MetadataProviderEntity implements IDeviceGroup, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -4993194128786517276L;

    /** Unique device id */
    private UUID id;

    /** Unique token */
    private String token;

    /** Group name */
    private String name;

    /** Group description */
    private String description;

    /** Image URL */
    private String imageUrl;

    /** List of roles */
    private List<String> roles = new ArrayList<String>();

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroup#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.group.IDeviceGroup#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

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
     * @see com.sitewhere.spi.common.IAccessible#getImageUrl()
     */
    @Override
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
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