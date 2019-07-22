package com.sitewhere.rdb.entities.event;

import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "device_command_invocation")
public class DeviceCommandInvocation implements IDeviceCommandInvocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /** Unique device_command_invocation id */
    private UUID id;

    /** Type of actor that initiated the command */
    @Enumerated(EnumType.STRING)
    private CommandInitiator initiator;

    /** Id of actor that initiated the command */
    private String initiatorId;

    /** Type of actor that will receive the command */
    @Enumerated(EnumType.STRING)
    private CommandTarget target;

    /** Id of actor that will receive the command */
    private String targetId;

    /** Unique token of command to execute */
    private String commandToken;

    /** Values to use for command parameters */
    @ElementCollection
    @CollectionTable(name="device_command_invocation_params")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> parameterValues = new HashMap<String, String>();

    /** Alternate id */
    private String alternateId;

    /** Event type */
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
    @CollectionTable(name="device_command_invocation_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public CommandInitiator getInitiator() {
        return initiator;
    }

    @Override
    public String getInitiatorId() {
        return initiatorId;
    }

    @Override
    public CommandTarget getTarget() {
        return target;
    }

    @Override
    public String getTargetId() {
        return targetId;
    }

    @Override
    public String getCommandToken() {
        return commandToken;
    }

    @Override
    public Map<String, String> getParameterValues() {
        return parameterValues;
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

    public void setInitiator(CommandInitiator initiator) {
        this.initiator = initiator;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public void setTarget(CommandTarget target) {
        this.target = target;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void setCommandToken(String commandToken) {
        this.commandToken = commandToken;
    }

    public void setParameterValues(Map<String, String> parameterValues) {
        this.parameterValues = parameterValues;
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
