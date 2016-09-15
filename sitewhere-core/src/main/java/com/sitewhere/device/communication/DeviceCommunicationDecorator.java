/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.List;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
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
import com.sitewhere.spi.server.tenant.ITenantHazelcastAware;
import com.sitewhere.spi.server.tenant.ITenantHazelcastConfiguration;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Uses decorator pattern to wrap a delegate {@link IDeviceCommunication}
 * implementation and override pieces of the functionality.
 * 
 * @author Derek
 */
public class DeviceCommunicationDecorator extends LifecycleComponentDecorator
	implements IDeviceCommunication, ITenantHazelcastAware {

    /** Wrapped instance */
    private IDeviceCommunication delegate;

    public DeviceCommunicationDecorator(IDeviceCommunication delegate) {
	super(delegate);
	this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#setTenant(
     * com. sitewhere.spi.user.ITenant)
     */
    @Override
    public void setTenant(ITenant tenant) {
	delegate.setTenant(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent#getTenant()
     */
    @Override
    public ITenant getTenant() {
	return delegate.getTenant();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.tenant.ITenantHazelcastAware#
     * setHazelcastConfiguration(com
     * .sitewhere.spi.server.tenant.ITenantHazelcastConfiguration)
     */
    @Override
    public void setHazelcastConfiguration(ITenantHazelcastConfiguration configuration) {
	if (delegate instanceof ITenantHazelcastAware) {
	    ((ITenantHazelcastAware) delegate).setHazelcastConfiguration(configuration);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getInboundEventSources( )
     */
    @Override
    public List<IInboundEventSource<?>> getInboundEventSources() {
	return delegate.getInboundEventSources();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getRegistrationManager( )
     */
    @Override
    public IRegistrationManager getRegistrationManager() {
	return delegate.getRegistrationManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getSymbolGeneratorManager()
     */
    @Override
    public ISymbolGeneratorManager getSymbolGeneratorManager() {
	return delegate.getSymbolGeneratorManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getBatchOperationManager()
     */
    @Override
    public IBatchOperationManager getBatchOperationManager() {
	return delegate.getBatchOperationManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getDeviceStreamManager( )
     */
    @Override
    public IDeviceStreamManager getDeviceStreamManager() {
	return delegate.getDeviceStreamManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getDevicePresenceManager()
     */
    @Override
    public IDevicePresenceManager getDevicePresenceManager() {
	return delegate.getDevicePresenceManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getCommandProcessingStrategy()
     */
    @Override
    public ICommandProcessingStrategy getCommandProcessingStrategy() {
	return delegate.getCommandProcessingStrategy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getOutboundCommandRouter()
     */
    @Override
    public IOutboundCommandRouter getOutboundCommandRouter() {
	return delegate.getOutboundCommandRouter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * getCommandDestinations( )
     */
    @Override
    public List<ICommandDestination<?, ?>> getCommandDestinations() {
	return delegate.getCommandDestinations();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * deliverCommand(com. sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void deliverCommand(IDeviceCommandInvocation invocation) throws SiteWhereException {
	delegate.deliverCommand(invocation);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceCommunication#
     * deliverSystemCommand( java.lang.String,
     * com.sitewhere.spi.device.command.ISystemCommand)
     */
    @Override
    public void deliverSystemCommand(String hardwareId, ISystemCommand command) throws SiteWhereException {
	delegate.deliverSystemCommand(hardwareId, command);
    }
}