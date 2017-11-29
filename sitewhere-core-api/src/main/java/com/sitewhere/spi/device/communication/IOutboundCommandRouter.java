/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.communication;

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
     * Initialize the router with destination information.
     * 
     * @param destinations
     * @throws SiteWhereException
     */
    public void initialize(List<ICommandDestination<?, ?>> destinations) throws SiteWhereException;

    /**
     * Route a command to one of the available destinations.
     * 
     * @param execution
     * @param nesting
     * @param assignment
     * @throws SiteWhereException
     */
    public void routeCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException;

    /**
     * Route a system command to one of the available destinations.
     * 
     * @param command
     * @param nesting
     * @param assignment
     * @throws SiteWhereException
     */
    public void routeSystemCommand(ISystemCommand command, IDeviceNestingContext nesting, IDeviceAssignment assignment)
	    throws SiteWhereException;
}