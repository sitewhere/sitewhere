/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.IDeviceStatus;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "device_status")
public class DeviceStatus implements IDeviceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;

    /** DeviceType token */
    @Column(name="device_type_token")
    private String deviceTypeToken;

    /** Status code */
    @Column(name="code")
    private String code;

    /** Unique id for parent device type */
    @Column(name="device_type_id")
    private UUID deviceTypeId;

    /** Display name */
    @Column(name="name")
    private String name;

    @Column(name = "background_color")
    private String backgroundColor;

    /** Foreground color */
    @Column(name="foreground_color")
    private String foregroundColor;

    /** Border color */
    @Column(name="border_color")
    private String borderColor;

    /** Icon */
    @Column(name="icon")
    private String icon;

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
    @Column(name="updated_by")
    private String updatedBy;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="device_status_metadata", joinColumns = @JoinColumn(name = "device_status_id"))
    @MapKeyColumn(name="prop_key")
    @Column(name="prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public String getCode() {
	return code;
    }

    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public String getBackgroundColor() {
	return backgroundColor;
    }

    @Override
    public String getForegroundColor() {
	return foregroundColor;
    }

    @Override
    public String getBorderColor() {
	return borderColor;
    }

    @Override
    public String getIcon() {
	return icon;
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

    public void setCode(String code) {
	this.code = code;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setBackgroundColor(String backgroundColor) {
	this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
	this.foregroundColor = foregroundColor;
    }

    public void setBorderColor(String borderColor) {
	this.borderColor = borderColor;
    }

    public void setIcon(String icon) {
	this.icon = icon;
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

    public String getDeviceTypeToken() {
	return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
	this.deviceTypeToken = deviceTypeToken;
    }
}
