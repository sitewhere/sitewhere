/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.device.persistence.rdb.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.device.group.IDeviceGroupElement;

@Entity
@Table(name = "device_group_element")
public class RdbDeviceGroupElement extends RdbPersistentEntity implements IDeviceGroupElement {

    /** Serial version UID */
    private static final long serialVersionUID = -3439614608465612991L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private RdbDeviceGroup deviceGroup;

    @Column(name = "device_id", nullable = true)
    private UUID deviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private RdbDevice device;

    @Column(name = "nested_group_id", nullable = true)
    private UUID nestedGroupId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "nested_group_id", insertable = false, updatable = false)
    private RdbDeviceGroup nestedGroup;

    /** List of roles for the element */
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_group_element_roles", joinColumns = @JoinColumn(name = "device_group_element_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<String>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_group_element_metadata", joinColumns = @JoinColumn(name = "device_group_element_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getId()
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

    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static void copy(IDeviceGroupElement source, RdbDeviceGroupElement target) {
	target.setGroupId(source.getGroupId());
	if (source.getDeviceId() != null) {
	    target.setDeviceId(source.getDeviceId());
	}
	if (source.getNestedGroupId() != null) {
	    target.setNestedGroupId(source.getNestedGroupId());
	}
	RdbPersistentEntity.copy(source, target);
    }
}
