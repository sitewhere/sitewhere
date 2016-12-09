/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.List;
import java.util.Map;

import com.sitewhere.spi.device.command.ICommandParameter;

/**
 * Interface for arguments needed to create a device command.
 * 
 * @author Derek
 */
public interface IDeviceCommandCreateRequest {

    /**
     * Get unique command token.
     * 
     * @return
     */
    public String getToken();

    /**
     * Optional namespace for distinguishing commands.
     * 
     * @return
     */
    public String getNamespace();

    /**
     * Get command name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get command description.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get list of parameters.
     * 
     * @return
     */
    public List<ICommandParameter> getParameters();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}