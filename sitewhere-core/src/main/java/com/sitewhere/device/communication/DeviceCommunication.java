/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.device.communication.symbology.SymbolGeneratorManager;
import com.sitewhere.server.batch.BatchOperationManager;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.batch.IBatchOperationManager;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.communication.ICommandDestination;
import com.sitewhere.spi.device.communication.ICommandProcessingStrategy;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.communication.IDeviceStreamManager;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.device.communication.IOutboundCommandRouter;
import com.sitewhere.spi.device.communication.IRegistrationManager;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.presence.IDevicePresenceManager;
import com.sitewhere.spi.device.symbology.ISymbolGeneratorManager;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Base class for implementations of {@link IDeviceCommunication}. Takes care of starting
 * and stopping nested components in the correct order.
 * 
 * @author Derek
 */
public abstract class DeviceCommunication extends TenantLifecycleComponent implements IDeviceCommunication {

	/** Configured registration manager */
	private IRegistrationManager registrationManager = new RegistrationManager();

	/** Configured symbol generator manager */
	private ISymbolGeneratorManager symbolGeneratorManager = new SymbolGeneratorManager();

	/** Configured batch operation manager */
	private IBatchOperationManager batchOperationManager = new BatchOperationManager();

	/** Configured device stream manager */
	private IDeviceStreamManager deviceStreamManager = new DeviceStreamManager();

	/** Configured device presence manager */
	private IDevicePresenceManager devicePresenceManager;

	/** Configured list of inbound event sources */
	private List<IInboundEventSource<?>> inboundEventSources = new ArrayList<IInboundEventSource<?>>();

	/** Configured command processing strategy */
	private ICommandProcessingStrategy commandProcessingStrategy = new DefaultCommandProcessingStrategy();

	/** Configured outbound command router */
	private IOutboundCommandRouter outboundCommandRouter = new NoOpCommandRouter();

	/** Configured list of command destinations */
	private List<ICommandDestination<?, ?>> commandDestinations = new ArrayList<ICommandDestination<?, ?>>();

	public DeviceCommunication() {
		super(LifecycleComponentType.DeviceCommunication);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		getLifecycleComponents().clear();

		// Start command processing strategy.
		if (getCommandProcessingStrategy() == null) {
			throw new SiteWhereException(
					"No command processing strategy configured for communication subsystem.");
		}
		startNestedComponent(getCommandProcessingStrategy(), true);

		// Start command destinations.
		if (getCommandDestinations() != null) {
			for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
				startNestedComponent(destination, false);
			}
		}

		// Start outbound command router.
		if (getOutboundCommandRouter() == null) {
			throw new SiteWhereException("No command router for communication subsystem.");
		}
		getOutboundCommandRouter().initialize(getCommandDestinations());
		startNestedComponent(getOutboundCommandRouter(), true);

		// Start registration manager.
		if (getRegistrationManager() == null) {
			throw new SiteWhereException("No registration manager configured for communication subsystem.");
		}
		startNestedComponent(getRegistrationManager(), true);

		// Start symbol generator manager.
		if (getSymbolGeneratorManager() == null) {
			throw new SiteWhereException(
					"No symbol generator manager configured for communication subsystem.");
		}
		startNestedComponent(getSymbolGeneratorManager(), true);

		// Start batch operation manager.
		if (getBatchOperationManager() == null) {
			throw new SiteWhereException(
					"No batch operation manager configured for communication subsystem.");
		}
		startNestedComponent(getBatchOperationManager(), true);

		// Start device stream manager.
		if (getDeviceStreamManager() == null) {
			throw new SiteWhereException("No device stream manager configured for communication subsystem.");
		}
		startNestedComponent(getDeviceStreamManager(), true);

		// Start device presence manager if configured.
		if (getDevicePresenceManager() != null) {
			startNestedComponent(getDevicePresenceManager(), true);
		}

