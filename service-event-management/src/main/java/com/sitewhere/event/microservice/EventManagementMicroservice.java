/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.microservice;

import com.sitewhere.event.spi.grpc.IEventManagementGrpcServer;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.client.device.DeviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.microservice.grpc.EventManagementGrpcServer;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides device event management functionality.
 */
public class EventManagementMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, IEventManagementTenantEngine>
	implements IEventManagementMicroservice {

    /** Microservice name */
    private static final String NAME = "Event Management";

    /** Provides server for event management GRPC requests */
    private IEventManagementGrpcServer eventManagementGrpcServer;

    /** Device management API channel */
    private IDeviceManagementApiChannel<?> deviceManagementApiChannel;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.EventManagement;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#isGlobal()
     */
    @Override
    public boolean isGlobal() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IEventManagementTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new EventManagementTenantEngine(tenant);
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * microserviceInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create GRPC components.
	createGrpcComponents();

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize event management GRPC server.
	init.addInitializeStep(this, getEventManagementGrpcServer(), true);

	// Initialize device management API channel.
	init.addInitializeStep(this, getDeviceManagementApiChannel(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * microserviceStart(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start event management GRPC server.
	start.addStartStep(this, getEventManagementGrpcServer(), true);

	// Start device mangement API channel.
	start.addStartStep(this, getDeviceManagementApiChannel(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * microserviceStop(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop event management GRPC server.
	stop.addStopStep(this, getEventManagementGrpcServer());

	// Stop device mangement API channel.
	stop.addStopStep(this, getDeviceManagementApiChannel());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Create device management GRPC server.
	this.eventManagementGrpcServer = new EventManagementGrpcServer(this);

	// Device management.
	this.deviceManagementApiChannel = new DeviceManagementApiChannel(getInstanceSettings());
    }

    /*
     * @see com.sitewhere.event.spi.microservice.IEventManagementMicroservice#
     * getDeviceManagementApiChannel()
     */
    @Override
    public IDeviceManagementApiChannel<?> getDeviceManagementApiChannel() {
	return deviceManagementApiChannel;
    }

    public void setDeviceManagementApiChannel(IDeviceManagementApiChannel<?> deviceManagementApiChannel) {
	this.deviceManagementApiChannel = deviceManagementApiChannel;
    }

    public IEventManagementGrpcServer getEventManagementGrpcServer() {
	return eventManagementGrpcServer;
    }

    public void setEventManagementGrpcServer(IEventManagementGrpcServer eventManagementGrpcServer) {
	this.eventManagementGrpcServer = eventManagementGrpcServer;
    }
}