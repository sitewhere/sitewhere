/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Model object for an event originating from a remote device.
 * 
 * @author dadams
 */
@JsonInclude(Include.NON_NULL)
public abstract class DeviceEvent extends MetadataProvider implements IDeviceEvent, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 3532362334243746084L;

    /** Unqiue id for event */
    private UUID id;

    /** Alternate (external) id for event */
    private String alternateId;

    /** Event type indicator */
    private DeviceEventType eventType;

    /** Device id */
    private UUID deviceId;

    /** Device assignment id */
    private UUID deviceAssignmentId;

    /** Customer id */
    private UUID customerId;

    /** Area id */
    private UUID areaId;

    /** Asset id */
    private UUID assetId;

    /** Date event occurred */
    private Date eventDate;

    /** Date event was received */
    private Date receivedDate;

    public DeviceEvent(DeviceEventType type) {
	this.eventType = type;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getId()
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
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAlternateId()
     */
    @Override
    public String getAlternateId() {
	return alternateId;
    }

    public void setAlternateId(String alternateId) {
	this.alternateId = alternateId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getEventType()
     */
    @Override
    public DeviceEventType getEventType() {
	return eventType;
    }

    public void setEventType(DeviceEventType eventType) {
	this.eventType = eventType;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return deviceAssignmentId;
    }

    public void setDeviceAssignmentId(UUID deviceAssignmentId) {
	this.deviceAssignmentId = deviceAssignmentId;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getCustomerId()
     */
    @Override
    public UUID getCustomerId() {
	return customerId;
    }

    public void setCustomerId(UUID customerId) {
	this.customerId = customerId;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAreaId()
     */
    @Override
    public UUID getAreaId() {
	return areaId;
    }

    public void setAreaId(UUID areaId) {
	this.areaId = areaId;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEvent#getAssetId()
     */
    @Override
    public UUID getAssetId() {
	return assetId;
    }

    public void setAssetId(UUID assetId) {
	this.assetId = assetId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceEvent#getEventDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getEventDate() {
	return eventDate;
    }

    public void setEventDate(Date eventDate) {
	this.eventDate = eventDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceEvent#getReceivedDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getReceivedDate() {
	return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
	this.receivedDate = receivedDate;
    }
}