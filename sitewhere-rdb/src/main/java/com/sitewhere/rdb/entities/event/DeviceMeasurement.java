package com.sitewhere.rdb.entities.event;

import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "device_measurement")
public class DeviceMeasurement implements IDeviceMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /** Unique alarm id */
    private UUID id;

    /** Measurement name */
    private String name;

    /** Measurement value */
    private Double value;

    /** Alternate id */
    private String alternateId;

    /** Event type */
    @Enumerated(EnumType.STRING)
    private DeviceEventType eventType;

    /** Device ID */
    private UUID deviceId;

    /** Device Assignment ID */
    private UUID deviceAssignmentId;

    /** Customer ID */
    private UUID customerId;

    /** Area ID */
    private UUID areaId;

    /** Asset ID */
    private UUID assetId;

    /** Event date */
    private Date eventDate;

    /** Received date */
    private Date receivedDate;

    @ElementCollection
    @CollectionTable(name="device_measurement_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getAlternateId() {
        return alternateId;
    }

    @Override
    public DeviceEventType getEventType() {
        return eventType;
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
    public Date getEventDate() {
        return eventDate;
    }

    @Override
    public Date getReceivedDate() {
        return receivedDate;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public void setEventType(DeviceEventType eventType) {
        this.eventType = eventType;
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

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
