/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.microservice;

import com.sitewhere.connectors.configuration.OutboundConnectorsModelProvider;
import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice;
import com.sitewhere.connectors.spi.microservice.IOutboundConnectorsTenantEngine;
import com.sitewhere.grpc.client.ApiChannelNotAvailableException;
import com.sitewhere.grpc.client.device.DeviceManagementApiDemux;
import com.sitewhere.grpc.client.event.DeviceEventManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiDemux;
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
 * Microservice that provides outbound connector management.
 * 
 * @author Derek
 */
public class OutboundConnectorsMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, IOutboundConnectorsTenantEngine>
	implements IOutboundConnectorsMicroservice {

    /** Microservice name */
    private static final String NAME = "Outbound Connectors";

    /** Device management API demux */
    private IDeviceManagementApiDemux deviceManagementApiDemux;

    /** Device event management API demux */
    private IDeviceEventManagementApiDemux deviceEventManagementApiDemux;

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
	return MicroserviceIdentifier.OutboundConnectors;
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
	return new OutboundConnectorsModelProvider().buildModel();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IOutboundConnectorsTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new OutboundConnectorsTenantEngine(tenant);
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
	getDeviceEventManagementApiDemux().waitForMicroserviceAvailable();
	getLogger().debug("Device event management microservice detected as available.");
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

	// Initialize device event management API demux.
	init.addInitializeStep(this, getDeviceEventManagementApiDemux(), true);

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

	// Start device event mangement API demux.
	start.addStartStep(this, getDeviceEventManagementApiDemux(), true);

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

	// Stop device event mangement API demux.
	stop.addStopStep(this, getDeviceEventManagementApiDemux());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Device management.
	this.deviceManagementApiDemux = new DeviceManagementApiDemux(true);

	// Device event management.
	this.deviceEventManagementApiDemux = new DeviceEventManagementApiDemux(true);
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice#
     * getDeviceManagementApiDemux()
     */
    @Override
    public IDeviceManagementApiDemux getDeviceManagementApiDemux() {
	return deviceManagementApiDemux;
    }

    public void setDeviceManagementApiDemux(IDeviceManagementApiDemux deviceManagementApiDemux) {
	this.deviceManagementApiDemux = deviceManagementApiDemux;
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.microservice.IOutboundConnectorsMicroservice#
     * getDeviceEventManagementApiDemux()
     */
    @Override
    public IDeviceEventManagementApiDemux getDeviceEventManagementApiDemux() {
	return deviceEventManagementApiDemux;
    }

    public void setDeviceEventManagementApiDemux(IDeviceEventManagementApiDemux deviceEventManagementApiDemux) {
	this.deviceEventManagementApiDemux = deviceEventManagementApiDemux;
    }
}