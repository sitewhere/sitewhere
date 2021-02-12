/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.state.IRecentAlertEvent;

@Entity
@Table(name = "recent_alert_event")
public class RdbRecentAlertEvent implements IRecentAlertEvent {

    /** Serial version UID */
    private static final long serialVersionUID = -2026228182018439537L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_state_id", nullable = false)
    private UUID deviceStateId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_state_id", insertable = false, updatable = false)
    private RdbDeviceState deviceState;

    @Column(name = "event_date ")
    private Date eventDate;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "source", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertSource source;

    @Column(name = "level", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertLevel level;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "message", nullable = false)
    private String message;

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

    public RdbDeviceState getDeviceState() {
	return deviceState;
    }

    public void setDeviceState(RdbDeviceState deviceState) {
	this.deviceState = deviceState;
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

    /*
     * @see com.sitewhere.spi.device.event.IDeviceAlertContent#getSource()
     */
    @Override
    public AlertSource getSource() {
	return source;
    }

    public void setSource(AlertSource source) {
	this.source = source;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceAlertContent#getLevel()
     */
    @Override
    public AlertLevel getLevel() {
	return level;
    }

    public void setLevel(AlertLevel level) {
	this.level = level;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceAlertContent#getType()
     */
    @Override
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceAlertContent#getMessage()
     */
    @Override
    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!(obj instanceof RdbRecentAlertEvent)) {
	    return false;
	}
	RdbRecentAlertEvent that = (RdbRecentAlertEvent) obj;
	EqualsBuilder eb = new EqualsBuilder();
	eb.append(getId(), that.getId());
	return eb.isEquals();
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	HashCodeBuilder hcb = new HashCodeBuilder();
	hcb.append(getId());
	return hcb.toHashCode();
    }

    /**
     * Create RDB entity from API entity.
     * 
     * @param state
     * @param api
     * @return
     */
    public static RdbRecentAlertEvent createFrom(RdbDeviceState state, IDeviceAlert api) {
	RdbRecentAlertEvent rdb = new RdbRecentAlertEvent();
	rdb.setDeviceStateId(state.getId());
	rdb.setEventId(api.getId());
	rdb.setEventDate(api.getEventDate());
	rdb.setSource(api.getSource());
	rdb.setLevel(api.getLevel());
	rdb.setType(api.getType());
	rdb.setMessage(api.getMessage());
	return rdb;
    }

    /**
     * Create API entity from RDB entity.
     * 
     * @param rdb
     * @return
     */
    public static IDeviceAlert createApiFrom(RdbRecentAlertEvent rdb) {
	DeviceAlert api = new DeviceAlert();
	api.setId(rdb.getId());
	api.setEventDate(rdb.getEventDate());
	api.setSource(rdb.getSource());
	api.setLevel(rdb.getLevel());
	api.setType(rdb.getType());
	api.setMessage(rdb.getMessage());
	return api;
    }
}
