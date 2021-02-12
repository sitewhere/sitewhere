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
import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.device.command.IDeviceCommand;

@Entity
@Table(name = "device_command", uniqueConstraints = @UniqueConstraint(columnNames = { "token" }))
@NamedQuery(name = Queries.QUERY_DEVICE_COMMAND_BY_TOKEN, query = "SELECT c FROM RdbDeviceCommand c WHERE c.token = :token")
public class RdbDeviceCommand extends RdbPersistentEntity implements IDeviceCommand {

    /** Serial version UID */
    private static final long serialVersionUID = 7140127377676943042L;

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

    /** Command namespace */
    @Column(name = "namespace")
    private String namespace;

    /** Command name */
    @Column(name = "name")
    private String name;

    /** Command description */
    @Column(name = "description")
    private String description;

    /** Parameter list */
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "deviceCommand")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<RdbCommandParameter> parameters = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_command_metadata", joinColumns = @JoinColumn(name = "device_command_id"))
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
     * @see com.sitewhere.spi.device.command.IDeviceCommand#getDeviceTypeId()
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    /*
     * @see com.sitewhere.spi.device.command.IDeviceCommand#getNamespace()
     */
    @Override
    public String getNamespace() {
	return namespace;
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
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
     * @see com.sitewhere.spi.device.command.IDeviceCommand#getParameters()
     */
    @Override
    public List<RdbCommandParameter> getParameters() {
	return parameters;
    }

    public void setParameters(List<RdbCommandParameter> parameters) {
	this.parameters = parameters;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
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

    public static void copy(IDeviceCommand source, RdbDeviceCommand target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getDeviceTypeId() != null) {
	    target.setDeviceTypeId(source.getDeviceTypeId());
	}
	if (source.getNamespace() != null) {
	    target.setNamespace(source.getNamespace());
	}
	if (source.getName() != null) {
	    target.setName(source.getName());
	}
	if (source.getDescription() != null) {
	    target.setDescription(source.getDescription());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
	RdbBrandedEntity.copy(source, target);
    }
}
