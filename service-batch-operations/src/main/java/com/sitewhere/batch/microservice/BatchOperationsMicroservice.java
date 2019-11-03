/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.microservice;

import javax.enterprise.context.ApplicationScoped;

import com.sitewhere.batch.configuration.BatchOperationsModelProvider;
import com.sitewhere.batch.spi.grpc.IBatchManagementGrpcServer;
import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.grpc.client.device.CachedDeviceManagementApiChannel;
import com.sitewhere.grpc.client.device.DeviceManagementApiChannel;
import com.sitewhere.grpc.client.event.DeviceEventManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.microservice.grpc.BatchManagementGrpcServer;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides batch operations functionality.
 */
@ApplicationScoped
public class BatchOperationsMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, IBatchOperationsTenantEngine>
	implements IBatchOperationsMicroservice {

    /** Microservice name */
    private static final String NAME = "Batch Operations";

    /** Provides server for batch management GRPC requests */
    private IBatchManagementGrpcServer batchManagementGrpcServer;

    /** Device management API demux */
    private IDeviceManagementApiChannel<?> deviceManagementApiChannel;

    /** Device event management API demux */
    private IDeviceEventManagementApiChannel<?> deviceEventManagementApiChannel;

    /** Cached device management implementation */
    private IDeviceManagement cachedDeviceManagement;

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
	return MicroserviceIdentifier.BatchOperations;
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
	return new BatchOperationsModelProvider().buildModel();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IBatchOperationsTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new BatchOperationsTenantEngine(tenant);
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

	// Initialize batch management GRPC server.
	init.addInitializeStep(this, getBatchManagementGrpcServer(), true);

	// Initialize device management API channel + cache.
	init.addInitializeStep(this, getCachedDeviceManagement(), true);

	// Initialize device event management API channel.
	init.addInitializeStep(this, getDeviceEventManagementApiChannel(), true);

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

	// Start batch management GRPC server.
	start.addStartStep(this, getBatchManagementGrpcServer(), true);

	// Start device mangement API channel + cache.
	start.addStartStep(this, getCachedDeviceManagement(), true);

	// Start device event mangement API channel.
	start.addStartStep(this, getDeviceEventManagementApiChannel(), true);

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

	// Stop batch management GRPC server.
	stop.addStopStep(this, getBatchManagementGrpcServer());

	// Stop device mangement API channel + cache.
	stop.addStopStep(this, getCachedDeviceManagement());

	// Stop device event mangement API channel.
	stop.addStopStep(this, getDeviceEventManagementApiChannel());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Create batch management GRPC server.
	this.batchManagementGrpcServer = new BatchManagementGrpcServer(this);

	// Device management.
	this.deviceManagementApiChannel = new DeviceManagementApiChannel(getInstanceSettings());
	this.cachedDeviceManagement = new CachedDeviceManagementApiChannel(deviceManagementApiChannel,
		new CachedDeviceManagementApiChannel.CacheSettings());

	// Device event management.
	this.deviceEventManagementApiChannel = new DeviceEventManagementApiChannel(getInstanceSettings());
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice#
     * getBatchManagementGrpcServer()
     */
    @Override
    public IBatchManagementGrpcServer getBatchManagementGrpcServer() {
	return batchManagementGrpcServer;
    }

    public void setBatchManagementGrpcServer(IBatchManagementGrpcServer batchManagementGrpcServer) {
	this.batchManagementGrpcServer = batchManagementGrpcServer;
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice#
     * getDeviceManagementApiChannel()
     */
    @Override
    public IDeviceManagementApiChannel<?> getDeviceManagementApiChannel() {
	return deviceManagementApiChannel;
    }

    public void setDeviceManagementApiChannel(IDeviceManagementApiChannel<?> deviceManagementApiChannel) {
	this.deviceManagementApiChannel = deviceManagementApiChannel;
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice#
     * getCachedDeviceManagement()
     */
    @Override
    public IDeviceManagement getCachedDeviceManagement() {
	return cachedDeviceManagement;
    }

    public void setCachedDeviceManagement(IDeviceManagement cachedDeviceManagement) {
	this.cachedDeviceManagement = cachedDeviceManagement;
    }

    /*
     * @see com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice#
     * getDeviceEventManagementApiChannel()
     */
    @Override
    public IDeviceEventManagementApiChannel<?> getDeviceEventManagementApiChannel() {
	return deviceEventManagementApiChannel;
    }

    public void setDeviceEventManagementApiChannel(
	    IDeviceEventManagementApiChannel<?> deviceEventManagementApiChannel) {
	this.deviceEventManagementApiChannel = deviceEventManagementApiChannel;
    }
}