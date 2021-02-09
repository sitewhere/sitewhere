/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	    ICommandDestination<?, ?> destination = getCommandDestinationsManager().getCommandDestinationsMap()
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
	    return run(binding);
	} catch (SiteWhereException e) {
	    throw new SiteWhereException("Unable to run router script.", e);
	}
    }

    protected ICommandDestinationsManager getCommandDestinationsManager() {
	return ((ICommandDeliveryTenantEngine) getTenantEngine()).getCommandDestinationsManager();
    }
}