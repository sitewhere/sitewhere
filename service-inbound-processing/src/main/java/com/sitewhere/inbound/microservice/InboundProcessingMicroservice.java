/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.client.DeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.client.DeviceEventManagementGrpcChannel;
import com.sitewhere.grpc.model.client.DeviceManagementGrpcChannel;
import com.sitewhere.grpc.model.spi.ApiNotAvailableException;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.microservice.MicroserviceEnvironment;
import com.sitewhere.microservice.ignite.client.CachedDeviceManagementApiChannel;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides inbound event processing functionality.
 * 
 * @author Derek
 */
public class InboundProcessingMicroservice extends MultitenantMicroservice<IInboundProcessingTenantEngine>
	implements IInboundProcessingMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Inbound Processing";

    /** Identifies module resources such as configuration file */
    private static final String MODULE_IDENTIFIER = "inbound-processing";

    /** Device management GRPC channel */
    private DeviceManagementGrpcChannel deviceManagementGrpcChannel;

    /** Device management API channel */
    private IDeviceManagementApiChannel deviceManagementApiChannel;

    /** Device event management GRPC channel */
    private DeviceEventManagementGrpcChannel deviceEventManagementGrpcChannel;

    /** Device event management API channel */
    private IDeviceEventManagementApiChannel deviceEventManagementApiChannel;

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
    public String getIdentifier() {
	return MODULE_IDENTIFIER;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IInboundProcessingTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new InboundProcessingTenantEngine(this, tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.Microservice#afterMicroserviceStarted()
     */
    @Override
    public void afterMicroserviceStarted() {
	try {
	    waitForApisAvailable();
	    getLogger().info("All required APIs detected as available.");
	} catch (ApiNotAvailableException e) {
	    getLogger().error("Required APIs not available.", e);
	}
    }

    /**
     * Wait for required APIs to become available.
     * 
     * @throws ApiNotAvailableException
     */
    protected void waitForApisAvailable() throws ApiNotAvailableException {
	getDeviceManagementApiChannel().waitForApiAvailable();
	getLogger().info("Device management API detected as available.");
	getDeviceEventManagementApiChannel().waitForApiAvailable();
	getLogger().info("Device event management API detected as available.");
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

	// Initialize device management GRPC channel.
	init.addInitializeStep(this, getDeviceManagementGrpcChannel(), true);

	// Initialize device event management GRPC channel.
	init.addInitializeStep(this, getDeviceEventManagementGrpcChannel(), true);

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

	// Start device mangement GRPC channel.
	start.addStartStep(this, getDeviceManagementGrpcChannel(), true);

	// Start device event mangement GRPC channel.
	start.addStartStep(this, getDeviceEventManagementGrpcChannel(), true);

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

	// Stop device mangement GRPC channel.
	stop.addStopStep(this, getDeviceManagementGrpcChannel());

	// Stop device event mangement GRPC channel.
	stop.addStopStep(this, getDeviceEventManagementGrpcChannel());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Device management.
	this.deviceManagementGrpcChannel = new DeviceManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_DEVICE_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.deviceManagementApiChannel = new CachedDeviceManagementApiChannel(this, getDeviceManagementGrpcChannel());

	// Device event management.
	this.deviceEventManagementGrpcChannel = new DeviceEventManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_EVENT_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.deviceEventManagementApiChannel = new DeviceEventManagementApiChannel(
		getDeviceEventManagementGrpcChannel());
    }

    /*
     * @see com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice#
     * getDeviceManagementApiChannel()
     */
    @Override
    public IDeviceManagementApiChannel getDeviceManagementApiChannel() {
	return deviceManagementApiChannel;
    }

    public void setDeviceManagementApiChannel(IDeviceManagementApiChannel deviceManagementApiChannel) {
	this.deviceManagementApiChannel = deviceManagementApiChannel;
    }

    /*
     * @see com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice#
     * getDeviceEventManagementApiChannel()
     */
    @Override
    public IDeviceEventManagementApiChannel getDeviceEventManagementApiChannel() {
	return deviceEventManagementApiChannel;
    }

    public void setDeviceEventManagementApiChannel(IDeviceEventManagementApiChannel deviceEventManagementApiChannel) {
	this.deviceEventManagementApiChannel = deviceEventManagementApiChannel;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public DeviceManagementGrpcChannel getDeviceManagementGrpcChannel() {
	return deviceManagementGrpcChannel;
    }

    public void setDeviceManagementGrpcChannel(DeviceManagementGrpcChannel deviceManagementGrpcChannel) {
	this.deviceManagementGrpcChannel = deviceManagementGrpcChannel;
    }

    public DeviceEventManagementGrpcChannel getDeviceEventManagementGrpcChannel() {
	return deviceEventManagementGrpcChannel;
    }

    public void setDeviceEventManagementGrpcChannel(DeviceEventManagementGrpcChannel deviceEventManagementGrpcChannel) {
	this.deviceEventManagementGrpcChannel = deviceEventManagementGrpcChannel;
    }
}