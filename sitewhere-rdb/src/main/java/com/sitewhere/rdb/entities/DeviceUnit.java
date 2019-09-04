/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.element.IDeviceSlot;
import com.sitewhere.spi.device.element.IDeviceUnit;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "device_util")
public class DeviceUnit implements IDeviceUnit, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Util name */
    private String name;

    /** Util path */
    private String path;

    @OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.LAZY)
    private List<DeviceSlot> deviceSlots = new ArrayList<>();

    @OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.LAZY)
    private List<DeviceUnit> deviceUnits = new ArrayList<>();

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

    @Override
    public List<IDeviceSlot> getDeviceSlots() {
        List<IDeviceSlot> list = new ArrayList<>();
        for(DeviceSlot ds : deviceSlots) {
            list.add(ds);
        }
        return list;
    }

    @Override
    public List<IDeviceUnit> getDeviceUnits() {
        List<IDeviceUnit> list = new ArrayList<>();
        for(DeviceUnit du : deviceUnits) {
            list.add(du);
        }
        return list;
    }

    public void setDeviceSlots(List<DeviceSlot> deviceSlots) {
        this.deviceSlots = deviceSlots;
    }

    public void setDeviceUnits(List<DeviceUnit> deviceUnits) {
        this.deviceUnits = deviceUnits;
    }
}
