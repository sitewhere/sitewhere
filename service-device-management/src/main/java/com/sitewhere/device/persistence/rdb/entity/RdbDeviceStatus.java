/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sitewhere.rdb.entities.RdbBrandedEntity;
import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.device.IDeviceStatus;

@Entity
@Table(name = "device_status")
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

    /** Unique id for parent device type */
    @Column(name = "device_type_id")
    private UUID deviceTypeId;

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
	if (source.getIcon() != null) {
	    target.setIcon(source.getIcon());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
	RdbBrandedEntity.copy(source, target);
    }
}
