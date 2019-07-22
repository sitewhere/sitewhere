package com.sitewhere.rdb.entities.event;

import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceStateChange;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "device_state_change")
public class DeviceStateChange implements IDeviceStateChange {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /** Unique alarm id */
    private UUID id;

    /** State change category */
    private String attribute;

    /** State change type */
    private String type;

    /** Previous or expected state */
    private String previousState;

    /** New state */
    private String newState;

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
    @CollectionTable(name="device_state_change_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> metadata = new HashMap<>();


    @Override
    public String getAttribute() {
        return attribute;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getPreviousState() {
        return previousState;
    }

    @Override
    public String getNewState() {
        return newState;
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
}