		// Start device event sources.
		if (getInboundEventSources() != null) {
			for (IInboundEventSource<?> processor : getInboundEventSources()) {
				startNestedComponent(processor, false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		// Stop inbound event sources.
		if (getInboundEventSources() != null) {
			for (IInboundEventSource<?> processor : getInboundEventSources()) {
				processor.lifecycleStop();
			}
		}

		// Stop device stream manager.
		if (getDevicePresenceManager() != null) {
			getDevicePresenceManager().lifecycleStop();
		}

		// Stop device stream manager.
		if (getDeviceStreamManager() != null) {
			getDeviceStreamManager().lifecycleStop();
		}

		// Stop batch operation manager.
		if (getBatchOperationManager() != null) {
			getBatchOperationManager().lifecycleStop();
		}

		// Stop symbol generator manager.
		if (getSymbolGeneratorManager() != null) {
			getSymbolGeneratorManager().lifecycleStop();
		}

		// Stop registration manager.
		if (getRegistrationManager() != null) {
			getRegistrationManager().lifecycleStop();
		}

		// Stop command processing strategy.
		if (getCommandProcessingStrategy() != null) {
			getCommandProcessingStrategy().lifecycleStop();
		}

		// Start command destinations.
		if (getCommandDestinations() != null) {
			for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
				destination.lifecycleStop();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceCommunication#deliverCommand(com.
	 * sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void deliverCommand(IDeviceCommandInvocation invocation) throws SiteWhereException {
		getCommandProcessingStrategy().deliverCommand(this, invocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceCommunication#deliverSystemCommand
	 * (java.lang.String, com.sitewhere.spi.device.command.ISystemCommand)
	 */
	@Override
	public void deliverSystemCommand(String hardwareId, ISystemCommand command) throws SiteWhereException {
		getCommandProcessingStrategy().deliverSystemCommand(this, hardwareId, command);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceCommunication#getRegistrationManager
	 * ()
	 */
	public IRegistrationManager getRegistrationManager() {
		return registrationManager;
	}

	public void setRegistrationManager(IRegistrationManager registrationManager) {
		this.registrationManager = registrationManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
	 * getSymbolGeneratorManager ()
	 */
	public ISymbolGeneratorManager getSymbolGeneratorManager() {
		return symbolGeneratorManager;
	}

	public void setSymbolGeneratorManager(ISymbolGeneratorManager symbolGeneratorManager) {
		this.symbolGeneratorManager = symbolGeneratorManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
	 * getBatchOperationManager ()
	 */
	public IBatchOperationManager getBatchOperationManager() {
		return batchOperationManager;
	}

	public void setBatchOperationManager(IBatchOperationManager batchOperationManager) {
		this.batchOperationManager = batchOperationManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceCommunication#getDeviceStreamManager
	 * ()
	 */
	public IDeviceStreamManager getDeviceStreamManager() {
		return deviceStreamManager;
	}

	public void setDeviceStreamManager(IDeviceStreamManager deviceStreamManager) {
		this.deviceStreamManager = deviceStreamManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
	 * getDevicePresenceManager()
	 */
	public IDevicePresenceManager getDevicePresenceManager() {
		return devicePresenceManager;
	}

	public void setDevicePresenceManager(IDevicePresenceManager devicePresenceManager) {
		this.devicePresenceManager = devicePresenceManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceCommunication#getInboundEventSources
	 * ()
	 */
	public List<IInboundEventSource<?>> getInboundEventSources() {
		return inboundEventSources;
	}

	public void setInboundEventSources(List<IInboundEventSource<?>> inboundEventSources) {
		this.inboundEventSources = inboundEventSources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
	 * getCommandProcessingStrategy()
	 */
	public ICommandProcessingStrategy getCommandProcessingStrategy() {
		return commandProcessingStrategy;
	}

	public void setCommandProcessingStrategy(ICommandProcessingStrategy commandProcessingStrategy) {
		this.commandProcessingStrategy = commandProcessingStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
	 * getOutboundCommandRouter ()
	 */
	public IOutboundCommandRouter getOutboundCommandRouter() {
		return outboundCommandRouter;
	}

	public void setOutboundCommandRouter(IOutboundCommandRouter outboundCommandRouter) {
		this.outboundCommandRouter = outboundCommandRouter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.communication.IDeviceCommunication#getCommandDestinations
	 * ()
	 */
	public List<ICommandDestination<?, ?>> getCommandDestinations() {
		return commandDestinations;
	}

	public void setCommandDestinations(List<ICommandDestination<?, ?>> commandDestinations) {
		this.commandDestinations = commandDestinations;
	}
}