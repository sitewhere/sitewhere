/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.microservice;

import com.sitewhere.event.configuration.EventManagementModelProvider;
import com.sitewhere.event.grpc.EventManagementGrpcServer;
import com.sitewhere.event.spi.grpc.IEventManagementGrpcServer;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.client.ApiChannelNotAvailableException;
import com.sitewhere.grpc.client.device.DeviceManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiDemux;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.messages.SiteWhereMessage;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides device event management functionality.
 * 
 * @author Derek
 */
public class EventManagementMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, IEventManagementTenantEngine>
	implements IEventManagementMicroservice {

    /** Microservice name */
    private static final String NAME = "Event Management";

    /** Provides server for event management GRPC requests */
    private IEventManagementGrpcServer eventManagementGrpcServer;

    /** Device management API demux */
    private IDeviceManagementApiDemux deviceManagementApiDemux;

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
     * @see com.sitewhere.spi.microservice.IMicroservice#buildConfigurationModel()
     */
    @Override
    public IConfigurationModel buildConfigurationModel() {
	return new EventManagementModelProvider().buildModel();
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.Microservice#afterMicroserviceStarted()
     */
    @Override
    public void afterMicroserviceStarted() {
	try {
	    waitForDependenciesAvailable();
	    getLogger().debug("All required microservices detected as available.");
	} catch (ApiChannelNotAvailableException e) {
	    getLogger().error(e, SiteWhereMessage.MICROSERVICE_NOT_AVAILABLE);

	}
    }

    /**
     * Wait for required microservices to become available.
     * 
     * @throws ApiNotAvailableException
     */
    protected void waitForDependenciesAvailable() throws ApiChannelNotAvailableException {
	getDeviceManagementApiDemux().waitForMicroserviceAvailable();
	getLogger().debug("Device management microservice detected as available.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#
     * microserviceInitialize(com.sitewhere.spi.server.lifecycle.
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

	// Initialize device management API demux.
	init.addInitializeStep(this, getDeviceManagementApiDemux(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#
     * microserviceStart(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start event management GRPC server.
	start.addStartStep(this, getEventManagementGrpcServer(), true);

	// Start device mangement API demux.
	start.addStartStep(this, getDeviceManagementApiDemux(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#
     * microserviceStop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop event management GRPC server.
	stop.addStopStep(this, getEventManagementGrpcServer());

	// Stop device mangement API demux.
	stop.addStopStep(this, getDeviceManagementApiDemux());

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
	this.deviceManagementApiDemux = new DeviceManagementApiDemux(true);
    }

    /*
     * @see com.sitewhere.event.spi.microservice.IEventManagementMicroservice#
     * getDeviceManagementApiDemux()
     */
    @Override
    public IDeviceManagementApiDemux getDeviceManagementApiDemux() {
	return deviceManagementApiDemux;
    }

    public void setDeviceManagementApiDemux(IDeviceManagementApiDemux deviceManagementApiDemux) {
	this.deviceManagementApiDemux = deviceManagementApiDemux;
    }

    public IEventManagementGrpcServer getEventManagementGrpcServer() {
	return eventManagementGrpcServer;
    }

    public void setEventManagementGrpcServer(IEventManagementGrpcServer eventManagementGrpcServer) {
	this.eventManagementGrpcServer = eventManagementGrpcServer;
    }
}