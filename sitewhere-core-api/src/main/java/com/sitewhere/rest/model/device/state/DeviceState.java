/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.state;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.spi.device.state.IDeviceState;

/**
 * Model object for device state.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceState implements IDeviceState {

    /** Serial version UID */
    private static final long serialVersionUID = 3438565646525194167L;

    /** Unique id */
    private UUID id;

    /** Device id */
    private UUID deviceId;

    /** Device assignment id */
    private UUID deviceAssignmentId;

    /** Date of last interaction with assignment */
    private Date lastInteractionDate;

    /** Date presence was determined to be missing */
    private Date presenceMissingDate;

    /** Event id of last location event */
    private UUID lastLocationEventId;

    /** Map of last measurement event ids by mx id */
    private Map<String, UUID> lastMeasurementEventIds;

    /** Map of last alert event ids by alert type */
    private Map<String, UUID> lastAlertEventIds;

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return deviceAssignmentId;
    }

    public void setDeviceAssignmentId(UUID deviceAssignmentId) {
	this.deviceAssignmentId = deviceAssignmentId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getLastInteractionDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getLastInteractionDate() {
	return lastInteractionDate;
    }

    public void setLastInteractionDate(Date lastInteractionDate) {
	this.lastInteractionDate = lastInteractionDate;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getPresenceMissingDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getPresenceMissingDate() {
	return presenceMissingDate;
    }

    public void setPresenceMissingDate(Date presenceMissingDate) {
	this.presenceMissingDate = presenceMissingDate;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getLastLocationEventId()
     */
    @Override
    public UUID getLastLocationEventId() {
	return lastLocationEventId;
    }

    public void setLastLocationEventId(UUID lastLocationEventId) {
	this.lastLocationEventId = lastLocationEventId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getLastMeasurementEventIds()
     */
    @Override
    public Map<String, UUID> getLastMeasurementEventIds() {
	return lastMeasurementEventIds;
    }

    public void setLastMeasurementEventIds(Map<String, UUID> lastMeasurementEventIds) {
	this.lastMeasurementEventIds = lastMeasurementEventIds;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getLastAlertEventIds()
     */
    @Override
    public Map<String, UUID> getLastAlertEventIds() {
	return lastAlertEventIds;
    }

    public void setLastAlertEventIds(Map<String, UUID> lastAlertEventIds) {
	this.lastAlertEventIds = lastAlertEventIds;
    }
}