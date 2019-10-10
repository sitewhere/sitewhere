/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "device_command")
public class DeviceCommand implements IDeviceCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Unique id for parent specification */
    private UUID deviceTypeId;

    private String deviceTypeToken;

    /** Command namespace */
    private String namespace;

    /** Command name */
    private String name;

    /** Command description */
    private String description;

    /** Parameter list */
    @OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.LAZY)
    private List<CommandParameter> parameterList = new ArrayList<>();

    /** Unique token */
    private String token;

    /** Date entity was created */
    private Date createdDate;

    /** Username for creator */
    private String createdBy;

    /** Date entity was last updated */
    private Date updatedDate;

    /** Username that updated entity */
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

    public String getDeviceTypeToken() {
        return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
        this.deviceTypeToken = deviceTypeToken;
    }
}
