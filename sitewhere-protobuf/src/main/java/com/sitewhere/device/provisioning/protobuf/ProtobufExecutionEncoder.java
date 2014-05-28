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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.sitewhere.core.DataUtils;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.Device.Command;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.Device.Header;
import com.sitewhere.device.provisioning.protobuf.proto.Sitewhere.Device.RegistrationAck;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
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
	 * .spi.device.command.IDeviceCommandExecution, com.sitewhere.spi.device.IDevice,
	 * com.sitewhere.spi.device.IDeviceAssignment)
	 */
	@Override
	public byte[] encode(IDeviceCommandExecution execution, IDeviceNestingContext nested,
			IDeviceAssignment assignment) throws SiteWhereException {
		byte[] encoded = ProtobufMessageBuilder.createMessage(execution, nested, assignment);
		LOGGER.debug("Protobuf message: 0x" + DataUtils.bytesToHex(encoded));
		return encoded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandExecutionEncoder#encodeSystemCommand
	 * (java.lang.Object, com.sitewhere.spi.device.IDeviceNestingContext,
	 * com.sitewhere.spi.device.IDeviceAssignment)
	 */
	@Override
	public byte[] encodeSystemCommand(Object command, IDeviceNestingContext nested,
			IDeviceAssignment assignment) throws SiteWhereException {
		if (command instanceof RegistrationAck) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				Header header = Header.newBuilder().setCommand(Command.REGISTER_ACK).build();
				header.writeDelimitedTo(out);

				((RegistrationAck) command).writeDelimitedTo(out);
				out.close();
				return out.toByteArray();
			} catch (IOException e) {
				throw new SiteWhereException("Unable to marshal regsiter ack to protobuf.", e);
			}
		}
		throw new SiteWhereException("Unable to encode command: " + command.getClass().getName());
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