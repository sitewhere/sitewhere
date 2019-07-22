package com.sitewhere.rdb.entities.event;

import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "device_event")
public class DeviceEvent implements IDeviceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /** Unique alarm id */
    private UUID id;

    /** Alternate (external) id for event */
    private String alternateId;

    /** Event type indicator */
    @Enumerated(EnumType.STRING)
    private DeviceEventType eventType;

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

    /** Date event occurred */
    private Date eventDate;

    /** Date event was received */
    private Date receivedDate;

    @ElementCollection
    @CollectionTable(name="device_event_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> metadata = new HashMap<>();

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
