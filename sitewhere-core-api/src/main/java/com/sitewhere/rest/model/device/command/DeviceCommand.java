/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;

/**
 * Model object for a command that can be invoked on a device.
 * 
 * @author Derek
 */
public class DeviceCommand extends MetadataProviderEntity implements IDeviceCommand, Serializable {

    /** For Java serialization */
    private static final long serialVersionUID = -9098150828821813365L;

    /** Unique device command id */
    private UUID id;

    /** Unique token for command */
    private String token;

    /** Unique id for parent specification */
    private UUID deviceSpecificationId;

    /** Command namespace */
    private String namespace;

    /** Command name */
    private String name;

    /** Command description */
    private String description;

    /** Parameter list */
    private List<CommandParameter> parameters = new ArrayList<CommandParameter>();

    /*
     * @see com.sitewhere.spi.device.command.IDeviceCommand#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.IDeviceCommand#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see
     * com.sitewhere.spi.device.command.IDeviceCommand#getDeviceSpecificationId()
     */
    @Override
    public UUID getDeviceSpecificationId() {
	return deviceSpecificationId;
    }

    public void setDeviceSpecificationId(UUID deviceSpecificationId) {
	this.deviceSpecificationId = deviceSpecificationId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.IDeviceCommand#getNamespace()
     */
    @Override
    public String getNamespace() {
	return namespace;
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.IDeviceCommand#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.IDeviceCommand#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.IDeviceCommand#getParameters()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ICommandParameter> getParameters() {
	return (List<ICommandParameter>) (List<? extends ICommandParameter>) parameters;
    }

    public void setParameters(List<CommandParameter> parameters) {
	this.parameters = parameters;
    }

    /**
     * Create a copy of an SPI object. Used by web services for marshaling.
     * 
     * @param input
     * @return
     */
    public static DeviceCommand copy(IDeviceCommand input) throws SiteWhereException {
	DeviceCommand result = new DeviceCommand();
	MetadataProviderEntity.copy(input, result);
	result.setId(input.getId());
	result.setToken(input.getToken());
	result.setDeviceSpecificationId(input.getDeviceSpecificationId());
	result.setName(input.getName());
	result.setNamespace(input.getNamespace());
	result.setDescription(input.getDescription());
	for (ICommandParameter inparam : input.getParameters()) {
	    CommandParameter param = new CommandParameter();
	    param.setName(inparam.getName());
	    param.setType(inparam.getType());
	    param.setRequired(inparam.isRequired());
	    result.getParameters().add(param);
	}
	return result;
    }
}