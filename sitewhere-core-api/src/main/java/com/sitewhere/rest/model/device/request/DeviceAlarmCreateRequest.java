/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest;

/**
 * Fields needed to create/update a device alarm.
 * 
 * @author Derek Adams
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAlarmCreateRequest implements IDeviceAlarmCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = -5830937120265421555L;

    /** Device assignment token */
    private String deviceAssignmentToken;

    /** Alarm message */
    private String alarmMessage;

    /** Triggering event id */
    private UUID triggeringEventId;

    /** Alarm state */
    private DeviceAlarmState state;

    /** Alarm triggered date */
    private Date triggeredDate;

    /** Alarm acknowledged date */
    private Date acknowledgedDate;

    /** Alarm resolved date */
    private Date resolvedDate;

    /** Metadata */
    private Map<String, String> metadata;

    /*
     * @see com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest#
     * getDeviceAssignmentToken()
     */
    @Override
    public String getDeviceAssignmentToken() {
	return deviceAssignmentToken;
    }

    public void setDeviceAssignmentToken(String deviceAssignmentToken) {
	this.deviceAssignmentToken = deviceAssignmentToken;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest#getAlarmMessage()
     */
    @Override
    public String getAlarmMessage() {
	return alarmMessage;
    }

    public void setAlarmMessage(String alarmMessage) {
	this.alarmMessage = alarmMessage;
    }

    /*
     * @see com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest#
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
     * @see com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest#getState()
     */
    @Override
    public DeviceAlarmState getState() {
	return state;
    }

    public void setState(DeviceAlarmState state) {
	this.state = state;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest#getTriggeredDate()
     */
    @Override
    public Date getTriggeredDate() {
	return triggeredDate;
    }

    public void setTriggeredDate(Date triggeredDate) {
	this.triggeredDate = triggeredDate;
    }

    /*
     * @see com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest#
     * getAcknowledgedDate()
     */
    @Override
    public Date getAcknowledgedDate() {
	return acknowledgedDate;
    }

    public void setAcknowledgedDate(Date acknowledgedDate) {
	this.acknowledgedDate = acknowledgedDate;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest#getResolvedDate()
     */
    @Override
    public Date getResolvedDate() {
	return resolvedDate;
    }

    public void setResolvedDate(Date resolvedDate) {
	this.resolvedDate = resolvedDate;
    }

    /*
     * @see com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static class Builder {

	/** Request being built */
	private DeviceAlarmCreateRequest request = new DeviceAlarmCreateRequest();

	public Builder(String deviceAssignmentToken, String alarmMessage) {
	    request.setDeviceAssignmentToken(deviceAssignmentToken);
	    request.setAlarmMessage(alarmMessage);
	    request.setState(DeviceAlarmState.Triggered);
	}

	public Builder withTriggeringEventId(UUID eventId) {
	    request.setTriggeringEventId(eventId);
	    return this;
	}

	public Builder withTriggeredState() {
	    request.setState(DeviceAlarmState.Triggered);
	    return this;
	}

	public Builder withAcknowledgedState() {
	    request.setState(DeviceAlarmState.Acknowledged);
	    return this;
	}

	public Builder withResolvedState() {
	    request.setState(DeviceAlarmState.Resolved);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public DeviceAlarmCreateRequest build() {
	    return request;
	}
    }
}
