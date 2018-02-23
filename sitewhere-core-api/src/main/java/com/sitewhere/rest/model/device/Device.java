/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.element.IDeviceElementSchema;

/**
 * Model object for device information.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class Device extends MetadataProviderEntity implements IDevice, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -5409798557113797549L;

    /** Unique device id */
    private UUID id;

    /** Reference token */
    private String token;

    /** Device type id */
    private UUID deviceTypeId;

    /** Id for current assignment if assigned */
    private UUID deviceAssignmentId;

    /** Parent device id (if nested) */
    private UUID parentDeviceId;

    /** Mappings of {@link IDeviceElementSchema} paths to hardware ids */
    private List<DeviceElementMapping> deviceElementMappings = new ArrayList<DeviceElementMapping>();

    /** Comments */
    private String comments;

    /** Status indicator */
    private String status;

    /*
     * @see com.sitewhere.spi.device.IDevice#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see com.sitewhere.spi.device.IDevice#getDeviceTypeId()
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    /*
     * @see com.sitewhere.spi.device.IDevice#getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return deviceAssignmentId;
    }

    public void setDeviceAssignmentId(UUID deviceAssignmentId) {
	this.deviceAssignmentId = deviceAssignmentId;
    }

    /*
     * @see com.sitewhere.spi.device.IDevice#getParentDeviceId()
     */
    @Override
    public UUID getParentDeviceId() {
	return parentDeviceId;
    }

    public void setParentDeviceId(UUID parentDeviceId) {
	this.parentDeviceId = parentDeviceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDevice#getDeviceElementMappings()
     */
    @Override
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
     * @see com.sitewhere.spi.device.IDevice#getComments()
     */
    @Override
    public String getComments() {
	return comments;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDevice#getStatus()
     */
    @Override
    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }
}