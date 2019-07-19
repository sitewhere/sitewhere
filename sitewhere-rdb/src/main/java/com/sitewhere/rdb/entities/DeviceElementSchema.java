package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.element.IDeviceSlot;
import com.sitewhere.spi.device.element.IDeviceUnit;

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
    private UUID id;

    private String name;

    private String path;

    /** List of device slots */
    @OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.LAZY)
    private List<DeviceSlot> deviceSlots = new ArrayList<DeviceSlot>();

    /** List of device units */
    @OneToMany(cascade= {CascadeType.ALL},fetch=FetchType.LAZY)
    private List<DeviceUnit> deviceUnits = new ArrayList<DeviceUnit>();

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
