/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.microservice;

import com.sitewhere.devicestate.configuration.DeviceStateModelProvider;
import com.sitewhere.devicestate.spi.grpc.IDeviceStateGrpcServer;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.grpc.client.asset.AssetManagementApiChannel;
import com.sitewhere.grpc.client.asset.CachedAssetManagementApiChannel;
import com.sitewhere.grpc.client.device.CachedDeviceManagementApiChannel;
import com.sitewhere.grpc.client.device.DeviceManagementApiChannel;
import com.sitewhere.grpc.client.event.DeviceEventManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.microservice.grpc.DeviceStateGrpcServer;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides device state mangagement functionality.
 * 
 * @author Derek
 */
public class DeviceStateMicroservice extends MultitenantMicroservice<MicroserviceIdentifier, IDeviceStateTenantEngine>
	implements IDeviceStateMicroservice {

    /** Microservice name */
    private static final String NAME = "Presence Management";

    /** Provides server for device management GRPC requests */
    private IDeviceStateGrpcServer deviceStateGrpcServer;

    /** Device management API channel */
    private IDeviceManagementApiChannel<?> deviceManagementApiChannel;

    /** Cached device management implementation */
    private IDeviceManagement cachedDeviceManagement;

    /** Asset management API channel */
    private IAssetManagementApiChannel<?> assetManagementApiChannel;

    /** Cached asset management implementation */
    private IAssetManagement cachedAssetManagement;

    /** Device event management API channel */
    private IDeviceEventManagementApiChannel<?> deviceEventManagementApiChannel;

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
	return MicroserviceIdentifier.DeviceState;
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
	return new DeviceStateModelProvider().buildModel();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IDeviceStateTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new DeviceStateTenantEngine(tenant);
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

	// Initialize device state GRPC server.
	init.addInitializeStep(this, getDeviceStateGrpcServer(), true);

	// Initialize device management API channel + cache.
	init.addInitializeStep(this, getCachedDeviceManagement(), true);

	// Initialize asset management API channel + cache.
	init.addInitializeStep(this, getCachedAssetManagement(), true);

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

	// Start device state GRPC server.
	start.addStartStep(this, getDeviceStateGrpcServer(), true);

	// Start device mangement API channel + cache.
	start.addStartStep(this, getCachedDeviceManagement(), true);

	// Start asset mangement API channel + cache.
	start.addStartStep(this, getCachedAssetManagement(), true);

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

	// Stop device mangement API channel + cache.
	stop.addStopStep(this, getCachedDeviceManagement());

	// Stop asset mangement API channel + cache.
	stop.addStopStep(this, getCachedAssetManagement());

	// Stop device event mangement API channel.
	stop.addStopStep(this, getDeviceEventManagementApiChannel());

	// Stop device state GRPC server.
	stop.addStopStep(this, getDeviceStateGrpcServer());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Create device state GRPC server.
	this.deviceStateGrpcServer = new DeviceStateGrpcServer(this);

	// Device management.
	this.deviceManagementApiChannel = new DeviceManagementApiChannel(getInstanceSettings());
	this.cachedDeviceManagement = new CachedDeviceManagementApiChannel(deviceManagementApiChannel,
		new CachedDeviceManagementApiChannel.CacheSettings());

	// Asset management.
	this.assetManagementApiChannel = new AssetManagementApiChannel(getInstanceSettings());
	this.cachedAssetManagement = new CachedAssetManagementApiChannel(assetManagementApiChannel,
		new CachedAssetManagementApiChannel.CacheSettings());

	// Device event management.
	this.deviceEventManagementApiChannel = new DeviceEventManagementApiChannel(getInstanceSettings());
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice#
     * getDeviceStateGrpcServer()
     */
    @Override
    public IDeviceStateGrpcServer getDeviceStateGrpcServer() {
	return deviceStateGrpcServer;
    }

    public void setDeviceStateGrpcServer(IDeviceStateGrpcServer deviceStateGrpcServer) {
	this.deviceStateGrpcServer = deviceStateGrpcServer;
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice#
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
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice#
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
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice#
     * getAssetManagementApiChannel()
     */
    @Override
    public IAssetManagementApiChannel<?> getAssetManagementApiChannel() {
	return assetManagementApiChannel;
    }

    public void setAssetManagementApiChannel(IAssetManagementApiChannel<?> assetManagementApiChannel) {
	this.assetManagementApiChannel = assetManagementApiChannel;
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice#
     * getCachedAssetManagement()
     */
    @Override
    public IAssetManagement getCachedAssetManagement() {
	return cachedAssetManagement;
    }

    public void setCachedAssetManagement(IAssetManagement cachedAssetManagement) {
	this.cachedAssetManagement = cachedAssetManagement;
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice#
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