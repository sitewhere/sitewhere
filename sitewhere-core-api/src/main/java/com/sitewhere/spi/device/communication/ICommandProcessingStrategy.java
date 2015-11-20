/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.communication;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Defines the flow executed for processing a command for delivery.
 * 
 * @author Derek
 */
public interface ICommandProcessingStrategy extends ITenantLifecycleComponent {

	/**
	 * Get the {@link ICommandTargetResolver} implementation.
	 * 
	 * @return
	 */
	public ICommandTargetResolver getCommandTargetResolver();

	/**
	 * Send a command using the given communication subsystem implementation.
	 * 
	 * @param communication
	 * @param invocation
	 * @throws SiteWhereException
	 */
	public void deliverCommand(IDeviceCommunication communication, IDeviceCommandInvocation invocation)
			throws SiteWhereException;

	/**
	 * Delivers a system command using the given communication subsystem implementation.
	 * 
	 * @param communication
	 * @param hardwareId
	 * @param command
	 * @throws SiteWhereException
	 */
	public void deliverSystemCommand(IDeviceCommunication communication, String hardwareId,
			ISystemCommand command) throws SiteWhereException;
}