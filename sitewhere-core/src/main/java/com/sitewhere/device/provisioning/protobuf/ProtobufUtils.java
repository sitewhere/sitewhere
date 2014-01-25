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

import org.apache.log4j.Logger;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.sitewhere.spi.SiteWhereException;
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

	/**
	 * Create a {@link DynamicMessage} based on an {@link IDeviceCommandExecution}.
	 * 
	 * @param execution
	 * @return
	 * @throws SiteWhereException
	 */
	public static DynamicMessage createMessage(IDeviceCommandExecution execution) throws SiteWhereException {
		DescriptorProtos.DescriptorProto dproto = ProtobufUtils.createDescriptor(execution.getCommand());
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
	 * Create a descriptor from an {@link IDeviceCommand}.
	 * 
	 * @param command
	 * @return
	 * @throws SiteWhereException
	 */
	public static DescriptorProtos.DescriptorProto createDescriptor(IDeviceCommand command)
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
		case Boolean:
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