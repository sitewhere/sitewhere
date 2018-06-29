/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.groovy;

import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
import com.sitewhere.groovy.IGroovyVariables;
import com.sitewhere.microservice.groovy.GroovyComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;

/**
 * Implementation of {@link IOutboundCommandRouter} that uses Groovy scripts to
 * perform routing logic.
 * 
 * @author Derek
 */
public class GroovyCommandRouter extends GroovyComponent implements IOutboundCommandRouter {

    public GroovyCommandRouter() {
	super(LifecycleComponentType.CommandRouter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IOutboundCommandRouter#
     * routeCommand(com.sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public void routeCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException {
	route(execution, null, nesting, assignment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IOutboundCommandRouter#
     * routeSystemCommand(com.sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public void routeSystemCommand(ISystemCommand command, IDeviceNestingContext nesting, IDeviceAssignment assignment)
	    throws SiteWhereException {
	route(null, command, nesting, assignment);
    }

    /**
     * Route either a custom command or system command based on logic determined in
     * a Groovy script.
     * 
     * @param execution
     * @param system
     * @param nesting
     * @param assignment
     * @throws SiteWhereException
     */
    protected void route(IDeviceCommandExecution execution, ISystemCommand system, IDeviceNestingContext nesting,
	    IDeviceAssignment assignment) throws SiteWhereException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_COMMAND_EXECUTION, execution);
	    binding.setVariable(IGroovyVariables.VAR_SYSTEM_COMMAND, system);
	    binding.setVariable(IGroovyVariables.VAR_NESTING_CONTEXT, nesting);
	    binding.setVariable(IGroovyVariables.VAR_ASSIGNMENT, assignment);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, getLogger());
	    getLogger().debug("About to route command using script '" + getScriptId() + "'");
	    String target = (String) run(binding);
	    if (target != null) {
		ICommandDestination<?, ?> destination = getCommandDestinationsManager().getCommandDestinations()
			.get(target);
		if (destination != null) {
		    if (execution != null) {
			destination.deliverCommand(execution, nesting, assignment);
		    } else if (system != null) {
			destination.deliverSystemCommand(system, nesting, assignment);
		    }
		} else {
		    getLogger().warn("Command attempting to route to unknown destination: " + target);
		}
	    } else {
		getLogger().warn("Groovy command router did not return a command destination id.");
	    }
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run router script.", e);
	}
    }

    protected ICommandDestinationsManager getCommandDestinationsManager() {
	return ((ICommandDeliveryTenantEngine) getTenantEngine()).getCommandDestinationsManager();
    }
}