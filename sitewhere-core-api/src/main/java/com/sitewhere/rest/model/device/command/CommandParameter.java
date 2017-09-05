/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

import java.io.Serializable;

import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.ParameterType;

/**
 * Model object for a command parameter.
 * 
 * @author Derek
 */
public class CommandParameter implements ICommandParameter, Serializable {

    /** For Java serialization */
    private static final long serialVersionUID = -4689464499966528051L;

    /** Command name */
    private String name;

    /** Datatype */
    private ParameterType type;

    /** Indicates whether required */
    private boolean required;

    public CommandParameter() {
    }

    public CommandParameter(String name, ParameterType type, boolean required) {
	this.name = name;
	this.type = type;
	this.required = required;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.ICommandParameter#getName()
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
     * @see com.sitewhere.spi.device.command.ICommandParameter#getType()
     */
    @Override
    public ParameterType getType() {
	return type;
    }

    public void setType(ParameterType type) {
	this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.ICommandParameter#isRequired()
     */
    @Override
    public boolean isRequired() {
	return required;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }
}