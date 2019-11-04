/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceElementMapping;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;

/**
 *
 */
@Entity
@Table(name = "device")
public class Device implements IDevice {

    /** Serialization version identifier */
    private static final long serialVersionUID = -5409798557113797549L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Device type id */
    @Column(name = "device_type_id")
    private UUID deviceTypeId;

    /** DeviceType token */
    @Column(name = "device_type_token")
    private String deviceTypeToken;

    /** Id for current assignment if assigned */
    @Column(name = "device_assignment_id")
    private UUID deviceAssignmentId;

    /** Parent device id (if nested) */
    @Column(name = "parent_device_id")
    private UUID parentDeviceId;

    @OneToMany(cascade= {CascadeType.ALL}, fetch=FetchType.EAGER, mappedBy = "device")
    private List<DeviceElementMapping> deviceElementMappings = new ArrayList<>();

    /** Comments */
    @Column(name = "comments")
    private String comments;

    /** Status indicator */
    @Column(name = "status")
    private String status;

    /** Unique token */
    @Column(name = "token")
    private String token;

    /** Date entity was created */
    @Column(name = "created_date")
    private Date createdDate;

    /** Username for creator */
    @Column(name = "created_by")
    private String createdBy;

    /** Date entity was last updated */
    @Column(name = "updated_date")
    private Date updatedDate;

    /** Username that updated entity */
    @Column(name = "updated_by")
    private String updatedBy;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="device_active_assignment")
    @Column(name = "active_device_assignment_id")
    private List<UUID> activeDeviceAssignmentIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="device_metadata")
    @MapKeyColumn(name="prop_key")
    @Column(name="prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    @Override
    public List<UUID> getActiveDeviceAssignmentIds() {
	return activeDeviceAssignmentIds;
    }

    @Override
    public UUID getParentDeviceId() {
	return parentDeviceId;
    }

    @Override
    public List<IDeviceElementMapping> getDeviceElementMappings() {
	List<IDeviceElementMapping> lst = new ArrayList<>();
	for(DeviceElementMapping mapping : deviceElementMappings) {
	    lst.add(mapping);
	}
	return lst;
    }

    @Override
    public String getComments() {
	return comments;
    }

    @Override
    public String getStatus() {
	return status;
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
	return updatedBy;
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

    public void setDeviceAssignmentId(UUID deviceAssignmentId) {
	this.deviceAssignmentId = deviceAssignmentId;
    }

    public void setParentDeviceId(UUID parentDeviceId) {
	this.parentDeviceId = parentDeviceId;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    public void setStatus(String status) {
	this.status = status;
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

    public void setDeviceElementMappings(List<DeviceElementMapping> deviceElementMappings) {
	this.deviceElementMappings = deviceElementMappings;
    }

    public UUID getDeviceAssignmentId() {
	return deviceAssignmentId;
    }

    public void setActiveDeviceAssignmentIds(List<UUID> activeDeviceAssignmentIds) {
	this.activeDeviceAssignmentIds = activeDeviceAssignmentIds;
    }

    public String getDeviceTypeToken() {
	return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
	this.deviceTypeToken = deviceTypeToken;
    }
}
