/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.encoding.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
 * 
 * @author Derek
 */
public class JavaHybridProtobufExecutionEncoder extends ProtobufExecutionEncoder {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.communication.protobuf.ProtobufExecutionEncoder#
     * encode(com .sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public byte[] encode(IDeviceCommandExecution execution, IDeviceNestingContext nested, IDeviceAssignment assignment)
	    throws SiteWhereException {
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