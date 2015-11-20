/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandStatus;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Implementation of {@link IDeviceCommandInvocation}.
 * 
 * @author Derek
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceCommandInvocation extends DeviceEvent implements IDeviceCommandInvocation, Serializable {

	/** For Java serialization */
	private static final long serialVersionUID = -7389600825785131041L;

	/** Type of actor that initiated the command */
	private CommandInitiator initiator;

	/** Id of actor that initiated the command */
	private String initiatorId;

	/** Type of actor that will receive the command */
	private CommandTarget target;

	/** Id of actor that will receive the command */
	private String targetId;

	/** Unique token of command to execute */
	private String commandToken;

	/** Values to use for command parameters */
	private Map<String, String> parameterValues = new HashMap<String, String>();

	/** Current invocation status */
	private CommandStatus status;

	/** FIELDS BELOW DEPEND ON MARSHALING PARAMETERS */

	/** Command that was invoked */
	private DeviceCommand command;

	/** HTML representation of invocation */
	private String asHtml;

	public DeviceCommandInvocation() {
		super(DeviceEventType.CommandInvocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getInitiator()
	 */
	public CommandInitiator getInitiator() {
		return initiator;
	}

	public void setInitiator(CommandInitiator initiator) {
		this.initiator = initiator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getInitiatorId()
	 */
	public String getInitiatorId() {
		return initiatorId;
	}

	public void setInitiatorId(String initiatorId) {
		this.initiatorId = initiatorId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getTarget()
	 */
	public CommandTarget getTarget() {
		return target;
	}

	public void setTarget(CommandTarget target) {
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getTargetId()
	 */
	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getCommandToken()
	 */
	public String getCommandToken() {
		return commandToken;
	}

	public void setCommandToken(String commandToken) {
		this.commandToken = commandToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getParameterValues()
	 */
	public Map<String, String> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Map<String, String> parameterValues) {
		this.parameterValues = parameterValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getStatus()
	 */
	public CommandStatus getStatus() {
		return status;
	}

	public void setStatus(CommandStatus status) {
		this.status = status;
	}

	public DeviceCommand getCommand() {
		return command;
	}

	public void setCommand(DeviceCommand command) {
		this.command = command;
	}

	public String getAsHtml() {
		return asHtml;
	}

	public void setAsHtml(String asHtml) {
		this.asHtml = asHtml;
	}
}