/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandNamespace;

/**
 * Used to group device commands that use the same namespace.
 * 
 * @author Derek
 */
public class DeviceCommandNamespace implements IDeviceCommandNamespace {

    /** Serial version UID */
    private static final long serialVersionUID = 4615235653915060558L;

    /** Namespace value */
    private String value;

    /** List of commands */
    private List<IDeviceCommand> commands = new ArrayList<IDeviceCommand>();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.IDeviceCommandNamespace#getValue()
     */
    @Override
    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.command.IDeviceCommandNamespace#getCommands()
     */
    @Override
    public List<IDeviceCommand> getCommands() {
	return commands;
    }

    public void setCommands(List<IDeviceCommand> commands) {
	this.commands = commands;
    }
}