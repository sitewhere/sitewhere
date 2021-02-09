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
package com.sitewhere.commands;

import com.sitewhere.commands.spi.ICommandExecutionBuilder;
import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.rest.model.device.command.DeviceCommandExecution;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Default implementation of the {@link ICommandExecutionBuilder} interface that
 * handles the basic task of merging {@link IDeviceCommand} and
 * {@link IDeviceCommandInvocation} information to produce an
 * {@link IDeviceCommandExecution} that can be encoded and sent to a target.
 */
public class DefaultCommandExecutionBuilder extends LifecycleComponent implements ICommandExecutionBuilder {

    public DefaultCommandExecutionBuilder() {
	super(LifecycleComponentType.CommandExecutionBuilder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandExecutionBuilder#
     * createExecution (com.sitewhere.spi.device.command.IDeviceCommand,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public IDeviceCommandExecution createExecution(IDeviceCommand command, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	DeviceCommandExecution execution = new DeviceCommandExecution();
	execution.setCommand(command);
	execution.setInvocation(invocation);
	generateParameters(execution);
	return execution;
    }

    /**
     * Generate a parameters map based on information from the command and
     * invocation.
     * 
     * @param execution
     * @throws SiteWhereException
     */
    protected void generateParameters(IDeviceCommandExecution execution) throws SiteWhereException {
	execution.getParameters().clear();
	for (ICommandParameter parameter : execution.getCommand().getParameters()) {
	    String paramValue = execution.getInvocation().getParameterValues().get(parameter.getName());
	    boolean parameterValueIsNull = (paramValue == null);
	    boolean parameterValueIsEmpty = true;

	    if (!parameterValueIsNull) {
		paramValue = paramValue.trim();
		parameterValueIsEmpty = paramValue.length() == 0;
	    }

	    // Handle the required parameters first
	    if (parameter.isRequired()) {
		if (parameterValueIsNull) {
		    throw new SiteWhereSystemException(ErrorCode.RequiredCommandParameterMissing, ErrorLevel.ERROR);
		}

		if (parameterValueIsEmpty) {
		    throw new SiteWhereSystemException(ErrorCode.RequiredCommandParameterValueMissing,
			    ErrorLevel.ERROR);
		}
	    } else if (parameterValueIsNull || parameterValueIsEmpty) {
		continue;
	    }

	    Object converted = null;
	    switch (parameter.getType()) {
	    case Bool: {
		converted = Boolean.parseBoolean(paramValue);
		break;
	    }
	    case String: {
		converted = paramValue;
		break;
	    }
	    case Bytes: {
		converted = String.valueOf(converted).getBytes();
		break;
	    }
	    case Double: {
		try {
		    converted = Double.parseDouble(paramValue);
		} catch (NumberFormatException e) {
		    throw new SiteWhereException(
			    "Field '" + parameter.getName() + "' contains a value that can not be parsed as a double.");
		}
		break;
	    }
	    case Float: {
		try {
		    converted = Float.parseFloat(paramValue);
		} catch (NumberFormatException e) {
		    throw new SiteWhereException(
			    "Field '" + parameter.getName() + "' contains a value that can not be parsed as a float.");
		}
		break;
	    }
	    case Int32:
	    case UInt32:
	    case SInt32:
	    case Fixed32:
	    case SFixed32: {
		try {
		    converted = Integer.parseInt(paramValue);
		} catch (NumberFormatException e) {
		    throw new SiteWhereException("Field '" + parameter.getName()
			    + "' contains a value that can not be parsed as an integer.");
		}
		break;
	    }
	    case Int64:
	    case UInt64:
	    case SInt64:
	    case Fixed64:
	    case SFixed64: {
		try {
		    converted = Long.parseLong(paramValue);
		} catch (NumberFormatException e) {
		    throw new SiteWhereException(
			    "Field '" + parameter.getName() + "' contains a value that can not be parsed as an long.");
		}
		break;
	    }
	    default: {
		throw new SiteWhereException("Unhandled parameter type: " + parameter.getType().name());
	    }

	    }
	    if (converted != null) {
		execution.getParameters().put(parameter.getName(), converted);
	    }
	}
    }
}