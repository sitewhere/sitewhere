/*
 * SpecificationMappingCommandRouter.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.provisioning.IOutboundCommandAgent;
import com.sitewhere.spi.device.provisioning.IOutboundCommandRouter;

/**
 * Implementation of {@link IOutboundCommandRouter} that maps specification ids to
 * {@link IOutboundCommandAgent} ids and routes accordingly.
 * 
 * @author Derek
 */
public class SpecificationMappingCommandRouter extends OutboundCommandRouter {

	/** Map of specification tokens to command agent ids */
	private Map<String, String> mappings = new HashMap<String, String>();

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
		IOutboundCommandAgent<?, ?> agent = getAgentForDevice(device);
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
		IOutboundCommandAgent<?, ?> agent = getAgentForDevice(device);
		agent.deliverSystemCommand(command, nesting, assignment, device);
	}

	/**
	 * Get {@link IOutboundCommandAgent} for device based on specification token
	 * associated with the device.
	 * 
	 * @param device
	 * @return
	 * @throws SiteWhereException
	 */
	protected IOutboundCommandAgent<?, ?> getAgentForDevice(IDevice device) throws SiteWhereException {
		String specToken = device.getSpecificationToken();
		String agentId = mappings.get(specToken);
		if (agentId == null) {
			throw new SiteWhereException("No command agent mapping for specification: " + specToken);
		}
		IOutboundCommandAgent<?, ?> agent = getAgents().get(agentId);
		if (agent == null) {
			throw new SiteWhereException("Mapping exists, but no agent found for agent id: " + agentId);
		}
		return agent;
	}

	public Map<String, String> getMappings() {
		return mappings;
	}

	public void setMappings(Map<String, String> mappings) {
		this.mappings = mappings;
	}
}