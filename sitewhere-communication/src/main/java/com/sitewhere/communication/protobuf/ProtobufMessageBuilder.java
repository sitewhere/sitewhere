/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.communication.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Produces an encoded message based on Google Protocol Buffer derived from an
 * {@link IDeviceType}.
 * 
 * @author Derek
 */
public class ProtobufMessageBuilder {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(ProtobufMessageBuilder.class);

    /**
     * Create a protobuf message for an {@link IDeviceCommandExecution}.
     * 
     * @param execution
     * @param nested
     * @param assignments
     * @param tenant
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static byte[] createMessage(IDeviceCommandExecution execution, IDeviceNestingContext nested,
	    List<IDeviceAssignment> assignments, ITenant tenant, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	IDeviceType deviceType = deviceManagement.getDeviceType(execution.getCommand().getDeviceTypeId());
	DescriptorProtos.FileDescriptorProto fdproto = getFileDescriptor(deviceType, tenant, deviceManagement);
	LOGGER.debug("Using the following device type proto:\n" + fdproto.toString());
	Descriptors.FileDescriptor[] fdescs = new Descriptors.FileDescriptor[0];
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
	    Descriptors.FileDescriptor filedesc = Descriptors.FileDescriptor.buildFrom(fdproto, fdescs);
	    Descriptors.Descriptor mdesc = filedesc
		    .findMessageTypeByName(ProtobufNaming.getDeviceTypeIdentifier(deviceType));

	    // Create the header message.
	    Descriptors.Descriptor header = mdesc.findNestedTypeByName(ProtobufNaming.HEADER_MSG_NAME);
	    DynamicMessage.Builder headBuilder = DynamicMessage.newBuilder(header);

	    // Set enum value based on command.
	    Descriptors.EnumDescriptor enumDesc = mdesc.findEnumTypeByName(ProtobufNaming.COMMAND_TYPES_ENUM);
	    Descriptors.EnumValueDescriptor enumValue = enumDesc
		    .findValueByName(ProtobufNaming.getCommandEnumName(execution.getCommand()));
	    if (enumValue == null) {
		throw new SiteWhereException("No enum value found for command: " + execution.getCommand().getName());
	    }
	    headBuilder.setField(header.findFieldByName(ProtobufNaming.HEADER_COMMAND_FIELD_NAME), enumValue);
	    headBuilder.setField(header.findFieldByName(ProtobufNaming.HEADER_ORIGINATOR_FIELD_NAME),
		    execution.getInvocation().getId().toString());

	    if (nested.getNested() != null) {
		IDeviceType nestedType = deviceManagement.getDeviceType(nested.getNested().getDeviceTypeId());
		LOGGER.debug(
			"Targeting nested device with type: " + nestedType.getName() + " at path " + nested.getPath());
		headBuilder.setField(header.findFieldByName(ProtobufNaming.HEADER_NESTED_PATH_FIELD_NAME),
			nested.getPath());
		headBuilder.setField(header.findFieldByName(ProtobufNaming.HEADER_NESTED_TYPE_FIELD_NAME),
			nestedType.getToken());
	    }

	    DynamicMessage hmessage = headBuilder.build();
	    LOGGER.debug("Header:\n" + hmessage.toString());
	    hmessage.writeDelimitedTo(out);

	    // Find nested type for command and create/populate an instance.
	    Descriptors.Descriptor command = mdesc.findNestedTypeByName(execution.getCommand().getName());
	    DynamicMessage.Builder cbuilder = DynamicMessage.newBuilder(command);

	    // Set each field in the command message.
	    for (String name : execution.getParameters().keySet()) {
		Object value = execution.getParameters().get(name);
		Descriptors.FieldDescriptor field = command.findFieldByName(name);
		if (field == null) {
		    throw new SiteWhereException("Command parameter '" + name + "' not found in device type: ");
		}
		try {
		    cbuilder.setField(field, value);
		} catch (IllegalArgumentException iae) {
		    LOGGER.error(
			    "Error setting field '" + name + "' with object of type: " + value.getClass().getName(),
			    iae);
		}
	    }
	    DynamicMessage cmessage = cbuilder.build();
	    LOGGER.debug("Message:\n" + cmessage.toString());
	    cmessage.writeDelimitedTo(out);

	    return out.toByteArray();
	} catch (Descriptors.DescriptorValidationException e) {
	    throw new SiteWhereException("Unable to create protobuf message.", e);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to encode protobuf message.", e);
	}
    }

    /**
     * Gets a file descriptor for protobuf representation of {@link IDeviceType}.
     * 
     * @param deviceType
     * @param tenant
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    protected static DescriptorProtos.FileDescriptorProto getFileDescriptor(IDeviceType deviceType, ITenant tenant,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	return ProtobufSpecificationBuilder.createFileDescriptor(deviceType, tenant, deviceManagement);
    }
}