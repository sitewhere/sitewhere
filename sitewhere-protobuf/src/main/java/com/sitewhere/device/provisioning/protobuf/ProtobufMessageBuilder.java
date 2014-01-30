/*
 * ProtobufMessageBuilder.java 
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
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;

/**
 * Produces an encoded message based on Google Protocol Buffer derived from an
 * {@link IDeviceSpecification}.
 * 
 * @author Derek
 */
public class ProtobufMessageBuilder {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ProtobufMessageBuilder.class);

	/**
	 * Create a protobuf message for an {@link IDeviceCommandExecution}.
	 * 
	 * @param execution
	 * @return
	 * @throws SiteWhereException
	 */
	public static DynamicMessage createMessage(IDeviceCommandExecution execution) throws SiteWhereException {
		IDeviceSpecification specification =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceSpecificationByToken(
						execution.getCommand().getSpecificationToken());
		DescriptorProtos.FileDescriptorProto fdproto = getFileDescriptor(specification);
		LOGGER.debug("Using the following specification proto:\n" + fdproto.toString());
		Descriptors.FileDescriptor[] fdescs = new Descriptors.FileDescriptor[0];
		try {
			Descriptors.FileDescriptor filedesc = Descriptors.FileDescriptor.buildFrom(fdproto, fdescs);
			Descriptors.Descriptor mdesc =
					filedesc.findMessageTypeByName(ProtobufNaming.getSpecificationIdentifier(specification));
			DynamicMessage.Builder sbuilder = DynamicMessage.newBuilder(mdesc);

			// Set enum value based on command.
			Descriptors.EnumDescriptor enumDesc = mdesc.findEnumTypeByName(ProtobufNaming.COMMAND_TYPES_ENUM);
			Descriptors.EnumValueDescriptor enumValue =
					enumDesc.findValueByName(ProtobufNaming.getCommandEnumName(execution.getCommand()));
			if (enumValue == null) {
				throw new SiteWhereException("No enum value found for command: "
						+ execution.getCommand().getName());
			}
			sbuilder.setField(mdesc.findFieldByName(ProtobufNaming.COMMAND_TYPES_FIELD), enumValue);

			// Find nested type for command and create/populate an instance.
			Descriptors.Descriptor commandTypeDesc =
					mdesc.findNestedTypeByName(ProtobufNaming.getCommandTypeName(execution.getCommand()));
			DynamicMessage.Builder cbuilder = DynamicMessage.newBuilder(commandTypeDesc);

			// Set each field in the command message.
			for (String name : execution.getParameters().keySet()) {
				Object value = execution.getParameters().get(name);
				Descriptors.FieldDescriptor field = commandTypeDesc.findFieldByName(name);
				if (field == null) {
					throw new SiteWhereException("Command parameter '" + name
							+ "' not found in specification: ");
				}
				try {
					cbuilder.setField(field, value);
				} catch (IllegalArgumentException iae) {
					LOGGER.error("Error setting field '" + name + "' with object of type: "
							+ value.getClass().getName(), iae);
				}
			}
			sbuilder.setField(mdesc.findFieldByName(execution.getCommand().getName()), cbuilder.build());

			DynamicMessage message = sbuilder.build();
			LOGGER.debug("Generated message:\n" + message.toString());
			return message;
		} catch (Descriptors.DescriptorValidationException e) {
			throw new SiteWhereException("Unable to create protobuf message.", e);
		}
	}

	/**
	 * Gets a file descriptor for protobuf representation of {@link IDeviceSpecification}.
	 * 
	 * @param specification
	 * @return
	 * @throws SiteWhereException
	 */
	protected static DescriptorProtos.FileDescriptorProto getFileDescriptor(IDeviceSpecification specification)
			throws SiteWhereException {
		return ProtobufSpecificationBuilder.createFileDescriptor(specification);
	}
}