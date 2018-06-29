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
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Routes commands to one or more {@link ICommandDestination} implementations.
 * 
 * @author Derek
 */
public interface IOutboundCommandRouter extends ITenantEngineLifecycleComponent {

    /**
     * Compute list of destinations for the given command invocation.
     * 
     * @param execution
     * @param nesting
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    public List<ICommandDestination<?, ?>> getDestinationsFor(IDeviceCommandExecution execution,
	    IDeviceNestingContext nesting, IDeviceAssignment assignment) throws SiteWhereException;

    /**
     * Compute list of destinations for the given system command.
     * 
     * @param command
     * @param nesting
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    public List<ICommandDestination<?, ?>> getDestinationsFor(ISystemCommand command, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException;
}