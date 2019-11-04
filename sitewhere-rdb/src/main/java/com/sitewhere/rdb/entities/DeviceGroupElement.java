/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.group.IDeviceGroupElement;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "device_group_element")
public class DeviceGroupElement implements IDeviceGroupElement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Parent group id */
    @Column(name = "group_id")
    private UUID groupId;

    /** Device id (null if nested group id specified) */
    @Column(name = "device_id")
    private UUID deviceId;

    /** Nested group id (null if device id specified) */
    @Column(name = "nested_group_id")
    private UUID nestedGroupId;

    /** List of roles for the element */
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="device_group_element_roles", joinColumns = @JoinColumn(name = "device_group_element_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<String>();

    @Override
    public UUID getId() {
	return id;
    }

    @Override
    public UUID getGroupId() {
	return groupId;
    }

    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    @Override
    public UUID getNestedGroupId() {
	return nestedGroupId;
    }

    @Override
    public List<String> getRoles() {
	return roles;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    public void setGroupId(UUID groupId) {
	this.groupId = groupId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    public void setNestedGroupId(UUID nestedGroupId) {
	this.nestedGroupId = nestedGroupId;
    }

    public void setRoles(List<String> roles) {
	this.roles = roles;
    }

}
