/*
 * ProtobufSpecificationBuilder.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.protobuf;

import java.util.List;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.ParameterType;

/**
 * Builds Google Protocol Buffer data structures that allow commands for a specification
 * to be encoded.
 * 
 * @author Derek
 */
public class ProtobufSpecificationBuilder {

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
		builder.setName(ProtobufNaming.getSpecificationIdentifier(specification));
		builder.addEnumType(createCommandsEnum(commands));
		builder.addField(createEnumField());

		int index = 2;
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
		builder.setName(ProtobufNaming.COMMAND_TYPES_ENUM);
		int i = 2;
		for (IDeviceCommand command : commands) {
			DescriptorProtos.EnumValueDescriptorProto.Builder valueBuilder =
					DescriptorProtos.EnumValueDescriptorProto.newBuilder();
			valueBuilder.setName(ProtobufNaming.getCommandEnumName(command));
			valueBuilder.setNumber(i++);
			builder.addValue(valueBuilder.build());
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
	public static DescriptorProtos.FieldDescriptorProto createEnumField() throws SiteWhereException {
		DescriptorProtos.FieldDescriptorProto.Builder builder =
				DescriptorProtos.FieldDescriptorProto.newBuilder().setName(ProtobufNaming.COMMAND_TYPES_FIELD).setNumber(
						1).setTypeName(ProtobufNaming.COMMAND_TYPES_ENUM);
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
						number).setTypeName(ProtobufNaming.getCommandTypeName(command));
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
		builder.setName(ProtobufNaming.getCommandTypeName(command));

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