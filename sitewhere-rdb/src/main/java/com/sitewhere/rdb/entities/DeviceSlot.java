/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.element.IDeviceSlot;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "device_slot")
public class DeviceSlot implements IDeviceSlot, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Name */
    @Column(name = "name")
    private String name;

    /** Path */
    @Column(name = "path")
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_unit_id", nullable = true)
    DeviceUnit deviceUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_element_schema_id", nullable = true)
    DeviceElementSchema deviceElementSchema;

    @Override
    public String getName() {
	return name;
    }

    @Override
    public String getPath() {
	return path;
    }

    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public DeviceUnit getDeviceUnit() {
	return deviceUnit;
    }

    public void setDeviceUnit(DeviceUnit deviceUnit) {
	this.deviceUnit = deviceUnit;
    }

    public DeviceElementSchema getDeviceElementSchema() {
	return deviceElementSchema;
    }

    public void setDeviceElementSchema(DeviceElementSchema deviceElementSchema) {
	this.deviceElementSchema = deviceElementSchema;
    }
}
