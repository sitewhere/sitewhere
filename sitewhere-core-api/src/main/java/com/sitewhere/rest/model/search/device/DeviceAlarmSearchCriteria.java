/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.device;

import java.util.UUID;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria;

/**
 * Search criteria for device alarms.
 * 
 * @author Derek
 */
public class DeviceAlarmSearchCriteria extends SearchCriteria implements IDeviceAlarmSearchCriteria {

    /** Filter by device */
    private UUID deviceId;

    /** Filter by device assignment */
    private UUID deviceAssignmentId;

    /** Filter by customer */
    private UUID customerId;

    /** Filter by area */
    private UUID areaId;

    /** Filter by asset */
    private UUID assetId;

    /** Filter by triggering event id */
    private UUID triggeringEventId;

    /** Filter by state */
    private DeviceAlarmState state;

    public DeviceAlarmSearchCriteria() {
    }

    public DeviceAlarmSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria#
     * getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return deviceAssignmentId;
    }

    public void setDeviceAssignmentId(UUID deviceAssignmentId) {
	this.deviceAssignmentId = deviceAssignmentId;
    }

    /*
     * @see
     * com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria#getCustomerId()
     */
    @Override
    public UUID getCustomerId() {
	return customerId;
    }

    public void setCustomerId(UUID customerId) {
	this.customerId = customerId;
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria#getAreaId()
     */
    @Override
    public UUID getAreaId() {
	return areaId;
    }

    public void setAreaId(UUID areaId) {
	this.areaId = areaId;
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria#getAssetId()
     */
    @Override
    public UUID getAssetId() {
	return assetId;
    }

    public void setAssetId(UUID assetId) {
	this.assetId = assetId;
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria#
     * getTriggeringEventId()
     */
    @Override
    public UUID getTriggeringEventId() {
	return triggeringEventId;
    }

    public void setTriggeringEventId(UUID triggeringEventId) {
	this.triggeringEventId = triggeringEventId;
    }

    /*
     * @see com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria#getState()
     */
    @Override
    public DeviceAlarmState getState() {
	return state;
    }

    public void setState(DeviceAlarmState state) {
	this.state = state;
    }
}
