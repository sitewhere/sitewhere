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

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.state.IRecentLocationEvent;

@Entity
@Table(name = "recent_location_event")
public class RdbRecentLocationEvent implements IRecentLocationEvent {

    /** Serial version UID */
    private static final long serialVersionUID = -7598761819367748310L;

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

    /** Latitude */
    @Column(name = "latitude", nullable = false, precision = 12, scale = 8)
    private BigDecimal latitude;

    /** Longitude */
    @Column(name = "longitude", nullable = false, precision = 12, scale = 8)
    private BigDecimal longitude;

    /** Elevation */
    @Column(name = "elevation", nullable = true, precision = 12, scale = 2)
    private BigDecimal elevation;

    /*
     * @see com.sitewhere.spi.device.state.IRecentStateEvent#getId()
     */
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentStateEvent#getDeviceStateId()
     */
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

    public BigDecimal getLatitude() {
	return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
	this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
	return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
	this.longitude = longitude;
    }

    public BigDecimal getElevation() {
	return elevation;
    }

    public void setElevation(BigDecimal elevation) {
	this.elevation = elevation;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!(obj instanceof RdbRecentLocationEvent)) {
	    return false;
	}
	RdbRecentLocationEvent that = (RdbRecentLocationEvent) obj;
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
    public static RdbRecentLocationEvent createFrom(RdbDeviceState state, IDeviceLocation api) {
	RdbRecentLocationEvent rdb = new RdbRecentLocationEvent();
	rdb.setDeviceStateId(state.getId());
	rdb.setEventId(api.getId());
	rdb.setEventDate(api.getEventDate());
	rdb.setLatitude(api.getLatitude());
	rdb.setLongitude(api.getLongitude());
	rdb.setElevation(api.getElevation());
	return rdb;
    }

    /**
     * Create API entity from RDB entity.
     * 
     * @param rdb
     * @return
     */
    public static IDeviceLocation createApiFrom(RdbRecentLocationEvent rdb) {
	DeviceLocation api = new DeviceLocation();
	api.setId(rdb.getId());
	api.setEventDate(rdb.getEventDate());
	api.setLatitude(rdb.getLatitude());
	api.setLongitude(rdb.getLongitude());
	api.setElevation(rdb.getElevation());
	return api;
    }
}
