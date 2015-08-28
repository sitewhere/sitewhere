/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.communication;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Delivers commands to devices by encoding the commands, finding the list of target
 * devices, then using a delivery provider to send the encoded commands.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public interface ICommandDestination<T, P> extends ITenantLifecycleComponent {

	/**
	 * Get unique identifier for destination.
	 * 
	 * @return
	 */
	public String getDestinationId();

	/**
	 * Gets the configured command execution encoder.
	 * 
	 * @return
	 */
	public ICommandExecutionEncoder<T> getCommandExecutionEncoder();

	/**
	 * Get the configured command delivery parameter extractor.
	 * 
	 * @return
	 */
	public ICommandDeliveryParameterExtractor<P> getCommandDeliveryParameterExtractor();

	/**
	 * Gets the configured command delivery provider.
	 * 
	 * @return
	 */
	public ICommandDeliveryProvider<T, P> getCommandDeliveryProvider();

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
	public void deliverSystemCommand(ISystemCommand command, IDeviceNestingContext nesting,
			IDeviceAssignment assignment) throws SiteWhereException;
}