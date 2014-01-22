/*
 * IDeviceCommandInvocationCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

import java.util.Map;

import com.sitewhere.spi.device.event.CommandActor;
import com.sitewhere.spi.device.event.CommandStatus;

/**
 * Interface for arguments needed to create a device command invocation.
 * 
 * @author Derek
 */
public interface IDeviceCommandInvocationCreateRequest extends IDeviceEventCreateRequest {

	/**
	 * Get actor type that originated the command.
	 * 
	 * @return
	 */
	public CommandActor getSourceActor();

	/**
	 * Get unique id of command originator.
	 * 
	 * @return
	 */
	public String getSourceId();

	/**
	 * Get actor type that received command.
	 * 
	 * @return
	 */
	public CommandActor getTargetActor();

	/**
	 * Get unique id of command target.
	 * 
	 * @return
	 */
	public String getTargetId();

	/**
	 * Get unique token for command to invoke.
	 * 
	 * @return
	 */
	public String getCommandToken();

	/**
	 * Get the list of parameter names mapped to values.
	 * 
	 * @return
	 */
	public Map<String, String> getParameterValues();

	/**
	 * Get status of command.
	 * 
	 * @return
	 */
	public CommandStatus getStatus();
}