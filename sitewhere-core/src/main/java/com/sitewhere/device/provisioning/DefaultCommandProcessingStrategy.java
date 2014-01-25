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

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandProcessingStrategy#deliver(com.sitewhere
	 * .spi.device.provisioning.IDeviceProvisioning,
	 * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void deliver(IDeviceProvisioning provisioning, IDeviceCommandInvocation invocation)
			throws SiteWhereException {
		IDeviceCommand command =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceCommandByToken(
						invocation.getCommandToken());
		if (command != null) {
			IDeviceCommandExecution execution =
					provisioning.getCommandExecutionBuilder().createExecution(command, invocation);
			byte[] encoded = provisioning.getCommandExecutionEncoder().encode(execution);
			provisioning.getCommandDeliveryProvider().deliver(invocation, encoded);
		} else {
			throw new SiteWhereException("Invalid command referenced from invocation.");
		}
	}
}