/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.command;

import java.io.Serializable;
import java.util.Map;

import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Represents an {@link IDeviceCommand} combined with an
 * {@link IDeviceCommandInvocation} to represent the actual call made to the
 * target.
 * 
 * @author Derek
 * 
 */
public interface IDeviceCommandExecution extends Serializable {

    /**
     * Get the command being executed.
     * 
     * @return
     */
    public IDeviceCommand getCommand();

    /**
     * Get the invocation details.
     * 
     * @return
     */
    public IDeviceCommandInvocation getInvocation();

    /**
     * Get parameters populated with data from the invocation.
     * 
     * @return
     */
    public Map<String, Object> getParameters();
}