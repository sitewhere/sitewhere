/*
 * OutboundCommandAgent.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider;
import com.sitewhere.spi.device.provisioning.ICommandExecutionEncoder;
import com.sitewhere.spi.device.provisioning.IOutboundCommandAgent;

/**
 * Default implementation of {@link IOutboundCommandAgent}.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public class OutboundCommandAgent<T> implements IOutboundCommandAgent<T> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(OutboundCommandAgent.class);

	/** Unique agent id */
	private String agentId;

	/** Configured command execution encoder */
	private ICommandExecutionEncoder<T> commandExecutionEncoder;

	/** Configured command delivery provider */
	private ICommandDeliveryProvider<T> commandDeliveryProvider;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IOutboundCommandAgent#deliverCommand(com.
	 * sitewhere.spi.device.command.IDeviceCommandExecution,
	 * com.sitewhere.spi.device.IDeviceNestingContext,
	 * com.sitewhere.spi.device.IDeviceAssignment)
	 */
	@Override
	public void deliverCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
			IDeviceAssignment assignment) throws SiteWhereException {
		T encoded = getCommandExecutionEncoder().encode(execution, nesting, assignment);
		getCommandDeliveryProvider().deliver(nesting, assignment, execution, encoded);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IOutboundCommandAgent#deliverSystemCommand
	 * (java.lang.Object, com.sitewhere.spi.device.IDeviceNestingContext,
	 * com.sitewhere.spi.device.IDeviceAssignment)
	 */
	@Override
	public void deliverSystemCommand(Object command, IDeviceNestingContext nesting,
			IDeviceAssignment assignment) throws SiteWhereException {
		T encoded = getCommandExecutionEncoder().encodeSystemCommand(command, nesting, assignment);
		getCommandDeliveryProvider().deliverSystemCommand(nesting, assignment, encoded);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting command agent...");

		// Start command execution encoder.
		if (getCommandExecutionEncoder() == null) {
			throw new SiteWhereException("No command execution encoder configured for provisioning.");
		}
		getCommandExecutionEncoder().start();

		// Start command delivery provider.
		if (getCommandDeliveryProvider() == null) {
			throw new SiteWhereException("No command delivery provider configured for provisioning.");
		}
		getCommandDeliveryProvider().start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Stopping command agent...");

		// Stop command execution encoder.
		if (getCommandExecutionEncoder() != null) {
			getCommandExecutionEncoder().stop();
		}

		// Stop command delivery provider.
		if (getCommandDeliveryProvider() != null) {
			getCommandDeliveryProvider().stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.provisioning.IOutboundCommandAgent#getAgentId()
	 */
	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IOutboundCommandAgent#getCommandExecutionEncoder
	 * ()
	 */
	public ICommandExecutionEncoder<T> getCommandExecutionEncoder() {
		return commandExecutionEncoder;
	}

	public void setCommandExecutionEncoder(ICommandExecutionEncoder<T> commandExecutionEncoder) {
		this.commandExecutionEncoder = commandExecutionEncoder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IOutboundCommandAgent#getCommandDeliveryProvider
	 * ()
	 */
	public ICommandDeliveryProvider<T> getCommandDeliveryProvider() {
		return commandDeliveryProvider;
	}

	public void setCommandDeliveryProvider(ICommandDeliveryProvider<T> commandDeliveryProvider) {
		this.commandDeliveryProvider = commandDeliveryProvider;
	}
}