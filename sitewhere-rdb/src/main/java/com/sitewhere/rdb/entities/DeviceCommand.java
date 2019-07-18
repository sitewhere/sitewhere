package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "device_command")
public class DeviceCommand implements IDeviceCommand {

    /** Property for device type id */
    public static final String PROP_DEVICE_TYPE_ID = "dtid";

    /** Property for command namespace */
    public static final String PROP_NAMESPACE = "nmsp";

    /** Property for command name */
    public static final String PROP_NAME = "name";

    /** Property for command description */
    public static final String PROP_DESCRIPTION = "desc";

    /** Property for command parameters list */
    public static final String PROP_PARAMETERS = "parm";

    /** Property for command parameter name */
    public static final String PROP_PARAM_NAME = "pnam";

    /** Property for command parameter type */
    public static final String PROP_PARAM_TYPE = "ptyp";

    /** Property for command parameter required indicator */
    public static final String PROP_PARAM_REQUIRED = "preq";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Unique id for parent specification */
    private UUID deviceTypeId;

    /** Command namespace */
    private String namespace;

    /** Command name */
    private String name;

    /** Command description */
    private String description;

    /** Parameter list */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "device_command_id")
    private List<CommandParameter> parameterList = new ArrayList<>();

    private String token;

    private Date createdDate;

    private String createdBy;

    private Date updatedDate;

    private String updatedBy;

    @ElementCollection
    @CollectionTable(name="device_command_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public UUID getDeviceTypeId() {
        return deviceTypeId;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public List<ICommandParameter> getParameters() {
        List<ICommandParameter> lst = new ArrayList<>();
        for(CommandParameter p : parameterList) {
            lst.add(p);
        }
        return lst;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String getUpdatedBy() {
        return createdBy;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public List<CommandParameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<CommandParameter> parameterList) {
        this.parameterList = parameterList;
    }


}
