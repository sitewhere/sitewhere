/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.rdb.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.spi.device.group.IDeviceGroupElement;

@Entity
@Table(name = "device_group_element")
public class RdbDeviceGroupElement implements IDeviceGroupElement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "group_id", insertable = false, updatable = false, nullable = false)
    private UUID groupId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "group_id")
    private RdbDeviceGroup deviceGroup;

    @Column(name = "device_id", insertable = false, updatable = false, nullable = true)
    private UUID deviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "device_id")
    private RdbDevice device;

    @Column(name = "nested_group_id", insertable = false, updatable = false, nullable = true)
    private UUID nestedGroupId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nested_group_id")
    private RdbDeviceGroup nestedGroup;

    /** List of roles for the element */
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_group_element_roles", joinColumns = @JoinColumn(name = "device_group_element_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<String>();

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getGroupId()
     */
    @Override
    public UUID getGroupId() {
	return groupId;
    }

    public void setGroupId(UUID groupId) {
	this.groupId = groupId;
    }

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getNestedGroupId()
     */
    @Override
    public UUID getNestedGroupId() {
	return nestedGroupId;
    }

    public void setNestedGroupId(UUID nestedGroupId) {
	this.nestedGroupId = nestedGroupId;
    }

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroupElement#getRoles()
     */
    @Override
    public List<String> getRoles() {
	return roles;
    }

    public void setRoles(List<String> roles) {
	this.roles = roles;
    }

    public RdbDeviceGroup getDeviceGroup() {
	return deviceGroup;
    }

    public void setDeviceGroup(RdbDeviceGroup deviceGroup) {
	this.deviceGroup = deviceGroup;
    }

    public RdbDevice getDevice() {
	return device;
    }

    public void setDevice(RdbDevice device) {
	this.device = device;
    }

    public RdbDeviceGroup getNestedGroup() {
	return nestedGroup;
    }

    public void setNestedGroup(RdbDeviceGroup nestedGroup) {
	this.nestedGroup = nestedGroup;
    }

    public static void copy(IDeviceGroupElement source, RdbDeviceGroupElement target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getDeviceId() != null) {
	    target.setDeviceId(source.getDeviceId());
	}
	if (source.getNestedGroupId() != null) {
	    target.setNestedGroupId(source.getNestedGroupId());
	}
    }
}
