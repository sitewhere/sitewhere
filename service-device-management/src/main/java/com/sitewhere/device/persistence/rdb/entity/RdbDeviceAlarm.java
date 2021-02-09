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
package com.sitewhere.device.persistence.rdb.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.device.IDeviceAlarm;

@Entity
@Table(name = "device_alarm")
public class RdbDeviceAlarm implements IDeviceAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /** Unique alarm id */
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private RdbDevice device;

    @Column(name = "device_assignment_id", nullable = true)
    private UUID deviceAssignmentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "device_assignment_id", insertable = false, updatable = false)
    private RdbDeviceAssignment deviceAssignment;

    @Column(name = "customer_id", nullable = true)
    private UUID customerId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private RdbCustomer customer;

    @Column(name = "area_id", nullable = true)
    private UUID areaId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "area_id", insertable = false, updatable = false)
    private RdbArea area;

    @Column(name = "asset_id", nullable = true)
    private UUID assetId;

    /** Alarm message */
    @Column(name = "alarm_message")
    private String alarmMessage;

    /** Event that triggered alarm */
    @Column(name = "triggering_event_id")
    private UUID triggeringEventId;

    /** Current alarm state */
    @Enumerated(EnumType.STRING)
    @Column(name = "state ")
    private DeviceAlarmState state;

    /** Date alarm was triggered */
    @Column(name = "triggered_date ")
    private Date triggeredDate;

    /** Date alarm was acknowledged */
    @Column(name = "acknowledged_date ")
    private Date acknowledgedDate;

    /** Date alarm was resolved */
    @Column(name = "resolved_date ")
    private Date resolvedDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_alarm_metadata", joinColumns = @JoinColumn(name = "device_alarm_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

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

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static void copy(IDeviceAlarm source, RdbDeviceAlarm target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getDeviceId() != null) {
	    target.setDeviceId(source.getDeviceId());
	}
	if (source.getDeviceAssignmentId() != null) {
	    target.setDeviceAssignmentId(source.getDeviceAssignmentId());
	}
	if (source.getCustomerId() != null) {
	    target.setCustomerId(source.getCustomerId());
	}
	if (source.getAreaId() != null) {
	    target.setAreaId(source.getAreaId());
	}
	if (source.getAssetId() != null) {
	    target.setAssetId(source.getAssetId());
	}
	if (source.getAlarmMessage() != null) {
	    target.setAlarmMessage(source.getAlarmMessage());
	}
	if (source.getTriggeringEventId() != null) {
	    target.setTriggeringEventId(source.getTriggeringEventId());
	}
	if (source.getState() != null) {
	    target.setState(source.getState());
	}
	if (source.getTriggeredDate() != null) {
	    target.setTriggeredDate(source.getTriggeredDate());
	}
	if (source.getAcknowledgedDate() != null) {
	    target.setAcknowledgedDate(source.getAcknowledgedDate());
	}
	if (source.getResolvedDate() != null) {
	    target.setResolvedDate(source.getResolvedDate());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
    }
}
