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

import java.util.HashMap;
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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.device.IDeviceStatus;

@Entity
@Table(name = "device_status", uniqueConstraints = @UniqueConstraint(columnNames = { "token" }))
@NamedQuery(name = Queries.QUERY_DEVICE_STATUS_BY_TOKEN, query = "SELECT s FROM RdbDeviceStatus s WHERE s.token = :token")
public class RdbDeviceStatus extends RdbPersistentEntity implements IDeviceStatus {

    /** Serial version UID */
    private static final long serialVersionUID = -8636591489162201615L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Status code */
    @Column(name = "code")
    private String code;

    @Column(name = "device_type_id", nullable = false)
    private UUID deviceTypeId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_type_id", insertable = false, updatable = false)
    private RdbDeviceType deviceType;

    /** Display name */
    @Column(name = "name")
    private String name;

    @Column(name = "background_color")
    private String backgroundColor;

    /** Foreground color */
    @Column(name = "foreground_color")
    private String foregroundColor;

    /** Border color */
    @Column(name = "border_color")
    private String borderColor;

    /** Icon */
    @Column(name = "icon")
    private String icon;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_status_metadata", joinColumns = @JoinColumn(name = "device_status_id"))
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
     * @see com.sitewhere.spi.device.IDeviceStatus#getCode()
     */
    @Override
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceStatus#getDeviceTypeId()
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceStatus#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.common.IColorProvider#getBackgroundColor()
     */
    @Override
    public String getBackgroundColor() {
	return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
	this.backgroundColor = backgroundColor;
    }

    /*
     * @see com.sitewhere.spi.common.IColorProvider#getForegroundColor()
     */
    @Override
    public String getForegroundColor() {
	return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
	this.foregroundColor = foregroundColor;
    }

    /*
     * @see com.sitewhere.spi.common.IColorProvider#getBorderColor()
     */
    @Override
    public String getBorderColor() {
	return borderColor;
    }

    public void setBorderColor(String borderColor) {
	this.borderColor = borderColor;
    }

    /*
     * @see com.sitewhere.spi.common.IIconProvider#getIcon()
     */
    @Override
    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
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

    public static void copy(IDeviceStatus source, RdbDeviceStatus target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getCode() != null) {
	    target.setCode(source.getCode());
	}
	if (source.getDeviceTypeId() != null) {
	    target.setDeviceTypeId(source.getDeviceTypeId());
	}
	if (source.getName() != null) {
	    target.setName(source.getName());
	}
	if (source.getBackgroundColor() != null) {
	    target.setBackgroundColor(source.getBackgroundColor());
	}
	if (source.getForegroundColor() != null) {
	    target.setForegroundColor(source.getForegroundColor());
	}
	if (source.getBorderColor() != null) {
	    target.setBorderColor(source.getBorderColor());
	}
	if (source.getIcon() != null) {
	    target.setIcon(source.getIcon());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
	RdbBrandedEntity.copy(source, target);
    }
}
