/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.scripting;

import java.util.List;

import com.sitewhere.commands.spi.CommandEncodeException;
import com.sitewhere.commands.spi.ICommandExecutionEncoder;
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
 * Implementation of {@link ICommandExecutionEncoder} that defers encoding to a
 * script. This implementation is used for command destinations that require a
 * String payload (such as SMS).
 */
public class ScriptedStringCommandExecutionEncoder extends ScriptingComponent<String>
	implements ICommandExecutionEncoder<String> {

    public ScriptedStringCommandExecutionEncoder() {
	super(LifecycleComponentType.CommandExecutionEncoder);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandExecutionEncoder#encode(com.sitewhere.spi.
     * device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public String encode(IDeviceCommandExecution command, IDeviceNestingContext nested,
	    List<IDeviceAssignment> assignments) throws SiteWhereException {
	try {
	    Binding binding = createBindingFor(this);
	    binding.setVariable(IScriptVariables.VAR_COMMAND_EXECUTION, command);
	    binding.setVariable(IScriptVariables.VAR_NESTING_CONTEXT, nested);
	    binding.setVariable(IScriptVariables.VAR_ACTIVE_ASSIGNMENTS, assignments);
	    return (String) run(ScriptScope.TenantEngine, ScriptType.Managed, binding);
	} catch (SiteWhereException e) {
	    throw new CommandEncodeException("Unable to run command encoder script.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandExecutionEncoder#encodeSystemCommand(com.
     * sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public String encodeSystemCommand(ISystemCommand command, IDeviceNestingContext nested,
	    List<IDeviceAssignment> assignments) throws SiteWhereException {
	try {
	    Binding binding = createBindingFor(this);
	    binding.setVariable(IScriptVariables.VAR_SYSTEM_COMMAND, command);
	    binding.setVariable(IScriptVariables.VAR_NESTING_CONTEXT, nested);
	    binding.setVariable(IScriptVariables.VAR_ACTIVE_ASSIGNMENTS, assignments);
	    return (String) run(ScriptScope.TenantEngine, ScriptType.Managed, binding);
	} catch (SiteWhereException e) {
	    throw new CommandEncodeException("Unable to run command encoder script.", e);
	}
    }
}