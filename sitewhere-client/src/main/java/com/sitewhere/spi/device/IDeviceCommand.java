/*
 * IDeviceCommand.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

/**
 * Represents a command to or from a device.
 * 
 * @author Derek
 */
public interface IDeviceCommand extends IDeviceEvent {

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
	 * Get the command content.
	 * 
	 * @return
	 */
	public byte[] getCommand();

	/**
	 * Get command response (if any).
	 * 
	 * @return
	 */
	public byte[] getResponse();

	/**
	 * Get status of command.
	 * 
	 * @return
	 */
	public CommandStatus getStatus();
}