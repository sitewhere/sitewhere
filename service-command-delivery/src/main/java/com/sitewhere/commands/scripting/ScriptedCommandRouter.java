/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.scripting;

import java.util.Collections;
import java.util.List;

import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.microservice.scripting.ScriptingComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;
import com.sitewhere.spi.microservice.scripting.ScriptScope;
import com.sitewhere.spi.microservice.scripting.ScriptType;

/**
 * Implementation of {@link IOutboundCommandRouter} that uses scripts to perform
 * routing logic.
 */
public class ScriptedCommandRouter extends ScriptingComponent<String> implements IOutboundCommandRouter {

    public ScriptedCommandRouter() {
	super(LifecycleComponentType.CommandRouter);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.IOutboundCommandRouter#getDestinationsFor(com.
     * sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public List<ICommandDestination<?, ?>> getDestinationsFor(IDeviceCommandExecution execution,
	    IDeviceNestingContext nesting, List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	return findAndResolveCommandDestinations(execution, null, nesting, assignments);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.IOutboundCommandRouter#getDestinationsFor(com.
     * sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public List<ICommandDestination<?, ?>> getDestinationsFor(ISystemCommand command, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	return findAndResolveCommandDestinations(null, command, nesting, assignments);
    }

    /**
     * Execute Groovy script and resolve the destination id to a command
     * destination.
     * 
     * @param execution
     * @param system
     * @param nesting
     * @param assignments
     * @return
     * @throws SiteWhereException
     */
    protected List<ICommandDestination<?, ?>> findAndResolveCommandDestinations(IDeviceCommandExecution execution,
	    ISystemCommand system, IDeviceNestingContext nesting, List<? extends IDeviceAssignment> assignments)
	    throws SiteWhereException {
	String target = findCommandDestination(execution, system, nesting, assignments);
	if (target != null) {
	    ICommandDestination<?, ?> destination = getCommandDestinationsManager().getCommandDestinations()
		    .get(target);
	    if (destination == null) {
		throw new SiteWhereException(
			"Groovy command router returned invalid command destination: " + destination);
	    }
	    return Collections.singletonList(destination);
	} else {
	    throw new SiteWhereException("Groovy command router did not return a command destination id.");
	}
    }

    /**
     * Route either a custom command or system command based on logic determined in
     * a Groovy script.
     * 
     * @param execution
     * @param system
     * @param nesting
     * @param assignments
     * @return
     * @throws SiteWhereException
     */
    protected String findCommandDestination(IDeviceCommandExecution execution, ISystemCommand system,
	    IDeviceNestingContext nesting, List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	try {
	    Binding binding = createBindingFor(this);
	    binding.setVariable(IScriptVariables.VAR_COMMAND_EXECUTION, execution);
	    binding.setVariable(IScriptVariables.VAR_SYSTEM_COMMAND, system);
	    binding.setVariable(IScriptVariables.VAR_NESTING_CONTEXT, nesting);
	    binding.setVariable(IScriptVariables.VAR_ACTIVE_ASSIGNMENTS, assignments);
	    return run(ScriptScope.TenantEngine, ScriptType.Managed, binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run router script.", e);
	}
    }

    protected ICommandDestinationsManager getCommandDestinationsManager() {
	return ((ICommandDeliveryTenantEngine) getTenantEngine()).getCommandDestinationsManager();
    }
}