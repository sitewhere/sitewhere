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
package com.sitewhere.commands.encoding.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import com.sitewhere.rest.model.device.event.DeviceEventOriginator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Extends {@link ProtobufExecutionEncoder} so that commands and parameter
 * values are encoded as serialized Java arrays that can be unpacked on the
 * device side and executed dynamically with the need for compiling protocol
 * buffer stubs.
 */
public class JavaHybridProtobufExecutionEncoder extends ProtobufExecutionEncoder {

    /*
     * @see
     * com.sitewhere.commands.encoding.protobuf.ProtobufExecutionEncoder#encode(com.
     * sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List)
     */
    @Override
    public byte[] encode(IDeviceCommandExecution execution, IDeviceNestingContext nested,
	    List<? extends IDeviceAssignment> assignments) throws SiteWhereException {
	try {
	    ByteArrayOutputStream encoded = new ByteArrayOutputStream();
	    ObjectOutputStream out = new ObjectOutputStream(encoded);
	    out.writeObject(execution.getCommand().getName());

	    int i = 0;
	    Object[] parameterValues = new Object[execution.getCommand().getParameters().size()];
	    for (ICommandParameter parameter : execution.getCommand().getParameters()) {
		parameterValues[i++] = encodeParameterValue(parameter, execution.getInvocation());
	    }
	    out.writeObject(parameterValues);
	    out.writeObject(new DeviceEventOriginator(execution.getInvocation()));
	    out.flush();
	    return encoded.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to encode command.", e);
	}
    }

    /**
     * Create a Java object that corresponds to the parameter value.
     * 
     * @param parameter
     * @param invocation
     * @return
     * @throws SiteWhereException
     */
    protected Object encodeParameterValue(ICommandParameter parameter, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	String value = invocation.getParameterValues().get(parameter.getName());
	try {
	    switch (parameter.getType()) {
	    case String: {
		return value;
	    }
	    case Bool: {
		return Boolean.parseBoolean(value);
	    }
	    case Bytes: {
		return value.getBytes();
	    }
	    case Double: {
		return Double.parseDouble(value);
	    }
	    case Float: {
		return Float.parseFloat(value);
	    }
	    case Fixed32:
	    case Fixed64:
	    case Int32:
	    case Int64:
	    case SFixed32:
	    case SFixed64:
	    case SInt32:
	    case SInt64:
	    case UInt32:
	    case UInt64: {
		return Integer.parseInt(value);
	    }
	    }
	} catch (Throwable e) {
	    throw new SiteWhereException("Error converting command parameter.", e);
	}
	throw new SiteWhereException("Parameter type could not be encoded: " + parameter.getType());
    }
}