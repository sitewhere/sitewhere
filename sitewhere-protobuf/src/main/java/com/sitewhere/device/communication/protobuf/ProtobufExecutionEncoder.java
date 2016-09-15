/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.ByteString;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.core.DataUtils;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Device.Command;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Device.DeviceStreamAck;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Device.DeviceStreamAckState;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Device.Header;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Device.RegistrationAck;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Device.RegistrationAckError;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Device.RegistrationAckState;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Model;
import com.sitewhere.device.communication.protobuf.proto.Sitewhere.Model.DeviceStreamData;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.IDeviceStreamAckCommand;
import com.sitewhere.spi.device.command.IRegistrationAckCommand;
import com.sitewhere.spi.device.command.IRegistrationFailureCommand;
import com.sitewhere.spi.device.command.ISendDeviceStreamDataCommand;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.communication.ICommandExecutionEncoder;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ICommandExecutionEncoder} that uses Google Protocol
 * Buffers to encode the execution.
 * 
 * @author Derek
 */
public class ProtobufExecutionEncoder extends TenantLifecycleComponent implements ICommandExecutionEncoder<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public ProtobufExecutionEncoder() {
	super(LifecycleComponentType.CommandExecutionEncoder);
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
    public byte[] encode(IDeviceCommandExecution execution, IDeviceNestingContext nested, IDeviceAssignment assignment)
	    throws SiteWhereException {
	byte[] encoded = ProtobufMessageBuilder.createMessage(execution, nested, assignment, getTenant());
	LOGGER.debug("Protobuf message: 0x" + DataUtils.bytesToHex(encoded));
	return encoded;
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
	switch (command.getType()) {
	case RegistrationAck: {
	    IRegistrationAckCommand ack = (IRegistrationAckCommand) command;
	    RegistrationAck.Builder builder = RegistrationAck.newBuilder();
	    switch (ack.getReason()) {
	    case AlreadyRegistered: {
		builder.setState(RegistrationAckState.ALREADY_REGISTERED);
		break;
	    }
	    case NewRegistration: {
		builder.setState(RegistrationAckState.NEW_REGISTRATION);
		break;
	    }
	    }
	    return encodeRegistrationAck(builder.build());
	}
	case RegistrationFailure: {
	    IRegistrationFailureCommand fail = (IRegistrationFailureCommand) command;
	    RegistrationAck.Builder builder = RegistrationAck.newBuilder();
	    builder.setState(RegistrationAckState.REGISTRATION_ERROR);
	    builder.setErrorMessage(fail.getErrorMessage());
	    switch (fail.getReason()) {
	    case NewDevicesNotAllowed: {
		builder.setErrorType(RegistrationAckError.NEW_DEVICES_NOT_ALLOWED);
		break;
	    }
	    case InvalidSpecificationToken: {
		builder.setErrorType(RegistrationAckError.INVALID_SPECIFICATION);
		break;
	    }
	    case SiteTokenRequired: {
		builder.setErrorType(RegistrationAckError.SITE_TOKEN_REQUIRED);
		break;
	    }
	    }
	    return encodeRegistrationAck(builder.build());
	}
	case DeviceStreamAck: {
	    IDeviceStreamAckCommand ack = (IDeviceStreamAckCommand) command;
	    DeviceStreamAck.Builder builder = DeviceStreamAck.newBuilder();
	    builder.setStreamId(ack.getStreamId());
	    switch (ack.getStatus()) {
	    case DeviceStreamCreated: {
		builder.setState(DeviceStreamAckState.STREAM_CREATED);
		break;
	    }
	    case DeviceStreamExists: {
		builder.setState(DeviceStreamAckState.STREAM_EXISTS);
		break;
	    }
	    case DeviceStreamFailed: {
		builder.setState(DeviceStreamAckState.STREAM_FAILED);
		break;
	    }
	    }
	    return encodeDeviceStreamAck(builder.build());
	}
	case SendDeviceStreamData: {
	    ISendDeviceStreamDataCommand send = (ISendDeviceStreamDataCommand) command;
	    Model.DeviceStreamData.Builder builder = Model.DeviceStreamData.newBuilder();
	    builder.setHardwareId(send.getHardwareId());
	    builder.setStreamId(send.getStreamId());
	    builder.setSequenceNumber(send.getSequenceNumber());
	    builder.setData(ByteString.copyFrom(send.getData()));
	    return encodeSendDeviceStreamData(builder.build());
	}
	case DeviceMappingAck: {
	    String json = MarshalUtils.marshalJsonAsPrettyString(command);
	    LOGGER.warn("No protocol buffer encoding implemented for sending device mapping acknowledgement.");
	    LOGGER.info("JSON representation of command is:\n" + json + "\n");
	    return new byte[0];
	}
	}
	throw new SiteWhereException("Unable to encode command: " + command.getClass().getName());
    }

    /**
     * Encode {@link RegistrationAck} as a byte array.
     * 
     * @param ack
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeRegistrationAck(RegistrationAck ack) throws SiteWhereException {
	try {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Header header = Header.newBuilder().setCommand(Command.ACK_REGISTRATION).build();
	    header.writeDelimitedTo(out);

	    ((RegistrationAck) ack).writeDelimitedTo(out);
	    out.close();
	    return out.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to marshal registration ack to protobuf.", e);
	}
    }

    /**
     * Encode {@link DeviceStreamAck} as a byte array.
     * 
     * @param ack
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeDeviceStreamAck(DeviceStreamAck ack) throws SiteWhereException {
	try {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Header header = Header.newBuilder().setCommand(Command.ACK_DEVICE_STREAM).build();
	    header.writeDelimitedTo(out);
	    ack.writeDelimitedTo(out);
	    out.close();
	    return out.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to marshal device stream ack to protobuf.", e);
	}
    }

    /**
     * Encode {@link DeviceStreamData} as byte array.
     * 
     * @param data
     * @return
     * @throws SiteWhereException
     */
    protected byte[] encodeSendDeviceStreamData(Model.DeviceStreamData data) throws SiteWhereException {
	try {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Header header = Header.newBuilder().setCommand(Command.RECEIVE_DEVICE_STREAM_DATA).build();
	    header.writeDelimitedTo(out);
	    data.writeDelimitedTo(out);
	    out.close();
	    return out.toByteArray();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to marshal device stream data chunk to protobuf.", e);
	}
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
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
    }
}