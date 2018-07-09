/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import java.util.Date;
import java.util.UUID;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.device.IDeviceAlarm;

/**
 * Model object for device alarm information.
 * 
 * @author Derek
 */
public class DeviceAlarm extends MetadataProvider implements IDeviceAlarm {

    /** Serial version UID */
    private static final long serialVersionUID = -53003065627573710L;

    /** Unique alarm id */
    private UUID id;

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

    /** Alarm message */
    private String alarmMessage;

    /** Event that triggered alarm */
    private UUID triggeringEventId;

    /** Current alarm state */
    private DeviceAlarmState state;

    /** Date alarm was triggered */
    private Date triggeredDate;

    /** Date alarm was acknowledged */
    private Date acknowledgedDate;

    /** Date alarm was resolved */
    private Date resolvedDate;

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return deviceAssignmentId;
    }

    public void setDeviceAssignmentId(UUID deviceAssignmentId) {
	this.deviceAssignmentId = deviceAssignmentId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getCustomerId()
     */
    @Override
    public UUID getCustomerId() {
	return customerId;
    }

    public void setCustomerId(UUID customerId) {
	this.customerId = customerId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getAreaId()
     */
    @Override
    public UUID getAreaId() {
	return areaId;
    }

    public void setAreaId(UUID areaId) {
	this.areaId = areaId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getAssetId()
     */
    @Override
    public UUID getAssetId() {
	return assetId;
    }

    public void setAssetId(UUID assetId) {
	this.assetId = assetId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getAlarmMessage()
     */
    @Override
    public String getAlarmMessage() {
	return alarmMessage;
    }

    public void setAlarmMessage(String alarmMessage) {
	this.alarmMessage = alarmMessage;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getTriggeringEventId()
     */
    @Override
    public UUID getTriggeringEventId() {
	return triggeringEventId;
    }

    public void setTriggeringEventId(UUID triggeringEventId) {
	this.triggeringEventId = triggeringEventId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getState()
     */
    @Override
    public DeviceAlarmState getState() {
	return state;
    }

    public void setState(DeviceAlarmState state) {
	this.state = state;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getTriggeredDate()
     */
    @Override
    public Date getTriggeredDate() {
	return triggeredDate;
    }

    public void setTriggeredDate(Date triggeredDate) {
	this.triggeredDate = triggeredDate;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getAcknowledgedDate()
     */
    @Override
    public Date getAcknowledgedDate() {
	return acknowledgedDate;
    }

    public void setAcknowledgedDate(Date acknowledgedDate) {
	this.acknowledgedDate = acknowledgedDate;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAlarm#getResolvedDate()
     */
    @Override
    public Date getResolvedDate() {
	return resolvedDate;
    }

    public void setResolvedDate(Date resolvedDate) {
	this.resolvedDate = resolvedDate;
    }
}
