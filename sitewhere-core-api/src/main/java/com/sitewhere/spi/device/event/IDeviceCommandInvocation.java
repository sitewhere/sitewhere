/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.Map;

/**
 * Captures information about the invocation of a command.
 * 
 * @author Derek
 */
public interface IDeviceCommandInvocation extends IDeviceEvent {

    /**
     * Get actor type that initiated the command.
     * 
     * @return
     */
    public CommandInitiator getInitiator();

    /**
     * Get unique id of command initiated.
     * 
     * @return
     */
    public String getInitiatorId();

    /**
     * Get actor type that received command.
     * 
     * @return
     */
    public CommandTarget getTarget();

    /**
     * Get unique id of command target.
     * 
     * @return
     */
    public String getTargetId();

    /**
     * Get the unique token of the command to be executed.
     * 
     * @return
     */
    public String getCommandToken();

    /**
     * Get the list of parameter names mapped to values.
     * 
     * @return
     */
    public Map<String, String> getParameterValues();

    /**
     * Get status of command.
     * 
     * @return
     */
    public CommandStatus getStatus();
}