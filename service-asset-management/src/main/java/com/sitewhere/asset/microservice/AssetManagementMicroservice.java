/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.microservice;

import com.sitewhere.asset.configuration.AssetManagementModelProvider;
import com.sitewhere.asset.grpc.AssetManagementGrpcServer;
import com.sitewhere.asset.messages.AssetManagementMessages;
import com.sitewhere.asset.spi.grpc.IAssetManagementGrpcServer;
import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
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
 * Microservice that provides asset management functionality.
 * 
 * @author Derek
 */
public class AssetManagementMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, IAssetManagementTenantEngine>
	implements IAssetManagementMicroservice {

    /** Microservice name */
    private static final String NAME = "Asset Management";

    /** Provides server for asset management GRPC requests */
    private IAssetManagementGrpcServer assetManagementGrpcServer;

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
     * @see com.sitewhere.microservice.Microservice#afterMicroserviceStarted()
     */
    @Override
    public void afterMicroserviceStarted() {
	try {
	    waitForDependenciesAvailable();
	    getLogger().debug(AssetManagementMessages.ALL_REQUIRED_MS_AVAILABLE);
	} catch (ApiChannelNotAvailableException e) {
	    getLogger().error(SiteWhereMessage.MICROSERVICE_NOT_AVAILABLE);
	    getLogger().error("Microservice not available.", e);
	}
    }

    /**
     * Wait for required microservices to become available.
     * 
     * @throws ApiChannelNotAvailableException
     */
    protected void waitForDependenciesAvailable() throws ApiChannelNotAvailableException {
	getDeviceManagementApiDemux().waitForMicroserviceAvailable();
	getLogger().debug(AssetManagementMessages.DEVICE_MANAGEMENT_MS_AVAILABLE);
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

	// Start asset management GRPC server.
	start.addStartStep(this, getAssetManagementGrpcServer(), true);

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

	// Stop asset management GRPC server.
	stop.addStopStep(this, getAssetManagementGrpcServer());

	// Stop device mangement API demux.
	stop.addStopStep(this, getDeviceManagementApiDemux());

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
	this.deviceManagementApiDemux = new DeviceManagementApiDemux(true);
    }

    /*
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice#
     * getDeviceManagementApiDemux()
     */
    @Override
    public IDeviceManagementApiDemux getDeviceManagementApiDemux() {
	return deviceManagementApiDemux;
    }

    public void setDeviceManagementApiDemux(IDeviceManagementApiDemux deviceManagementApiDemux) {
	this.deviceManagementApiDemux = deviceManagementApiDemux;
    }

    public IAssetManagementGrpcServer getAssetManagementGrpcServer() {
	return assetManagementGrpcServer;
    }

    public void setAssetManagementGrpcServer(IAssetManagementGrpcServer assetManagementGrpcServer) {
	this.assetManagementGrpcServer = assetManagementGrpcServer;
    }
}