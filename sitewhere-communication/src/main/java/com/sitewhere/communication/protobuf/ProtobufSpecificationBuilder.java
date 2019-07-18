/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.communication.protobuf;

import java.util.List;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.ParameterType;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Builds Google Protocol Buffer data structures that allow commands for a
 * specification to be encoded.
 * 
 * @author Derek
 */
public class ProtobufSpecificationBuilder {

    /**
     * Creates a {@link FileDescriptorProto} based on an {@link IDeviceType}.
     * 
     * @param deviceType
     * @param tenant
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static DescriptorProtos.FileDescriptorProto createFileDescriptor(IDeviceType deviceType, ITenant tenant,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	DescriptorProtos.FileDescriptorProto.Builder builder = DescriptorProtos.FileDescriptorProto.newBuilder();
	builder.addMessageType(createDeviceTypeMessage(deviceType, tenant, deviceManagement));
	return builder.build();
    }

    /**
     * Create the message for a device type.
     * 
     * @param deviceType
     * @param tenant
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static DescriptorProtos.DescriptorProto createDeviceTypeMessage(IDeviceType deviceType, ITenant tenant,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	DeviceCommandSearchCriteria criteria = new DeviceCommandSearchCriteria(1, 0);
	criteria.setDeviceTypeToken(deviceType.getToken());
	ISearchResults<IDeviceCommand> commands = deviceManagement.listDeviceCommands(criteria);

	DescriptorProtos.DescriptorProto.Builder builder = DescriptorProtos.DescriptorProto.newBuilder();
	builder.setName(ProtobufNaming.getDeviceTypeIdentifier(deviceType));
	builder.addEnumType(createCommandsEnum(commands.getResults()));
	builder.addNestedType(createUuidMessage());
	builder.addNestedType(createHeaderMessage());

	for (IDeviceCommand command : commands.getResults()) {
	    builder.addNestedType(createCommandMessage(command)).build();
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
	DescriptorProtos.EnumDescriptorProto.Builder builder = DescriptorProtos.EnumDescriptorProto.newBuilder();
	builder.setName(ProtobufNaming.COMMAND_TYPES_ENUM);
	int i = 1;
	for (IDeviceCommand command : commands) {
	    DescriptorProtos.EnumValueDescriptorProto.Builder valueBuilder = DescriptorProtos.EnumValueDescriptorProto
		    .newBuilder();
	    valueBuilder.setName(ProtobufNaming.getCommandEnumName(command));
	    valueBuilder.setNumber(i++);
	    builder.addValue(valueBuilder.build());
	}
	return builder.build();
    }

    /**
     * Create message that defines a UUID in terms of two int64 fields.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static DescriptorProtos.DescriptorProto createUuidMessage() throws SiteWhereException {
	DescriptorProtos.DescriptorProto.Builder builder = DescriptorProtos.DescriptorProto.newBuilder();
	builder.setName(ProtobufNaming.UUID_MSG_NAME);
	DescriptorProtos.FieldDescriptorProto.Builder lsb = DescriptorProtos.FieldDescriptorProto.newBuilder()
		.setName("lsb").setNumber(1).setType(Type.TYPE_INT64);
	builder.addField(lsb.build());
	DescriptorProtos.FieldDescriptorProto.Builder msb = DescriptorProtos.FieldDescriptorProto.newBuilder()
		.setName("msb").setNumber(2).setType(Type.TYPE_INT64);
	builder.addField(msb.build());
	return builder.build();
    }

    /**
     * Create header message.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static DescriptorProtos.DescriptorProto createHeaderMessage() throws SiteWhereException {
	DescriptorProtos.DescriptorProto.Builder builder = DescriptorProtos.DescriptorProto.newBuilder();
	builder.setName(ProtobufNaming.HEADER_MSG_NAME);
	DescriptorProtos.FieldDescriptorProto.Builder command = DescriptorProtos.FieldDescriptorProto.newBuilder()
		.setName(ProtobufNaming.HEADER_COMMAND_FIELD_NAME).setNumber(1)
		.setTypeName(ProtobufNaming.COMMAND_TYPES_ENUM);
	builder.addField(command.build());
	DescriptorProtos.FieldDescriptorProto.Builder originator = DescriptorProtos.FieldDescriptorProto.newBuilder()
		.setName(ProtobufNaming.HEADER_ORIGINATOR_FIELD_NAME).setNumber(2).setType(Type.TYPE_STRING);
	builder.addField(originator.build());
	DescriptorProtos.FieldDescriptorProto.Builder path = DescriptorProtos.FieldDescriptorProto.newBuilder()
		.setName(ProtobufNaming.HEADER_NESTED_PATH_FIELD_NAME).setNumber(3).setType(Type.TYPE_STRING);
	builder.addField(path.build());
	DescriptorProtos.FieldDescriptorProto.Builder target = DescriptorProtos.FieldDescriptorProto.newBuilder()
		.setName(ProtobufNaming.HEADER_NESTED_TYPE_FIELD_NAME).setNumber(4).setType(Type.TYPE_STRING);
	builder.addField(target.build());
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
    public static DescriptorProtos.FieldDescriptorProto createField(ICommandParameter parameter, int fieldNumber)
	    throws SiteWhereException {
	DescriptorProtos.FieldDescriptorProto.Builder builder = DescriptorProtos.FieldDescriptorProto.newBuilder()
		.setName(parameter.getName()).setNumber(fieldNumber).setType(getType(parameter.getType()));
	return builder.build();
    }

    /**
     * Gets the protobuf parameter type based on SiteWhere parameter type.
     * 
     * @param param
     * @return
     * @throws SiteWhereException
     */
    public static DescriptorProtos.FieldDescriptorProto.Type getType(ParameterType param) throws SiteWhereException {
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