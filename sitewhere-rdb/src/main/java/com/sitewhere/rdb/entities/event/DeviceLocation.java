package com.sitewhere.rdb.entities.event;

import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceLocation;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "device_location")
public class DeviceLocation implements IDeviceLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /** Unique device_location id */
    private UUID id;

    /** Latitude value */
    private Double latitude;

    /** Longitude value */
    private Double longitude;

    /** Elevation value */
    private Double elevation;

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
    @CollectionTable(name="device_location_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public Double getElevation() {
        return elevation;
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

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
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
