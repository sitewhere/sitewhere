/*
 * DefaultCommandProcessingStrategy.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.provisioning.ICommandProcessingStrategy;
import com.sitewhere.spi.device.provisioning.IDeviceProvisioning;

/**
 * Default implementation of {@link ICommandProcessingStrategy}.
 * 
 * @author Derek
 */
public class DefaultCommandProcessingStrategy implements ICommandProcessingStrategy {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultCommandProcessingStrategy.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandProcessingStrategy#deliverCommand
	 * (com.sitewhere.spi.device.provisioning.IDeviceProvisioning,
	 * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void deliverCommand(IDeviceProvisioning provisioning, IDeviceCommandInvocation invocation)
			throws SiteWhereException {
		LOGGER.debug("Command processing strategy handling invocation.");
		IDeviceCommand command =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceCommandByToken(
						invocation.getCommandToken());
		if (command != null) {
			IDeviceCommandExecution execution =
					provisioning.getCommandExecutionBuilder().createExecution(command, invocation);
			byte[] encoded = provisioning.getCommandExecutionEncoder().encode(execution);
			List<IDeviceAssignment> assignments =
					provisioning.getCommandTargetResolver().resolveTargets(invocation);
			for (IDeviceAssignment assignment : assignments) {
				provisioning.getCommandDeliveryProvider().deliver(assignment, invocation, encoded);
			}
		} else {
			throw new SiteWhereException("Invalid command referenced from invocation.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandProcessingStrategy#deliverSystemCommand
	 * (com.sitewhere.spi.device.provisioning.IDeviceProvisioning, java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void deliverSystemCommand(IDeviceProvisioning provisioning, String hardwareId, Object command)
			throws SiteWhereException {
		byte[] encoded = provisioning.getCommandExecutionEncoder().encodeSystemCommand(command);
		provisioning.getCommandDeliveryProvider().deliverSystemCommand(hardwareId, encoded);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Started command processing strategy.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Stopped command processing strategy");
	}
}