/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.spi.device.group.IDeviceGroup;

@Entity
@Table(name = "device_group")
@NamedQuery(name = Queries.QUERY_DEVICE_GROUP_BY_TOKEN, query = "SELECT g FROM RdbDeviceGroup g WHERE g.token = :token")
public class RdbDeviceGroup extends RdbBrandedEntity implements IDeviceGroup {

    /** Serial version UID */
    private static final long serialVersionUID = 8574963682462211766L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Group name */
    @Column(name = "name")
    private String name;

    /** Group description */
    @Column(name = "description")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "deviceGroup", fetch = FetchType.LAZY)
    private List<RdbDeviceGroupElement> elements;

    @JsonIgnore
    @OneToMany(mappedBy = "nestedGroup", fetch = FetchType.LAZY)
    private List<RdbDeviceGroupElement> groupElementReferences;

    /** List of roles */
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_group_roles", joinColumns = @JoinColumn(name = "device_group_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_group_metadata", joinColumns = @JoinColumn(name = "device_group_id"))
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
     * @see com.sitewhere.spi.common.IAccessible#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.common.IAccessible#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * @see com.sitewhere.spi.device.group.IDeviceGroup#getRoles()
     */
    @Override
    public List<String> getRoles() {
	return roles;
    }

    public void setRoles(List<String> roles) {
	this.roles = roles;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public List<RdbDeviceGroupElement> getElements() {
	return elements;
    }

    public void setElements(List<RdbDeviceGroupElement> elements) {
	this.elements = elements;
    }

    public List<RdbDeviceGroupElement> getGroupElementReferences() {
	return groupElementReferences;
    }

    public void setGroupElementReferences(List<RdbDeviceGroupElement> groupElementReferences) {
	this.groupElementReferences = groupElementReferences;
    }

    public static void copy(IDeviceGroup source, RdbDeviceGroup target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getName() != null) {
	    target.setName(source.getName());
	}
	if (source.getDescription() != null) {
	    target.setDescription(source.getDescription());
	}
	if (source.getRoles() != null) {
	    target.setRoles(source.getRoles());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
	RdbBrandedEntity.copy(source, target);
    }
}
