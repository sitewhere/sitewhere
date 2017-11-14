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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
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

    /** Unique assignment token */
    private String token;

    /** Device hardware id */
    private String deviceHardwareId;

    /** Type of associated asset */
    private DeviceAssignmentType assignmentType;

    /** Id of asset module */
    private String assetModuleId;

    /** Id of associated asset */
    private String assetId;

    /** Site token */
    private String siteToken;

    /** Assignment status */
    private DeviceAssignmentStatus status;

    /** Assignment start date */
    private Date activeDate;

    /** Assignment end date */
    private Date releasedDate;

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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignment#getDeviceHardwareId()
     */
    @Override
    public String getDeviceHardwareId() {
	return deviceHardwareId;
    }

    public void setDeviceHardwareId(String deviceHardwareId) {
	this.deviceHardwareId = deviceHardwareId;
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignment#getAssetModuleId()
     */
    public String getAssetModuleId() {
	return assetModuleId;
    }

    public void setAssetModuleId(String assetModuleId) {
	this.assetModuleId = assetModuleId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignment#getAssetId()
     */
    @Override
    public String getAssetId() {
	return assetId;
    }

    public void setAssetId(String assetId) {
	this.assetId = assetId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignment#getSiteToken()
     */
    @Override
    public String getSiteToken() {
	return siteToken;
    }

    public void setSiteToken(String siteToken) {
	this.siteToken = siteToken;
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