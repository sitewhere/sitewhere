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
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.communication.ICommandDestination;
import com.sitewhere.spi.device.communication.ICommandProcessingStrategy;
import com.sitewhere.spi.device.communication.IDeviceCommunication;
import com.sitewhere.spi.device.communication.IDeviceStreamManager;
import com.sitewhere.spi.device.communication.IInboundEventSource;
import com.sitewhere.spi.device.communication.IOutboundCommandRouter;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.symbology.ISymbolGeneratorManager;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Base class for implementations of {@link IDeviceCommunication}. Takes care of
 * starting and stopping nested components in the correct order.
 * 
 * @author Derek
 */
public abstract class DeviceCommunication extends TenantLifecycleComponent implements IDeviceCommunication {

    /** Configured symbol generator manager */
    private ISymbolGeneratorManager symbolGeneratorManager = new SymbolGeneratorManager();

    /** Configured device stream manager */
    private IDeviceStreamManager deviceStreamManager = new DeviceStreamManager();

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
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLifecycleComponents().clear();

	// Start command processing strategy.
	if (getCommandProcessingStrategy() == null) {
	    throw new SiteWhereException("No command processing strategy configured for communication subsystem.");
	}
	startNestedComponent(getCommandProcessingStrategy(), monitor, true);

	// Start command destinations.
	if (getCommandDestinations() != null) {
	    for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
		startNestedComponent(destination, monitor, false);
	    }
	}

	// Start outbound command router.
	if (getOutboundCommandRouter() == null) {
	    throw new SiteWhereException("No command router for communication subsystem.");
	}
	getOutboundCommandRouter().initialize(getCommandDestinations());
	startNestedComponent(getOutboundCommandRouter(), monitor, true);

	// Start symbol generator manager.
	if (getSymbolGeneratorManager() == null) {
	    throw new SiteWhereException("No symbol generator manager configured for communication subsystem.");
	}
	startNestedComponent(getSymbolGeneratorManager(), monitor, true);

	// Start device stream manager.
	if (getDeviceStreamManager() == null) {
	    throw new SiteWhereException("No device stream manager configured for communication subsystem.");
	}
	startNestedComponent(getDeviceStreamManager(), monitor, true);

	// Start device event sources.
	if (getInboundEventSources() != null) {
	    for (IInboundEventSource<?> processor : getInboundEventSources()) {
		startNestedComponent(processor, monitor, false);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop inbound event sources.
	if (getInboundEventSources() != null) {
	    for (IInboundEventSource<?> processor : getInboundEventSources()) {
		processor.lifecycleStop(monitor);
	    }
	}

	// Stop device stream manager.
	if (getDeviceStreamManager() != null) {
	    getDeviceStreamManager().lifecycleStop(monitor);
	}

	// Stop symbol generator manager.
	if (getSymbolGeneratorManager() != null) {
	    getSymbolGeneratorManager().lifecycleStop(monitor);
	}

	// Stop command processing strategy.
	if (getCommandProcessingStrategy() != null) {
	    getCommandProcessingStrategy().lifecycleStop(monitor);
	}

	// Start command destinations.
	if (getCommandDestinations() != null) {
	    for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
		destination.lifecycleStop(monitor);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * deliverCommand(com. sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void deliverCommand(IDeviceCommandInvocation invocation) throws SiteWhereException {
	getCommandProcessingStrategy().deliverCommand(this, invocation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * deliverSystemCommand (java.lang.String,
     * com.sitewhere.spi.device.command.ISystemCommand)
     */
    @Override
    public void deliverSystemCommand(String hardwareId, ISystemCommand command) throws SiteWhereException {
	getCommandProcessingStrategy().deliverSystemCommand(this, hardwareId, command);
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
     * getDeviceStreamManager ()
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
     * getInboundEventSources ()
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
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getCommandDestinations ()
     */
    public List<ICommandDestination<?, ?>> getCommandDestinations() {
	return commandDestinations;
    }

    public void setCommandDestinations(List<ICommandDestination<?, ?>> commandDestinations) {
	this.commandDestinations = commandDestinations;
    }
}