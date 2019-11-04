/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.device.IDeviceAlarm;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "device_alarm")
public class DeviceAlarm implements IDeviceAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /** Unique alarm id */
    @Column(name = "id")
    private UUID id;

    /** Device id */
    @Column(name = "device_id")
    private UUID deviceId;

    /** Device assignment id */
    @Column(name = "device_assignment_id")
    private UUID deviceAssignmentId;

    /** Customer id */
    @Column(name = "customer_id")
    private UUID customerId;

    /** Area id */
    @Column(name = "area_id")
    private UUID areaId;

    /** Asset id */
    @Column(name = "asset_id")
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
    @CollectionTable(name="device_alarm_metadata", joinColumns = @JoinColumn(name = "device_alarm_id"))
    @MapKeyColumn(name="prop_key")
    @Column(name="prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public UUID getId() {
	return id;
    }

    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    @Override
    public UUID getDeviceAssignmentId() {
	return deviceAssignmentId;
    }

    @Override
    public UUID getCustomerId() {
	return customerId;
    }

    @Override
    public UUID getAreaId() {
	return areaId;
    }

    @Override
    public UUID getAssetId() {
	return assetId;
    }

    @Override
    public String getAlarmMessage() {
	return alarmMessage;
    }

    @Override
    public UUID getTriggeringEventId() {
	return triggeringEventId;
    }

    @Override
    public DeviceAlarmState getState() {
	return state;
    }

    @Override
    public Date getTriggeredDate() {
	return triggeredDate;
    }

    @Override
    public Date getAcknowledgedDate() {
	return acknowledgedDate;
    }

    @Override
    public Date getResolvedDate() {
	return resolvedDate;
    }

    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    public void setDeviceAssignmentId(UUID deviceAssignmentId) {
	this.deviceAssignmentId = deviceAssignmentId;
    }

    public void setCustomerId(UUID customerId) {
	this.customerId = customerId;
    }

    public void setAreaId(UUID areaId) {
	this.areaId = areaId;
    }

    public void setAssetId(UUID assetId) {
	this.assetId = assetId;
    }

    public void setAlarmMessage(String alarmMessage) {
	this.alarmMessage = alarmMessage;
    }

    public void setTriggeringEventId(UUID triggeringEventId) {
	this.triggeringEventId = triggeringEventId;
    }

    public void setState(DeviceAlarmState state) {
	this.state = state;
    }

    public void setTriggeredDate(Date triggeredDate) {
	this.triggeredDate = triggeredDate;
    }

    public void setAcknowledgedDate(Date acknowledgedDate) {
	this.acknowledgedDate = acknowledgedDate;
    }

    public void setResolvedDate(Date resolvedDate) {
	this.resolvedDate = resolvedDate;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }
}
