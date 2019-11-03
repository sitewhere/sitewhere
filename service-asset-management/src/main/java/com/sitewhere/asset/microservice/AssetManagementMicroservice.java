/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.microservice;

import javax.enterprise.context.ApplicationScoped;

import com.sitewhere.asset.configuration.AssetManagementModelProvider;
import com.sitewhere.asset.grpc.AssetManagementGrpcServer;
import com.sitewhere.asset.spi.grpc.IAssetManagementGrpcServer;
import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.grpc.client.device.CachedDeviceManagementApiChannel;
import com.sitewhere.grpc.client.device.DeviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
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
 * Microservice that provides asset management functionality.
 */
@ApplicationScoped
public class AssetManagementMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, IAssetManagementTenantEngine>
	implements IAssetManagementMicroservice {

    /** Microservice name */
    private static final String NAME = "Asset Management";

    /** Provides server for asset management GRPC requests */
    private IAssetManagementGrpcServer assetManagementGrpcServer;

    /** Device management API demux */
    private IDeviceManagementApiChannel<?> deviceManagementApiChannel;

    /** Cached device management implementation */
    private IDeviceManagement cachedDeviceManagement;

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
	return MicroserviceIdentifier.AssetManagement;
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
	return new AssetManagementModelProvider().buildModel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IAssetManagementTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new AssetManagementTenantEngine(tenant);
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

	// Initialize device management GRPC server.
	init.addInitializeStep(this, getAssetManagementGrpcServer(), true);

	// Initialize device management API channel + cache.
	init.addInitializeStep(this, getCachedDeviceManagement(), true);

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

	// Start asset management GRPC server.
	start.addStartStep(this, getAssetManagementGrpcServer(), true);

	// Start device mangement API channel + cache.
	start.addStartStep(this, getCachedDeviceManagement(), true);

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

	// Stop asset management GRPC server.
	stop.addStopStep(this, getAssetManagementGrpcServer());

	// Stop device mangement API channel + cache.
	stop.addStopStep(this, getCachedDeviceManagement());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Create asset management GRPC server.
	this.assetManagementGrpcServer = new AssetManagementGrpcServer(this);

	// Device management.
	this.deviceManagementApiChannel = new DeviceManagementApiChannel(getInstanceSettings());
	this.cachedDeviceManagement = new CachedDeviceManagementApiChannel(deviceManagementApiChannel,
		new CachedDeviceManagementApiChannel.CacheSettings());
    }

    /*
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice#
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
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice#
     * getCachedDeviceManagement()
     */
    @Override
    public IDeviceManagement getCachedDeviceManagement() {
	return cachedDeviceManagement;
    }

    public void setCachedDeviceManagement(IDeviceManagement cachedDeviceManagement) {
	this.cachedDeviceManagement = cachedDeviceManagement;
    }

    public IAssetManagementGrpcServer getAssetManagementGrpcServer() {
	return assetManagementGrpcServer;
    }

    public void setAssetManagementGrpcServer(IAssetManagementGrpcServer assetManagementGrpcServer) {
	this.assetManagementGrpcServer = assetManagementGrpcServer;
    }
}