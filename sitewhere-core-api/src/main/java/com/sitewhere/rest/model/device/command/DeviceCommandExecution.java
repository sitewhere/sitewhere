/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Default implementation of the {@link IDeviceCommandExecution} interface.
 * 
 * @author Derek
 */
public class DeviceCommandExecution implements IDeviceCommandExecution {

    /** Serial version UID */
    private static final long serialVersionUID = 2511870502556301534L;

    /** Command being executed */
    private IDeviceCommand command;

    /** Command invocation details */
    private IDeviceCommandInvocation invocation;

    /** Map of parameter names to values calculated from invocation */
    private Map<String, Object> parameters = new HashMap<String, Object>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.command.IDeviceCommandExecution#getCommand()
     */
    public IDeviceCommand getCommand() {
	return command;
    }

    public void setCommand(IDeviceCommand command) {
	this.command = command;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.command.IDeviceCommandExecution#getInvocation()
     */
    public IDeviceCommandInvocation getInvocation() {
	return invocation;
    }

    public void setInvocation(IDeviceCommandInvocation invocation) {
	this.invocation = invocation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.command.IDeviceCommandExecution#getParameters()
     */
    public Map<String, Object> getParameters() {
	return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
	this.parameters = parameters;
    }
}