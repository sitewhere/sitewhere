/*
 * SingleChoiceCommandRouter.java 
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
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.provisioning.IOutboundCommandAgent;
import com.sitewhere.spi.device.provisioning.IOutboundCommandRouter;

/**
 * Implementation of {@link IOutboundCommandRouter} that assumes a single
 * {@link IOutboundCommandAgent} is available and delivers commands to it.
 * 
 * @author Derek
 */
public class SingleChoiceCommandRouter extends OutboundCommandRouter {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SingleChoiceCommandRouter.class);

	/** Agent that will deliver all commands */
	private IOutboundCommandAgent<?, ?> agent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IOutboundCommandRouter#routeCommand(com.sitewhere
	 * .spi.device.command.IDeviceCommandExecution,
	 * com.sitewhere.spi.device.IDeviceNestingContext,
	 * com.sitewhere.spi.device.IDeviceAssignment, com.sitewhere.spi.device.IDevice)
	 */
	@Override
	public void routeCommand(IDeviceCommandExecution execution, IDeviceNestingContext nesting,
			IDeviceAssignment assignment, IDevice device) throws SiteWhereException {
		agent.deliverCommand(execution, nesting, assignment, device);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IOutboundCommandRouter#routeSystemCommand
	 * (java.lang.Object, com.sitewhere.spi.device.IDeviceNestingContext,
	 * com.sitewhere.spi.device.IDeviceAssignment, com.sitewhere.spi.device.IDevice)
	 */
	@Override
	public void routeSystemCommand(Object command, IDeviceNestingContext nesting,
			IDeviceAssignment assignment, IDevice device) throws SiteWhereException {
		agent.deliverSystemCommand(command, nesting, assignment, device);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.provisioning.OutboundCommandRouter#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Starting single choice command router...");
		super.start();
		if (getAgents().size() != 1) {
			throw new SiteWhereException("Expected exactly one agent for command routing but found "
					+ getAgents().size() + ".");
		}
		this.agent = getAgents().get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.provisioning.OutboundCommandRouter#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		super.stop();
		LOGGER.info("Stopped single choice command router.");
	}
}