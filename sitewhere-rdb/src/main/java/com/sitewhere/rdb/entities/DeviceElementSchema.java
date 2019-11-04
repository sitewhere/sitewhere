/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.element.IDeviceSlot;
import com.sitewhere.spi.device.element.IDeviceUnit;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "device_element_schema")
public class DeviceElementSchema implements IDeviceElementSchema, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    /** List of device slots */
    @OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.EAGER, mappedBy = "deviceElementSchema")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<DeviceSlot> deviceSlots = new ArrayList();

    /** List of device units */
    @OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.EAGER, mappedBy = "deviceUnitParent")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<DeviceUnit> deviceUnits = new ArrayList();

    @Override
    public List<IDeviceSlot> getDeviceSlots() {
	List<IDeviceSlot> ideviceSlots = new ArrayList<>();
	for(DeviceSlot slot : deviceSlots) {
	    ideviceSlots.add(slot);
	}
	return ideviceSlots;
    }

    @Override
    public List<IDeviceUnit> getDeviceUnits() {
	List<IDeviceUnit> ideviceUnits = new ArrayList<>();
	for(DeviceUnit util : deviceUnits) {
	    ideviceUnits.add(util);
	}
	return ideviceUnits;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public String getPath() {
	return path;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public void setDeviceSlots(List<DeviceSlot> deviceSlots) {
	this.deviceSlots = deviceSlots;
    }

    public void setDeviceUnits(List<DeviceUnit> deviceUnits) {
	this.deviceUnits = deviceUnits;
    }

    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }
}
