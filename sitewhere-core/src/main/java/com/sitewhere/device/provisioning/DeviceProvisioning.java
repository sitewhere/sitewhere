/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.provisioning.ICommandDestination;
import com.sitewhere.spi.device.provisioning.ICommandProcessingStrategy;
import com.sitewhere.spi.device.provisioning.IDeviceProvisioning;
import com.sitewhere.spi.device.provisioning.IInboundEventSource;
import com.sitewhere.spi.device.provisioning.IInboundProcessingStrategy;
import com.sitewhere.spi.device.provisioning.IOutboundCommandRouter;
import com.sitewhere.spi.device.provisioning.IOutboundProcessingStrategy;
import com.sitewhere.spi.device.provisioning.IRegistrationManager;

/**
 * Base class for implementations of {@link IDeviceProvisioning}. Takes care of starting
 * and stopping nested components in the correct order.
 * 
 * @author Derek
 */
public abstract class DeviceProvisioning implements IDeviceProvisioning {

	/** Configured registration manager */
	private IRegistrationManager registrationManager;

	/** Configured inbound processing strategy */
	private IInboundProcessingStrategy inboundProcessingStrategy;

	/** Configured list of inbound event sources */
	private List<IInboundEventSource<?>> inboundEventSources = new ArrayList<IInboundEventSource<?>>();

	/** Configured command processing strategy */
	private ICommandProcessingStrategy commandProcessingStrategy;

	/** Configured outbound processing strategy */
	private IOutboundProcessingStrategy outboundProcessingStrategy;

	/** Configured outbound command router */
	private IOutboundCommandRouter outboundCommandRouter;

	/** Configured list of command destinations */
	private List<ICommandDestination<?, ?>> commandDestinations = new ArrayList<ICommandDestination<?, ?>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		// Start command processing strategy.
		if (getCommandProcessingStrategy() == null) {
			throw new SiteWhereException("No command processing strategy configured for provisioning.");
		}
		getCommandProcessingStrategy().start();

		// Start command destinations.
		if (getCommandDestinations() != null) {
			for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
				destination.start();
			}
		}

		// Start outbound command router.
		if (getOutboundCommandRouter() == null) {
			throw new SiteWhereException("No command router for provisioning.");
		}
		getOutboundCommandRouter().initialize(getCommandDestinations());
		getOutboundCommandRouter().start();

		// Start outbound processing strategy.
		if (getOutboundProcessingStrategy() == null) {
			throw new SiteWhereException("No outbound processing strategy configured for provisioning.");
		}
		getOutboundProcessingStrategy().start();

		// Start registration manager.
		if (getRegistrationManager() == null) {
			throw new SiteWhereException("No registration manager configured for provisioning.");
		}
		getRegistrationManager().start();

		// Start inbound processing strategy.
		if (getInboundProcessingStrategy() == null) {
			throw new SiteWhereException("No inbound processing strategy configured for provisioning.");
		}
		getInboundProcessingStrategy().start();

		// Start device event sources.
		if (getInboundEventSources() != null) {
			for (IInboundEventSource<?> processor : getInboundEventSources()) {
				processor.start();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		// Stop inbound event sources.
		if (getInboundEventSources() != null) {
			for (IInboundEventSource<?> processor : getInboundEventSources()) {
				processor.stop();
			}
		}

		// Stop inbound processing strategy.
		if (getInboundProcessingStrategy() != null) {
			getInboundProcessingStrategy().stop();
		}

		// Stop outbound processing strategy.
		if (getOutboundProcessingStrategy() != null) {
			getOutboundProcessingStrategy().stop();
		}

		// Stop command processing strategy.
		if (getCommandProcessingStrategy() != null) {
			getCommandProcessingStrategy().stop();
		}

		// Start command destinations.
		if (getCommandDestinations() != null) {
			for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
				destination.stop();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IDeviceProvisioning#deliverCommand(com.sitewhere
	 * .spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void deliverCommand(IDeviceCommandInvocation invocation) throws SiteWhereException {
		getCommandProcessingStrategy().deliverCommand(this, invocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IDeviceProvisioning#deliverSystemCommand(
	 * java.lang.String, com.sitewhere.spi.device.command.ISystemCommand)
	 */
	@Override
	public void deliverSystemCommand(String hardwareId, ISystemCommand command) throws SiteWhereException {
		getCommandProcessingStrategy().deliverSystemCommand(this, hardwareId, command);
	}

	public IRegistrationManager getRegistrationManager() {
		return registrationManager;
	}

	public void setRegistrationManager(IRegistrationManager registrationManager) {
		this.registrationManager = registrationManager;
	}

	public IInboundProcessingStrategy getInboundProcessingStrategy() {
		return inboundProcessingStrategy;
	}

	public void setInboundProcessingStrategy(IInboundProcessingStrategy inboundProcessingStrategy) {
		this.inboundProcessingStrategy = inboundProcessingStrategy;
	}

	public List<IInboundEventSource<?>> getInboundEventSources() {
		return inboundEventSources;
	}

	public void setInboundEventSources(List<IInboundEventSource<?>> inboundEventSources) {
		this.inboundEventSources = inboundEventSources;
	}

	public ICommandProcessingStrategy getCommandProcessingStrategy() {
		return commandProcessingStrategy;
	}

	public void setCommandProcessingStrategy(ICommandProcessingStrategy commandProcessingStrategy) {
		this.commandProcessingStrategy = commandProcessingStrategy;
	}

	public IOutboundProcessingStrategy getOutboundProcessingStrategy() {
		return outboundProcessingStrategy;
	}

	public void setOutboundProcessingStrategy(IOutboundProcessingStrategy outboundProcessingStrategy) {
		this.outboundProcessingStrategy = outboundProcessingStrategy;
	}

	public IOutboundCommandRouter getOutboundCommandRouter() {
		return outboundCommandRouter;
	}

	public void setOutboundCommandRouter(IOutboundCommandRouter outboundCommandRouter) {
		this.outboundCommandRouter = outboundCommandRouter;
	}

	public List<ICommandDestination<?, ?>> getCommandDestinations() {
		return commandDestinations;
	}

	public void setCommandDestinations(List<ICommandDestination<?, ?>> commandDestinations) {
		this.commandDestinations = commandDestinations;
	}
}