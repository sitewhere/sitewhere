/*
 * IOutboundCommandAgent.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;

/**
 * Delivers commands to devices by encoding the commands, finding the list of target
 * devices, then using a delivery provider to send the encoded commands.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public interface IOutboundCommandAgent<T> extends ISiteWhereLifecycle {

	/**
	 * Get unique identifier for agent.
	 * 
	 * @return
	 */
	public String getAgentId();

	/**
	 * Gets the configured command execution encoder.
	 * 
	 * @return
	 */
	public ICommandExecutionEncoder<T> getCommandExecutionEncoder();

	/**
	 * Gets the configured command delivery provider.
	 * 
	 * @return
	 */
	public ICommandDeliveryProvider<T> getCommandDeliveryProvider();

	/**
	 * Deliver a command.
	 * 
	 * @param execution
	 * @param nesting
	 * @param assignment
	 * @throws SiteWhereException
	 */
	public void deliverCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
			IDeviceAssignment assignment) throws SiteWhereException;

	/**
	 * Deliver a system command.
	 * 
	 * @param command
	 * @param nesting
	 * @param assignment
	 * @throws SiteWhereException
	 */
	public void deliverSystemCommand(Object command, IDeviceNestingContext nesting,
			IDeviceAssignment assignment) throws SiteWhereException;
}