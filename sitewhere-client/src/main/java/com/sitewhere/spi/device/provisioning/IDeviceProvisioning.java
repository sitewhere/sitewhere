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

import java.util.List;

import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Base interface for device provisioning functionality.
 * 
 * @author Derek
 */
public interface IDeviceProvisioning extends ISiteWhereLifecycle {

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
	 * Gets the configured command target resolver.
	 * 
	 * @return
	 */
	public ICommandTargetResolver getCommandTargetResolver();

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
	 * Get the configured registration manager.
	 * 
	 * @return
	 */
	public IRegistrationManager getRegistrationManager();

	/**
	 * Get the strategy for moving decoded events into the inbound chain.
	 * 
	 * @return
	 */
	public IInboundProcessingStrategy getInboundProcessingStrategy();

	/**
	 * Get the list of processors that bring device event data into the system.
	 * 
	 * @return
	 */
	public List<IInboundEventProcessor> getInboundEventProcessors();

	/**
	 * Deliver a command invocation.
	 * 
	 * @param invocation
	 * @throws SiteWhereException
	 */
	public void deliverCommand(IDeviceCommandInvocation invocation) throws SiteWhereException;

	/**
	 * Deliver a system command via the provisioning pipeline.
	 * 
	 * @param hardwareId
	 * @param command
	 * @throws SiteWhereException
	 */
	public void deliverSystemCommand(String hardwareId, Object command) throws SiteWhereException;
}