/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.rdb.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sitewhere.spi.device.IDeviceElementMapping;

@Entity
@Table(name = "device_element_mapping")
public class RdbDeviceElementMapping implements IDeviceElementMapping {

    /** Serial version UID */
    private static final long serialVersionUID = -7213699772411424925L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_element_schema_path")
    private String deviceElementSchemaPath;

    @Column(name = "device_token")
    private String deviceToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    RdbDevice device;

    public RdbDeviceElementMapping() {
    }

    public RdbDeviceElementMapping(String deviceElementSchemaPath, String deviceToken) {
	this.deviceElementSchemaPath = deviceElementSchemaPath;
	this.deviceToken = deviceToken;
    }

    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceElementMapping#getDeviceElementSchemaPath()
     */
    @Override
    public String getDeviceElementSchemaPath() {
	return deviceElementSchemaPath;
    }

    public void setDeviceElementSchemaPath(String deviceElementSchemaPath) {
	this.deviceElementSchemaPath = deviceElementSchemaPath;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceElementMapping#getDeviceToken()
     */
    @Override
    public String getDeviceToken() {
	return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    public RdbDevice getDevice() {
	return device;
    }

    public void setDevice(RdbDevice device) {
	this.device = device;
    }
}
