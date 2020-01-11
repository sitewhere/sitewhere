/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.persistence.rdb.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.state.IRecentStateEvent;

@Entity
@Table(name = "recent_state_event")
public class RdbRecentStateEvent implements IRecentStateEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_state_id", nullable = true)
    private UUID deviceStateId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "device_state_id", insertable = false, updatable = false)
    private RdbDeviceState deviceState;

    /** Assignment status */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private DeviceEventType eventType;

    @Column(name = "classifier")
    private String classifier;

    @Column(name = "value")
    private String value;

    @Column(name = "event_date ")
    private Date eventDate;

    @Column(name = "event_id", nullable = true)
    private UUID eventId;

    /*
     * @see com.sitewhere.spi.device.state.IRecentStateEvent#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentStateEvent#getDeviceStateId()
     */
    @Override
    public UUID getDeviceStateId() {
	return deviceStateId;
    }

    public void setDeviceStateId(UUID deviceStateId) {
	this.deviceStateId = deviceStateId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentStateEvent#getEventType()
     */
    @Override
    public DeviceEventType getEventType() {
	return eventType;
    }

    public void setEventType(DeviceEventType eventType) {
	this.eventType = eventType;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentStateEvent#getClassifier()
     */
    @Override
    public String getClassifier() {
	return classifier;
    }

    public void setClassifier(String classifier) {
	this.classifier = classifier;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentStateEvent#getValue()
     */
    @Override
    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentStateEvent#getEventDate()
     */
    @Override
    public Date getEventDate() {
	return eventDate;
    }

    public void setEventDate(Date eventDate) {
	this.eventDate = eventDate;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentStateEvent#getEventId()
     */
    @Override
    public UUID getEventId() {
	return eventId;
    }

    public void setEventId(UUID eventId) {
	this.eventId = eventId;
    }

    public RdbDeviceState getDeviceState() {
	return deviceState;
    }

    public void setDeviceState(RdbDeviceState deviceState) {
	this.deviceState = deviceState;
    }

    public static void copy(IRecentStateEvent source, RdbRecentStateEvent target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getDeviceStateId() != null) {
	    target.setDeviceStateId(source.getDeviceStateId());
	}
	if (source.getEventType() != null) {
	    target.setEventType(source.getEventType());
	}
	if (source.getClassifier() != null) {
	    target.setClassifier(source.getClassifier());
	}
	if (source.getValue() != null) {
	    target.setValue(source.getValue());
	}
	if (source.getEventId() != null) {
	    target.setEventId(source.getEventId());
	}
	if (source.getEventDate() != null) {
	    target.setEventDate(source.getEventDate());
	}
    }
}
