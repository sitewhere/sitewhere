/*
 * ProtobufNaming.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.protobuf;

import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.command.IDeviceCommand;

/**
 * Common methods for naming scheme used in internal Google Protocol Buffer
 * representation.
 * 
 * @author Derek
 */
public class ProtobufNaming {

	/** Name of enum for message types */
	public static final String COMMAND_TYPES_ENUM = "_commands_";

	/** Name of message types enum field */
	public static final String COMMAND_TYPES_FIELD = "type";

	/** Prefix to indicate field type */
	public static final String FIELD_TYPE_PREFIX = "_type_";

	/**
	 * Get the specification identifier.
	 * 
	 * @param command
	 * @return
	 */
	protected static String getSpecificationIdentifier(IDeviceSpecification specification) {
		return specification.getName().replace(' ', '_').toLowerCase().trim();
	}

	/**
	 * Get the command enum entry name.
	 * 
	 * @param command
	 * @return
	 */
	protected static String getCommandEnumName(IDeviceCommand command) {
		return command.getName().toUpperCase();
	}

	/**
	 * Get the command nested message type identifier.
	 * 
	 * @param command
	 * @return
	 */
	protected static String getCommandTypeName(IDeviceCommand command) {
		return FIELD_TYPE_PREFIX + command.getName();
	}
}