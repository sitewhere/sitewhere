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
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.state.IRecentMeasurementEvent;

@Entity
@Table(name = "recent_measurement_event")
public class RdbRecentMeasurementEvent implements IRecentMeasurementEvent {

    /** Serial version UID */
    private static final long serialVersionUID = 629912667265259335L;

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

    @Column(name = "event_id", nullable = true)
    private UUID eventId;

    /** Name */
    @Column(name = "name", nullable = false)
    private String name;

    /** Value */
    @Column(name = "value", nullable = false, precision = 32, scale = 8)
    private BigDecimal value;

    /** Maximum value */
    @Column(name = "max_value", nullable = false, precision = 32, scale = 8)
    private BigDecimal maxValue;

    @Column(name = "max_value_date ")
    private Date maxValueDate;

    /** Minimum value */
    @Column(name = "min_value", nullable = false, precision = 32, scale = 8)
    private BigDecimal minValue;

    @Column(name = "min_value_date ")
    private Date minValueDate;

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
     * @see com.sitewhere.spi.device.event.IDeviceMeasurementContent#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceMeasurementContent#getValue()
     */
    @Override
    public BigDecimal getValue() {
	return value;
    }

    public void setValue(BigDecimal value) {
	this.value = value;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentMeasurementEvent#getMaxValue()
     */
    @Override
    public BigDecimal getMaxValue() {
	return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
	this.maxValue = maxValue;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentMeasurementEvent#getMaxValueDate()
     */
    @Override
    public Date getMaxValueDate() {
	return maxValueDate;
    }

    public void setMaxValueDate(Date maxValueDate) {
	this.maxValueDate = maxValueDate;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentMeasurementEvent#getMinValue()
     */
    @Override
    public BigDecimal getMinValue() {
	return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
	this.minValue = minValue;
    }

    /*
     * @see com.sitewhere.spi.device.state.IRecentMeasurementEvent#getMinValueDate()
     */
    @Override
    public Date getMinValueDate() {
	return minValueDate;
    }

    public void setMinValueDate(Date minValueDate) {
	this.minValueDate = minValueDate;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!(obj instanceof RdbRecentMeasurementEvent)) {
	    return false;
	}
	RdbRecentMeasurementEvent that = (RdbRecentMeasurementEvent) obj;
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
    public static RdbRecentMeasurementEvent createFrom(RdbDeviceState state, IDeviceMeasurement api) {
	RdbRecentMeasurementEvent rdb = new RdbRecentMeasurementEvent();
	rdb.setDeviceStateId(state.getId());
	rdb.setEventId(api.getId());
	rdb.setEventDate(api.getEventDate());
	rdb.setName(api.getName());
	rdb.setValue(api.getValue());
	rdb.setMaxValue(api.getValue());
	rdb.setMaxValueDate(new Date());
	rdb.setMinValue(api.getValue());
	rdb.setMinValueDate(new Date());
	return rdb;
    }

    /**
     * Create API entity from RDB entity.
     * 
     * @param rdb
     * @return
     */
    public static IDeviceMeasurement createApiFrom(RdbRecentMeasurementEvent rdb) {
	DeviceMeasurement api = new DeviceMeasurement();
	api.setId(rdb.getId());
	api.setEventDate(rdb.getEventDate());
	api.setName(rdb.getName());
	api.setValue(rdb.getValue());
	return api;
    }
}
