/*
 * ProtobufExecutionEncoder.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.protobuf;

import org.apache.log4j.Logger;

import com.google.protobuf.DynamicMessage;
import com.sitewhere.core.DataUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.provisioning.ICommandExecutionEncoder;

/**
 * Implementation of {@link ICommandExecutionEncoder} that uses Google Protocol Buffers to
 * encode the execution.
 * 
 * @author Derek
 */
public class ProtobufExecutionEncoder implements ICommandExecutionEncoder {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ProtobufExecutionEncoder.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandExecutionEncoder#encode(com.sitewhere
	 * .spi.device.command.IDeviceCommandExecution)
	 */
	@Override
	public byte[] encode(IDeviceCommandExecution execution) throws SiteWhereException {
		DynamicMessage message = ProtobufUtils.createMessage(execution);
		LOGGER.debug("Protobuf message: 0x" + DataUtils.bytesToHex(message.toByteArray()));
		return message.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Started Google Protocol Buffer execution encoder.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Stopped Google Protocol Buffer execution encoder");
	}
}