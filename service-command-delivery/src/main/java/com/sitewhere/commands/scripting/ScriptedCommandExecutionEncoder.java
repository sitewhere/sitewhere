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

/**
 * Implementation of {@link ICommandExecutionEncoder} that defers encoding to a
 * script.
 */
public class ScriptedCommandExecutionEncoder extends ScriptingComponent<byte[]>
	implements ICommandExecutionEncoder<byte[]> {

    public ScriptedCommandExecutionEncoder() {
	super(LifecycleComponentType.CommandExecutionEncoder);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandExecutionEncoder#encode(com.sitewhere.spi.
     * device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public byte[] encode(IDeviceCommandExecution command, IDeviceNestingContext nested,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	try {
	    Binding binding = createBindingFor(this);
	    binding.setVariable(IScriptVariables.VAR_COMMAND_EXECUTION, command);
	    binding.setVariable(IScriptVariables.VAR_NESTING_CONTEXT, nested);
	    binding.setVariable(IScriptVariables.VAR_ACTIVE_ASSIGNMENTS, assignments);
	    return (byte[]) run(binding);
	} catch (SiteWhereException e) {
	    throw new CommandEncodeException("Unable to run encoder script.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandExecutionEncoder#encodeSystemCommand(com.
     * sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public byte[] encodeSystemCommand(ISystemCommand command, IDeviceNestingContext nested,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	try {
	    Binding binding = createBindingFor(this);
	    binding.setVariable(IScriptVariables.VAR_SYSTEM_COMMAND, command);
	    binding.setVariable(IScriptVariables.VAR_NESTING_CONTEXT, nested);
	    binding.setVariable(IScriptVariables.VAR_ACTIVE_ASSIGNMENTS, assignments);
	    return (byte[]) run(binding);
	} catch (SiteWhereException e) {
	    throw new CommandEncodeException("Unable to run encoder script.", e);
	}
    }
}