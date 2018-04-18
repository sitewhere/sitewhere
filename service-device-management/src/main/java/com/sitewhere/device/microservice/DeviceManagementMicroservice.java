/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.microservice;

import com.sitewhere.device.configuration.DeviceManagementModelProvider;
import com.sitewhere.device.grpc.DeviceManagementGrpcServer;
import com.sitewhere.device.spi.grpc.IDeviceManagementGrpcServer;
import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.grpc.client.asset.AssetManagementApiDemux;
import com.sitewhere.grpc.client.event.DeviceEventManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiDemux;
import com.sitewhere.microservice.hazelcast.HazelcastManager;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.hazelcast.IHazelcastManager;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides device management functionality.
 * 
 * @author Derek
 */
public class DeviceManagementMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, IDeviceManagementTenantEngine>
	implements IDeviceManagementMicroservice {

    /** Microservice name */
    private static final String NAME = "Device Management";

    /** Provides server for device management GRPC requests */
    private IDeviceManagementGrpcServer deviceManagementGrpcServer;

    /** Event management API demux */
    private IDeviceEventManagementApiDemux eventManagementApiDemux;

    /** Asset management API demux */
    private IAssetManagementApiDemux assetManagementApiDemux;

    /** Hazelcast manager */
    private IHazelcastManager hazelcastManager;

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
	return MicroserviceIdentifier.DeviceManagement;
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
	return new DeviceManagementModelProvider().buildModel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IDeviceManagementTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new DeviceManagementTenantEngine(tenant);
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
	// Create Hazelcast manager.
	this.hazelcastManager = new HazelcastManager();

	// Create device management GRPC server.
	this.deviceManagementGrpcServer = new DeviceManagementGrpcServer(this);

	// Event management microservice connectivity.
	this.eventManagementApiDemux = new DeviceEventManagementApiDemux();

	// Asset management microservice connectivity.
	this.assetManagementApiDemux = new AssetManagementApiDemux();

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize Hazelcast manager.
	init.addInitializeStep(this, getHazelcastManager(), true);

	// Initialize device management GRPC server.
	init.addInitializeStep(this, getDeviceManagementGrpcServer(), true);

	// Initialize event management API demux.
	init.addInitializeStep(this, getEventManagementApiDemux(), true);

	// Initialize asset management GRPC demux.
	init.addInitializeStep(this, getAssetManagementApiDemux(), true);

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

	// Start Hazelcast manager.
	start.addStartStep(this, getHazelcastManager(), true);

	// Start device management GRPC server.
	start.addStartStep(this, getDeviceManagementGrpcServer(), true);

	// Start event management API demux.
	start.addStartStep(this, getEventManagementApiDemux(), true);

	// Start asset management API demux.
	start.addStartStep(this, getAssetManagementApiDemux(), true);

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

	// Stop device management GRPC server.
	stop.addStopStep(this, getDeviceManagementGrpcServer());

	// Stop event management API demux.
	stop.addStopStep(this, getEventManagementApiDemux());

	// Stop asset management API demux.
	stop.addStopStep(this, getAssetManagementApiDemux());

	// Stop Hazelcast manager.
	stop.addStopStep(this, getHazelcastManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice#
     * getDeviceManagementGrpcServer()
     */
    @Override
    public IDeviceManagementGrpcServer getDeviceManagementGrpcServer() {
	return deviceManagementGrpcServer;
    }

    public void setDeviceManagementGrpcServer(IDeviceManagementGrpcServer deviceManagementGrpcServer) {
	this.deviceManagementGrpcServer = deviceManagementGrpcServer;
    }

    /*
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice#
     * getEventManagementApiDemux()
     */
    @Override
    public IDeviceEventManagementApiDemux getEventManagementApiDemux() {
	return eventManagementApiDemux;
    }

    public void setEventManagementApiDemux(IDeviceEventManagementApiDemux eventManagementApiDemux) {
	this.eventManagementApiDemux = eventManagementApiDemux;
    }

    /*
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice#
     * getAssetManagementApiDemux()
     */
    @Override
    public IAssetManagementApiDemux getAssetManagementApiDemux() {
	return assetManagementApiDemux;
    }

    public void setAssetManagementApiDemux(IAssetManagementApiDemux assetManagementApiDemux) {
	this.assetManagementApiDemux = assetManagementApiDemux;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.ICachingMicroservice#getHazelcastManager()
     */
    @Override
    public IHazelcastManager getHazelcastManager() {
	return hazelcastManager;
    }

    public void setHazelcastManager(IHazelcastManager hazelcastManager) {
	this.hazelcastManager = hazelcastManager;
    }
}