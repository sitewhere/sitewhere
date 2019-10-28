/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.IDeviceElementMapping;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "device_element_mapping")
public class DeviceElementMapping implements IDeviceElementMapping {

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
    Device device;

    public DeviceElementMapping() {
    }

    public DeviceElementMapping(String deviceElementSchemaPath, String deviceToken) {
        this.deviceElementSchemaPath = deviceElementSchemaPath;
        this.deviceToken = deviceToken;
    }

    @Override
    public String getDeviceElementSchemaPath() {
        return deviceElementSchemaPath;
    }

    @Override
    public String getDeviceToken() {
        return deviceToken;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDeviceElementSchemaPath(String deviceElementSchemaPath) {
        this.deviceElementSchemaPath = deviceElementSchemaPath;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
