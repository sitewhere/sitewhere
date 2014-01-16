/*
 * DeviceCommand.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;

/**
 * Model object for a command that can be invoked on a device.
 * 
 * @author Derek
 */
public class DeviceCommand extends MetadataProviderEntity implements IDeviceCommand {

	/** Unique token for command */
	private String token;

	/** Token for parent specification */
	private String specificationToken;

	/** Command namespace */
	private String namespace;

	/** Command name */
	private String name;

	/** Parameter list */
	private List<CommandParameter> parameters = new ArrayList<CommandParameter>();

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
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.command.IDeviceCommand#getSpecificationToken()
	 */
	@Override
	public String getSpecificationToken() {
		return specificationToken;
	}

	public void setSpecificationToken(String specificationToken) {
		this.specificationToken = specificationToken;
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
}