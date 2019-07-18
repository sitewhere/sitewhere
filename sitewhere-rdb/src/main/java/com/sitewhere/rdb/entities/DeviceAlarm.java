package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.device.IDeviceAlarm;

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
    @Enumerated(EnumType.STRING)
    private DeviceAlarmState state;

    /** Date alarm was triggered */
    private Date triggeredDate;

    /** Date alarm was acknowledged */
    private Date acknowledgedDate;

    /** Date alarm was resolved */
    private Date resolvedDate;

    @ElementCollection
    @CollectionTable(name="device_alarm_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
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
