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

import javax.persistence.CascadeType;
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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.device.IDevice;

@Entity
@Table(name = "device", uniqueConstraints = @UniqueConstraint(columnNames = { "token" }))
@NamedQuery(name = Queries.QUERY_DEVICE_BY_TOKEN, query = "SELECT d FROM RdbDevice d WHERE d.token = :token")
public class RdbDevice extends RdbPersistentEntity implements IDevice {

    /** Serialization version identifier */
    private static final long serialVersionUID = -5409798557113797549L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_type_id", nullable = false)
    private UUID deviceTypeId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_type_id", insertable = false, updatable = false)
    private RdbDeviceType deviceType;

    @Column(name = "parent_device_id", nullable = true)
    private UUID parentDeviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_device_id", insertable = false, updatable = false)
    private RdbDevice parentDevice;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "device")
    private List<RdbDeviceElementMapping> deviceElementMappings = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private List<RdbDeviceGroupElement> groupElementReferences;

    /** Comments */
    @Column(name = "comments")
    private String comments;

    /** Status indicator */
    @Column(name = "status")
    private String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_metadata", joinColumns = @JoinColumn(name = "device_id"))
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
     * @see com.sitewhere.spi.device.IDevice#getDeviceTypeId()
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    /*
     * @see com.sitewhere.spi.device.IDevice#getParentDeviceId()
     */
    @Override
    public UUID getParentDeviceId() {
	return parentDeviceId;
    }

    public void setParentDeviceId(UUID parentDeviceId) {
	this.parentDeviceId = parentDeviceId;
    }

    /*
     * @see com.sitewhere.spi.device.IDevice#getDeviceElementMappings()
     */
    @Override
    public List<RdbDeviceElementMapping> getDeviceElementMappings() {
	return deviceElementMappings;
    }

    public void setDeviceElementMappings(List<RdbDeviceElementMapping> deviceElementMappings) {
	this.deviceElementMappings = deviceElementMappings;
    }

    /*
     * @see com.sitewhere.spi.device.IDevice#getComments()
     */
    @Override
    public String getComments() {
	return comments;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    /*
     * @see com.sitewhere.spi.device.IDevice#getStatus()
     */
    @Override
    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    /*
     * @see
     * com.sitewhere.rdb.entities.RdbPersistentEntity#setMetadata(java.util.Map)
     */
    @Override
    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public RdbDeviceType getDeviceType() {
	return deviceType;
    }

    public RdbDevice getParentDevice() {
	return parentDevice;
    }

    public static void copy(IDevice source, RdbDevice target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getDeviceTypeId() != null) {
	    target.setDeviceTypeId(source.getDeviceTypeId());
	}
	if (source.getParentDeviceId() != null) {
	    target.setParentDeviceId(source.getParentDeviceId());
	}
	if (source.getComments() != null) {
	    target.setComments(source.getComments());
	}
	if (source.getStatus() != null) {
	    target.setStatus(source.getStatus());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
	RdbPersistentEntity.copy(source, target);
    }
}
