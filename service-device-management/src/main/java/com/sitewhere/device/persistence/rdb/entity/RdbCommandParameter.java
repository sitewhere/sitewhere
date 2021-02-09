/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
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

    public RdbCommandParameter(ICommandParameter parameter) {
	this.name = parameter.getName();
	this.type = parameter.getType();
	this.required = parameter.isRequired();
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
