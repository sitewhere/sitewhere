/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.ParameterType;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "command_parameter")
public class CommandParameter implements ICommandParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ParameterType type;

    @Column(name = "required")
    private boolean required;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_command_id", nullable = false)
    private DeviceCommand deviceCommand;

    public CommandParameter() {
    }

    public CommandParameter(UUID id, String name, ParameterType type, boolean required) {
	this.id = id;
	this.name = name;
	this.type = type;
	this.required = required;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public ParameterType getType() {
	return type;
    }

    @Override
    public boolean isRequired() {
	return required;
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

    public void setType(ParameterType type) {
	this.type = type;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }

    public DeviceCommand getDeviceCommand() {
	return deviceCommand;
    }

    public void setDeviceCommand(DeviceCommand deviceCommand) {
	this.deviceCommand = deviceCommand;
    }
}
