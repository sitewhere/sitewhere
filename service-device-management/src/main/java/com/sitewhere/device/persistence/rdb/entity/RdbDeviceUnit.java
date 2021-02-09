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

import java.io.Serializable;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.device.element.IDeviceUnit;

@Entity
@Table(name = "device_unit", uniqueConstraints = @UniqueConstraint(columnNames = { "token" }))
public class RdbDeviceUnit extends RdbPersistentEntity implements IDeviceUnit, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 4677174015568643955L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private RdbDevice deviceUnitParent;

    /** Util name */
    @Column(name = "name")
    private String name;

    /** Util path */
    @Column(name = "path")
    private String path;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "deviceUnit")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<RdbDeviceSlot> deviceSlots = new ArrayList<>();

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "parentId", referencedColumnName = "id")
    private List<RdbDeviceUnit> deviceUnits = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_unit_metadata", joinColumns = @JoinColumn(name = "device_unit_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    public RdbDevice getDeviceUnitParent() {
	return deviceUnitParent;
    }

    public void setDeviceUnitParent(RdbDevice deviceUnitParent) {
	this.deviceUnitParent = deviceUnitParent;
    }

    /*
     * @see com.sitewhere.spi.device.element.IDeviceElement#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.device.element.IDeviceElement#getPath()
     */
    @Override
    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    /*
     * @see com.sitewhere.spi.device.element.IDeviceUnit#getDeviceSlots()
     */
    @Override
    public List<RdbDeviceSlot> getDeviceSlots() {
	return deviceSlots;
    }

    public void setDeviceSlots(List<RdbDeviceSlot> deviceSlots) {
	this.deviceSlots = deviceSlots;
    }

    /*
     * @see com.sitewhere.spi.device.element.IDeviceUnit#getDeviceUnits()
     */
    @Override
    public List<RdbDeviceUnit> getDeviceUnits() {
	return deviceUnits;
    }

    public void setDeviceUnits(List<RdbDeviceUnit> deviceUnits) {
	this.deviceUnits = deviceUnits;
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
}
