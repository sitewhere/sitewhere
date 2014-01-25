/*
 * IDeviceProvisioning.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Base interface for device provisioning functionality.
 * 
 * @author Derek
 */
public interface IDeviceProvisioning {

	/**
	 * Gets the configured command execution builder.
	 * 
	 * @return
	 */
	public ICommandExecutionBuilder getCommandExecutionBuilder();

	/**
	 * Gets the configured command execution encoder.
	 * 
	 * @return
	 */
	public ICommandExecutionEncoder getCommandExecutionEncoder();

	/**
	 * Gets the configured command delivery provider.
	 * 
	 * @return
	 */
	public ICommandDeliveryProvider getCommandDeliveryProvider();

	/**
	 * Get the configured command processing strategy.
	 * 
	 * @return
	 */
	public ICommandProcessingStrategy getCommandProcessingStrategy();

	/**
	 * Deliver a command invocation.
	 * 
	 * @param invocation
	 * @throws SiteWhereException
	 */
	public void deliver(IDeviceCommandInvocation invocation) throws SiteWhereException;
}