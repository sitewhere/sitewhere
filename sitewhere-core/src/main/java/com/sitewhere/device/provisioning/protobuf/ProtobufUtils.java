/*
 * ProtobufUtils.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.protobuf;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ParameterType;

/**
 * Utility methods for using Google Protocol Buffers with SiteWhere.
 * 
 * @author Derek
 */
public class ProtobufUtils {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ProtobufUtils.class);

	/** Name of enum for message types */
	private static final String COMMAND_PREFIX = "COMMAND_";

	/** Name of enum for message types */
	private static final String COMMAND_TYPES_ENUM = "MessageType";

	/** Name of message types enum field */
	private static final String COMMAND_TYPES_FIELD = "type";

	/**
	 * Create a {@link DynamicMessage} based on an {@link IDeviceCommandExecution}.
	 * 
	 * @param execution
	 * @return
	 * @throws SiteWhereException
	 */
	public static DynamicMessage createMessage(IDeviceCommandExecution execution) throws SiteWhereException {
		DescriptorProtos.DescriptorProto dproto = ProtobufUtils.createCommandMessage(execution.getCommand());
		DescriptorProtos.FileDescriptorProto fdproto =
				DescriptorProtos.FileDescriptorProto.newBuilder().addMessageType(dproto).build();

		Descriptors.FileDescriptor[] fdescs = new Descriptors.FileDescriptor[0];
		try {
			Descriptors.FileDescriptor filedesc = Descriptors.FileDescriptor.buildFrom(fdproto, fdescs);
			Descriptors.Descriptor mdesc = filedesc.findMessageTypeByName(execution.getCommand().getName());
			DynamicMessage.Builder dmbuilder = DynamicMessage.newBuilder(mdesc);
			for (String name : execution.getParameters().keySet()) {
				Object value = execution.getParameters().get(name);
				try {
					dmbuilder.setField(mdesc.findFieldByName(name), value);
				} catch (IllegalArgumentException iae) {
					LOGGER.error("Error setting field '" + name + "' with object of type: "
							+ value.getClass().getName(), iae);
				}
			}
			return dmbuilder.build();
		} catch (Descriptors.DescriptorValidationException e) {
			throw new SiteWhereException("Unable to create protobuf message.", e);
		}
	}

	/**
	 * Dump the descriptor for a specification.
	 * 
	 * @param specification
	 * @throws SiteWhereException
	 */
	public static void dumpFileDescriptor(IDeviceSpecification specification) throws SiteWhereException {
		DescriptorProtos.FileDescriptorProto descriptor = createFileDescriptor(specification);
		System.out.println("\n");
		System.out.println(descriptor.toString());
		System.out.println("\n");
	}

	/**
	 * Creates a {@link FileDescriptorProto} based on an {@link IDeviceSpecification}.
	 * 
	 * @param specification
	 * @return
	 * @throws SiteWhereException
	 */
	public static DescriptorProtos.FileDescriptorProto createFileDescriptor(IDeviceSpecification specification)
			throws SiteWhereException {
		DescriptorProtos.FileDescriptorProto.Builder builder =
				DescriptorProtos.FileDescriptorProto.newBuilder();
		builder.addMessageType(createSpecificationMessage(specification));
		return builder.build();
	}

	/**
	 * Create the message for a specification.
	 * 
	 * @param specification
	 * @return
	 * @throws SiteWhereException
	 */
	public static DescriptorProtos.DescriptorProto createSpecificationMessage(
			IDeviceSpecification specification) throws SiteWhereException {
		List<IDeviceCommand> commands =
				SiteWhereServer.getInstance().getDeviceManagement().listDeviceCommands(
						specification.getToken(), false);
		DescriptorProtos.DescriptorProto.Builder builder = DescriptorProtos.DescriptorProto.newBuilder();
		builder.setName(specification.getName());
		builder.addEnumType(createCommandsEnum(commands));
		builder.addField(createEnumField());

		int index = 1;
		for (IDeviceCommand command : commands) {
			builder.addNestedType(createCommandMessage(command)).build();
			builder.addField(createCommandField(command, index++));
		}

		return builder.build();
	}

	/**
	 * Create an enum that lists all commands.
	 * 
	 * @param commands
	 * @return
	 * @throws SiteWhereException
	 */
	public static DescriptorProtos.EnumDescriptorProto createCommandsEnum(List<IDeviceCommand> commands)
			throws SiteWhereException {
		DescriptorProtos.EnumDescriptorProto.Builder builder =
				DescriptorProtos.EnumDescriptorProto.newBuilder();
		builder.setName(COMMAND_TYPES_ENUM);
		int i = 1;
		for (IDeviceCommand command : commands) {
			DescriptorProtos.EnumValueDescriptorProto.Builder valueBuilder =
					DescriptorProtos.EnumValueDescriptorProto.newBuilder();
			valueBuilder.setName(getCommandType(command));
			valueBuilder.setNumber(i++);
			builder.addValue(valueBuilder.build());
		}
		return builder.build();
	}

	/**
	 * Get the command type identifier.
	 * 
	 * @param command
	 * @return
	 */
	protected static String getCommandType(IDeviceCommand command) {
		return COMMAND_PREFIX + command.getName().toUpperCase();
	}

	/**
	 * Create field for a parameter.
	 * 
	 * @param parameter
	 * @param fieldNumber
	 * @return
	 * @throws SiteWhereException
	 */
	public static DescriptorProtos.FieldDescriptorProto createEnumField() throws SiteWhereException {
		DescriptorProtos.FieldDescriptorProto.Builder builder =
				DescriptorProtos.FieldDescriptorProto.newBuilder().setName(COMMAND_TYPES_FIELD).setNumber(1).setTypeName(
						COMMAND_TYPES_ENUM);
		return builder.build();
	}

	/**
	 * Create field that references command type.
	 * 
	 * @param command
	 * @param number
	 * @return
	 * @throws SiteWhereException
	 */
	public static DescriptorProtos.FieldDescriptorProto createCommandField(IDeviceCommand command, int number)
			throws SiteWhereException {
		DescriptorProtos.FieldDescriptorProto.Builder builder =
				DescriptorProtos.FieldDescriptorProto.newBuilder().setName(command.getName()).setNumber(
						number).setTypeName(getCommandType(command));
		return builder.build();
	}

	/**
	 * Create a descriptor from an {@link IDeviceCommand}.
	 * 
	 * @param command
	 * @return
	 * @throws SiteWhereException
	 */
	public static DescriptorProtos.DescriptorProto createCommandMessage(IDeviceCommand command)
			throws SiteWhereException {
		DescriptorProtos.DescriptorProto.Builder builder = DescriptorProtos.DescriptorProto.newBuilder();
		builder.setName(command.getName());

		int i = 0;
		for (ICommandParameter parameter : command.getParameters()) {
			i++;
			DescriptorProtos.FieldDescriptorProto field = createField(parameter, i);
			builder.addField(field);
		}
		return builder.build();
	}

	/**
	 * Create field for a parameter.
	 * 
	 * @param parameter
	 * @param fieldNumber
	 * @return
	 * @throws SiteWhereException
	 */
	public static DescriptorProtos.FieldDescriptorProto createField(ICommandParameter parameter,
			int fieldNumber) throws SiteWhereException {
		DescriptorProtos.FieldDescriptorProto.Builder builder =
				DescriptorProtos.FieldDescriptorProto.newBuilder().setName(parameter.getName()).setNumber(
						fieldNumber).setType(getType(parameter.getType()));
		return builder.build();
	}

	/**
	 * Gets the protobuf parameter type based on SiteWhere parameter type.
	 * 
	 * @param param
	 * @return
	 * @throws SiteWhereException
	 */
	public static DescriptorProtos.FieldDescriptorProto.Type getType(ParameterType param)
			throws SiteWhereException {
		switch (param) {
		case Bool:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL;
		case Bytes:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES;
		case Double:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE;
		case Fixed32:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED32;
		case Fixed64:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED64;
		case Float:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT;
		case Int32:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32;
		case Int64:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64;
		case SFixed32:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED32;
		case SFixed64:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED64;
		case SInt32:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32;
		case SInt64:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64;
		case String:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING;
		case UInt32:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT32;
		case UInt64:
			return DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT64;
		default:
			throw new SiteWhereException("Unknown parameter type: " + param.name());
		}
	}
}