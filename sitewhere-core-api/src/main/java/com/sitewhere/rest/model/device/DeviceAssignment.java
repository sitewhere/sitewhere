/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDeviceAssignment;

/**
 * Device assignment value object used for marshaling.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAssignment extends MetadataProviderEntity implements IDeviceAssignment, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = 4053925804888464375L;

    /** Unique device id */
    private UUID id;

    /** Unique assignment token */
    private String token;

    /** Device id */
    private UUID deviceId;

    /** Type of associated asset */
    private DeviceAssignmentType assignmentType;

    /** Asset reference */
    private IAssetReference assetReference;

    /** Id of site when assigned */
    private UUID siteId;

    /** Assignment status */
    private DeviceAssignmentStatus status;

    /** Assignment start date */
    private Date activeDate;

    /** Assignment end date */
    private Date releasedDate;

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getId()
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
     * @see com.sitewhere.spi.device.IDeviceAssignment#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignment#getAssignmentType()
     */
    @Override
    public DeviceAssignmentType getAssignmentType() {
	return assignmentType;
    }

    public void setAssignmentType(DeviceAssignmentType assignmentType) {
	this.assignmentType = assignmentType;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getAssetReference()
     */
    @Override
    public IAssetReference getAssetReference() {
	return assetReference;
    }

    public void setAssetReference(IAssetReference assetReference) {
	this.assetReference = assetReference;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getSiteId()
     */
    @Override
    public UUID getSiteId() {
	return siteId;
    }

    public void setSiteId(UUID siteId) {
	this.siteId = siteId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignment#getStatus()
     */
    @Override
    public DeviceAssignmentStatus getStatus() {
	return status;
    }

    public void setStatus(DeviceAssignmentStatus status) {
	this.status = status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignment#getActiveDate()
     */
    @JsonSerialize(using = JsonDateSerializer.class)
    @Override
    public Date getActiveDate() {
	return activeDate;
    }

    public void setActiveDate(Date activeDate) {
	this.activeDate = activeDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignment#getReleasedDate()
     */
    @JsonSerialize(using = JsonDateSerializer.class)
    @Override
    public Date getReleasedDate() {
	return releasedDate;
    }

    public void setReleasedDate(Date releasedDate) {
	this.releasedDate = releasedDate;
    }
}