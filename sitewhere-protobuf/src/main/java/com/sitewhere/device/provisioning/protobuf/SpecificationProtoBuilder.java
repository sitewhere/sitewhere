/*
 * SpecificationProtoBuilder.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning.protobuf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;

/**
 * Builds the Google Protocol Buffer '.proto' file for a specification.
 * 
 * @author Derek
 */
public class SpecificationProtoBuilder {

	/** Declare once to prevent having to dynamically allocate */
	private static final String INDENT_CHAR = "\t\t\t\t\t\t\t\t\t\t\t\t";

	/**
	 * Generate a '.proto' file for a specification.
	 * 
	 * @param specification
	 * @return
	 * @throws SiteWhereException
	 */
	public static String getProtoForSpecification(IDeviceSpecification specification)
			throws SiteWhereException {
		StringBuffer buffer = new StringBuffer();
		List<IDeviceCommand> commands =
				SiteWhereServer.getInstance().getDeviceManagement().listDeviceCommands(
						specification.getToken(), false);
		generateProto(specification, commands, buffer);
		return buffer.toString();
	}

	/**
	 * Generate the '.proto' information.
	 * 
	 * @param specification
	 * @param commands
	 * @param buffer
	 * @throws SiteWhereException
	 */
	protected static void generateProto(IDeviceSpecification specification, List<IDeviceCommand> commands,
			StringBuffer buffer) throws SiteWhereException {
		int indent = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
		String specName = ProtobufNaming.getSpecificationIdentifier(specification);
		println("option optimize_for = LITE_RUNTIME;", indent, buffer);
		newline(buffer);

		println("// Google Protocol Buffer for '" + specification.getName() + "'.", indent, buffer);
		println("// Generated on " + formatter.format(new Date()), indent, buffer);
		println("message " + specName + " {", indent, buffer);
		newline(buffer);
		addCommandsEnum(specification, commands, buffer, indent + 1);
		newline(buffer);
		addHeader(buffer, indent + 1);
		newline(buffer);
		int index = 2;
		for (IDeviceCommand command : commands) {
			addCommand(command, index++, buffer, indent + 1);
			newline(buffer);
		}
		println("}", indent, buffer);
	}

	/**
	 * Generate an enum that lists all available commands for the specification.
	 * 
	 * @param specification
	 * @param commands
	 * @param buffer
	 * @throws SiteWhereException
	 */
	protected static void addCommandsEnum(IDeviceSpecification specification, List<IDeviceCommand> commands,
			StringBuffer buffer, int indent) throws SiteWhereException {
		StringBuffer enumCmds = new StringBuffer();
		enumCmds.append("enum " + ProtobufNaming.COMMAND_TYPES_ENUM + " {");
		int index = 1;
		for (IDeviceCommand command : commands) {
			String cmdName = ProtobufNaming.getCommandEnumName(command);
			enumCmds.append(cmdName + " = " + (index++) + "; ");
		}
		enumCmds.append("}");
		println(enumCmds.toString(), indent, buffer);
	}

	/**
	 * Add header message.
	 * 
	 * @param buffer
	 * @param indent
	 * @throws SiteWhereException
	 */
	protected static void addHeader(StringBuffer buffer, int indent) throws SiteWhereException {
		println("message _Header {", indent, buffer);
		println("required " + ProtobufNaming.COMMAND_TYPES_ENUM + " command = 1;", indent + 1, buffer);
		println("optional string originator = 2;", indent + 1, buffer);
		println("}", indent, buffer);
	}

	/**
	 * Add a single command to the proto.
	 * 
	 * @param command
	 * @param buffer
	 * @param indent
	 * @throws SiteWhereException
	 */
	protected static void addCommand(IDeviceCommand command, int cmdIndex, StringBuffer buffer, int indent)
			throws SiteWhereException {
		println("message " + command.getName() + " {", indent, buffer);
		int index = 1;
		for (ICommandParameter parameter : command.getParameters()) {
			StringBuffer paramBuffer = new StringBuffer();
			if (parameter.isRequired()) {
				paramBuffer.append("required");
			} else {
				paramBuffer.append("optional");
			}
			paramBuffer.append(" ");
			paramBuffer.append(parameter.getType().name().toLowerCase());
			paramBuffer.append(" ");
			paramBuffer.append(parameter.getName());
			paramBuffer.append(" = ");
			paramBuffer.append(index++);
			paramBuffer.append(";");
			println(paramBuffer.toString(), indent + 1, buffer);
		}
		println("}", indent, buffer);
	}

	/**
	 * Print a line to the buffer at the given indent level.
	 * 
	 * @param line
	 * @param indent
	 * @param buffer
	 */
	protected static void println(String line, int indent, StringBuffer buffer) {
		buffer.append(INDENT_CHAR.substring(0, indent) + line + "\n");
	}

	/**
	 * Finishes a line.
	 * 
	 * @param buffer
	 */
	protected static void newline(StringBuffer buffer) {
		buffer.append("\n");
	}
}