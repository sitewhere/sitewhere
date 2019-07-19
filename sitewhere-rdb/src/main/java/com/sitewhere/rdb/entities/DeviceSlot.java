package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.element.IDeviceSlot;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "device_alot")
public class DeviceSlot implements IDeviceSlot, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Name */
    private String name;

    /** Path */
    private String path;

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
}
