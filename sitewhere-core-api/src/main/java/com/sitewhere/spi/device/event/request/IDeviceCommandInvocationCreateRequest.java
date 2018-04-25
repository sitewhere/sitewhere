/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

import java.util.Map;

import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;

/**
 * Interface for arguments needed to create a device command invocation.
 * 
 * @author Derek
 */
public interface IDeviceCommandInvocationCreateRequest extends IDeviceEventCreateRequest {

    /**
     * Get command initiator type.
     * 
     * @return
     */
    public CommandInitiator getInitiator();

    /**
     * Get unique id of command inititator.
     * 
     * @return
     */
    public String getInitiatorId();

    /**
     * Get command target type.
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
     * Get unique token for command to invoke.
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
}