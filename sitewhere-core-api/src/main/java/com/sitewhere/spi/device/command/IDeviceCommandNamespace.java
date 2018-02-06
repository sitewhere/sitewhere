/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.command;

import java.io.Serializable;
import java.util.List;

/**
 * Used for grouping device commands by namespace.
 * 
 * @author Derek
 */
public interface IDeviceCommandNamespace extends Serializable {

    /**
     * Namespace value.
     * 
     * @return
     */
    public String getValue();

    /**
     * List of commands in namespace.
     * 
     * @return
     */
    public List<IDeviceCommand> getCommands();
}