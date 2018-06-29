/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.microservice;

import com.sitewhere.commands.configuration.CommandDeliveryModelProvider;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryMicroservice;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
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
 * Microservice that provides command delivery functionality.
 * 
 * @author Derek
 */
public class CommandDeliveryMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, ICommandDeliveryTenantEngine>
	implements ICommandDeliveryMicroservice {

    /** Microservice name */
    private static final String NAME = "Command Delivery";

    /** Device management API channel */
    private IDeviceManagementApiDemux deviceManagementApiDemux;

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getName()
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
	return MicroserviceIdentifier.CommandDelivery;
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
	return new CommandDeliveryModelProvider().buildModel();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public ICommandDeliveryTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new CommandDeliveryTenantEngine(tenant);
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
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#
     * microserviceInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create GRPC components.
	createGrpcComponents();

	// Composite step for initializing microservice.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize device management API demux.
	init.addInitializeStep(this, getDeviceManagementApiDemux(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#
     * microserviceStart(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for starting microservice.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start device mangement API demux.
	start.addStartStep(this, getDeviceManagementApiDemux(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#
     * microserviceStop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop device mangement API demux.
	stop.addStopStep(this, getDeviceManagementApiDemux());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Device management.
	this.deviceManagementApiDemux = new DeviceManagementApiDemux();
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryMicroservice#
     * getDeviceManagementApiDemux()
     */
    @Override
    public IDeviceManagementApiDemux getDeviceManagementApiDemux() {
	return deviceManagementApiDemux;
    }

    public void setDeviceManagementApiDemux(IDeviceManagementApiDemux deviceManagementApiDemux) {
	this.deviceManagementApiDemux = deviceManagementApiDemux;
    }
}