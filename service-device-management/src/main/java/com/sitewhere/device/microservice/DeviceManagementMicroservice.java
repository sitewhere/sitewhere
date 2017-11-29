/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.grpc.DeviceManagementGrpcServer;
import com.sitewhere.device.spi.grpc.IDeviceManagementGrpcServer;
import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.grpc.model.client.AssetManagementApiChannel;
import com.sitewhere.grpc.model.client.AssetManagementGrpcChannel;
import com.sitewhere.grpc.model.client.DeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.client.DeviceEventManagementGrpcChannel;
import com.sitewhere.grpc.model.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.microservice.MicroserviceEnvironment;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.rest.model.asset.AssetResolver;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides device management functionality.
 * 
 * @author Derek
 */
public class DeviceManagementMicroservice extends MultitenantMicroservice<IDeviceManagementTenantEngine>
	implements IDeviceManagementMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Device Management";

    /** Identifies module resources such as configuration file */
    private static final String MODULE_IDENTIFIER = "device-management";

    /** Provides server for device management GRPC requests */
    private IDeviceManagementGrpcServer deviceManagementGrpcServer;

    /** Event management GRPC channel */
    private DeviceEventManagementGrpcChannel eventManagementGrpcChannel;

    /** Event management API channel */
    private IDeviceEventManagementApiChannel eventManagementApiChannel;

    /** Asset management GRPC channel */
    private AssetManagementGrpcChannel assetManagementGrpcChannel;

    /** Asset management API channel */
    private IAssetManagementApiChannel assetManagementApiChannel;

    /** Asset resolver */
    private IAssetResolver assetResolver;

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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getIdentifier()
     */
    @Override
    public String getIdentifier() {
	return MODULE_IDENTIFIER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IDeviceManagementTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new DeviceManagementTenantEngine(this, tenant);
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
	// Create device management GRPC server.
	this.deviceManagementGrpcServer = new DeviceManagementGrpcServer(this);

	// Event management microservice connectivity.
	this.eventManagementGrpcChannel = new DeviceEventManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_EVENT_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.eventManagementApiChannel = new DeviceEventManagementApiChannel(getEventManagementGrpcChannel());

	// Asset management microservice connectivity.
	this.assetManagementGrpcChannel = new AssetManagementGrpcChannel(this,
		MicroserviceEnvironment.HOST_ASSET_MANAGEMENT, getInstanceSettings().getGrpcPort());
	this.assetManagementApiChannel = new AssetManagementApiChannel(getAssetManagementGrpcChannel());
	this.assetResolver = new AssetResolver(getAssetManagementApiChannel(), null);

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize device management GRPC server.
	init.addInitializeStep(this, getDeviceManagementGrpcServer(), true);

	// Initialize event management GRPC channel.
	init.addInitializeStep(this, getEventManagementGrpcChannel(), true);

	// Initialize asset management GRPC channel.
	init.addInitializeStep(this, getAssetManagementGrpcChannel(), true);

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

	// Start device management GRPC server.
	start.addStartStep(this, getDeviceManagementGrpcServer(), true);

	// Start event management GRPC channel.
	start.addStartStep(this, getEventManagementGrpcChannel(), true);

	// Start asset management GRPC channel.
	start.addStartStep(this, getAssetManagementGrpcChannel(), true);

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

	// Stop event management GRPC channel.
	stop.addStopStep(this, getEventManagementGrpcChannel());

	// Stop asset management GRPC channel.
	stop.addStopStep(this, getAssetManagementGrpcChannel());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
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
     * getEventManagementGrpcChannel()
     */
    @Override
    public DeviceEventManagementGrpcChannel getEventManagementGrpcChannel() {
	return eventManagementGrpcChannel;
    }

    public void setEventManagementGrpcChannel(DeviceEventManagementGrpcChannel eventManagementGrpcChannel) {
	this.eventManagementGrpcChannel = eventManagementGrpcChannel;
    }

    /*
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice#
     * getEventManagementApiChannel()
     */
    @Override
    public IDeviceEventManagementApiChannel getEventManagementApiChannel() {
	return eventManagementApiChannel;
    }

    public void setEventManagementApiChannel(IDeviceEventManagementApiChannel eventManagementApiChannel) {
	this.eventManagementApiChannel = eventManagementApiChannel;
    }

    /*
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice#
     * getAssetManagementGrpcChannel()
     */
    @Override
    public AssetManagementGrpcChannel getAssetManagementGrpcChannel() {
	return assetManagementGrpcChannel;
    }

    public void setAssetManagementGrpcChannel(AssetManagementGrpcChannel assetManagementGrpcChannel) {
	this.assetManagementGrpcChannel = assetManagementGrpcChannel;
    }

    /*
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice#
     * getAssetManagementApiChannel()
     */
    @Override
    public IAssetManagementApiChannel getAssetManagementApiChannel() {
	return assetManagementApiChannel;
    }

    public void setAssetManagementApiChannel(IAssetManagementApiChannel assetManagementApiChannel) {
	this.assetManagementApiChannel = assetManagementApiChannel;
    }

    /*
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice#
     * getAssetResolver()
     */
    @Override
    public IAssetResolver getAssetResolver() {
	return assetResolver;
    }

    public void setAssetResolver(IAssetResolver assetResolver) {
	this.assetResolver = assetResolver;
    }
}