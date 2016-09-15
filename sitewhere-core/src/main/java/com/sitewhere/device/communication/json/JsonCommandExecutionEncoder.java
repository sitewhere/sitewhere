/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.json;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.communication.ICommandExecutionEncoder;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ICommandExecutionEncoder} that sends commands in
 * JSON format.
 * 
 * @author Derek
 */
public class JsonCommandExecutionEncoder extends TenantLifecycleComponent implements ICommandExecutionEncoder<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public JsonCommandExecutionEncoder() {
	super(LifecycleComponentType.CommandExecutionEncoder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.ICommandExecutionEncoder#encode(
     * com.sitewhere .spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public byte[] encode(IDeviceCommandExecution command, IDeviceNestingContext nested, IDeviceAssignment assignment)
	    throws SiteWhereException {
	EncodedCommandExecution encoded = new EncodedCommandExecution(command, nested, assignment);
	LOGGER.debug("Custom command being encoded:\n\n" + MarshalUtils.marshalJsonAsPrettyString(encoded));
	return MarshalUtils.marshalJson(encoded);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICommandExecutionEncoder#
     * encodeSystemCommand (com.sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public byte[] encodeSystemCommand(ISystemCommand command, IDeviceNestingContext nested,
	    IDeviceAssignment assignment) throws SiteWhereException {
	EncodedCommandExecution encoded = new EncodedCommandExecution(command, nested, assignment);
	LOGGER.debug("System command being encoded:\n\n" + MarshalUtils.marshalJsonAsPrettyString(encoded));
	return MarshalUtils.marshalJson(encoded);
    }
}