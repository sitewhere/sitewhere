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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.ParameterType;

@Entity
@Table(name = "command_parameter")
public class RdbCommandParameter implements ICommandParameter {

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
    private RdbDeviceCommand deviceCommand;

    public RdbCommandParameter() {
    }

    public RdbCommandParameter(UUID id, String name, ParameterType type, boolean required) {
	this.id = id;
	this.name = name;
	this.type = type;
	this.required = required;
    }

    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public ParameterType getType() {
	return type;
    }

    public void setType(ParameterType type) {
	this.type = type;
    }

    @Override
    public boolean isRequired() {
	return required;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }

    public RdbDeviceCommand getDeviceCommand() {
	return deviceCommand;
    }

    public void setDeviceCommand(RdbDeviceCommand deviceCommand) {
	this.deviceCommand = deviceCommand;
    }
}
