/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.spi;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Encodes an {@link IDeviceCommandExecution} into a format that can be
 * transmitted.
 * 
 * @param <T>
 *                format for encoded command. Must be compatible with the
 *                {@link ICommandDeliveryProvider} that will deliver the
 *                command.
 */
public interface ICommandExecutionEncoder<T> extends ITenantEngineLifecycleComponent {

    /**
     * Encodes a command execution.
     * 
     * @param command
     * @param nested
     * @param assignments
     * @return
     * @throws SiteWhereException
     */
    public T encode(IDeviceCommandExecution command, IDeviceNestingContext nested, List<IDeviceAssignment> assignments)
	    throws SiteWhereException;

    /**
     * Encodes a SiteWhere system command.
     * 
     * @param command
     * @param nested
     * @param assignments
     * @return
     * @throws SiteWhereException
     */
    public T encodeSystemCommand(ISystemCommand command, IDeviceNestingContext nested,
	    List<IDeviceAssignment> assignments) throws SiteWhereException;
}