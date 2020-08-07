/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.microservice;

import javax.enterprise.context.ApplicationScoped;

import com.sitewhere.grpc.client.asset.AssetManagementApiChannel;
import com.sitewhere.grpc.client.asset.CachedAssetManagementApiChannel;
import com.sitewhere.grpc.client.device.CachedDeviceManagementApiChannel;
import com.sitewhere.grpc.client.device.DeviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.labels.configuration.LabelGenerationConfiguration;
import com.sitewhere.labels.configuration.LabelGenerationModule;
import com.sitewhere.labels.grpc.LabelGenerationGrpcServer;
import com.sitewhere.labels.spi.grpc.ILabelGenerationGrpcServer;
import com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice;
import com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.IMicroserviceModule;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Microservice that provides label generation functionality.
 */
@ApplicationScoped
public class LabelGenerationMicroservice extends
	MultitenantMicroservice<MicroserviceIdentifier, LabelGenerationConfiguration, ILabelGenerationTenantEngine>
	implements ILabelGenerationMicroservice {

    /** Device management API channel */
    private CachedDeviceManagementApiChannel deviceManagement;

    /** Asset management API channel */
    private CachedAssetManagementApiChannel assetManagement;

    /** Provides server for label generation GRPC requests */
    private ILabelGenerationGrpcServer labelGenerationGrpcServer;

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return "Label Generation";
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.LabelGeneration;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationClass()
     */
    @Override
    public Class<LabelGenerationConfiguration> getConfigurationClass() {
	return LabelGenerationConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#createConfigurationModule()
     */
    @Override
    public IMicroserviceModule<LabelGenerationConfiguration> createConfigurationModule() {
	return new LabelGenerationModule(getMicroserviceConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public ILabelGenerationTenantEngine createTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException {
	return new LabelGenerationTenantEngine(engine);
    }

    /*
     * @see
     * com.sitewhere.microservice.multitenant.MultitenantMicroservice#initialize(com
     * .sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create label generation GRPC server.
	this.labelGenerationGrpcServer = new LabelGenerationGrpcServer(this);

	// Create GRPC components.
	createGrpcComponents();

	// Composite step for initializing microservice.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize label generation GRPC server.
	init.addInitializeStep(this, getLabelGenerationGrpcServer(), true);

	// Initialize device management API channel.
	init.addInitializeStep(this, getDeviceManagement(), true);

	// Initialize asset management API channel.
	init.addInitializeStep(this, getAssetManagement(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.microservice.multitenant.MultitenantMicroservice#start(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Composite step for starting microservice.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start label generation GRPC server.
	start.addStartStep(this, getLabelGenerationGrpcServer(), true);

	// Start device mangement API channel.
	start.addStartStep(this, getDeviceManagement(), true);

	// Start asset mangement API channel.
	start.addStartStep(this, getAssetManagement(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#stop(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop label generation GRPC server.
	stop.addStopStep(this, getLabelGenerationGrpcServer());

	// Stop device mangement API channel.
	stop.addStopStep(this, getDeviceManagement());

	// Stop asset mangement API channel.
	stop.addStopStep(this, getAssetManagement());

	// Execute shutdown steps.
	stop.execute(monitor);

	super.stop(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Device management.
	IDeviceManagementApiChannel<?> deviceImpl = new DeviceManagementApiChannel(getInstanceSettings());
	this.deviceManagement = new CachedDeviceManagementApiChannel(deviceImpl,
		new CachedDeviceManagementApiChannel.CacheSettings());

	// Asset management.
	IAssetManagementApiChannel<?> assetImpl = new AssetManagementApiChannel(getInstanceSettings());
	this.assetManagement = new CachedAssetManagementApiChannel(assetImpl,
		new CachedAssetManagementApiChannel.CacheSettings());
    }

    /*
     * @see com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice#
     * getLabelGenerationGrpcServer()
     */
    @Override
    public ILabelGenerationGrpcServer getLabelGenerationGrpcServer() {
	return labelGenerationGrpcServer;
    }

    /*
     * @see com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice#
     * getDeviceManagement()
     */
    @Override
    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    /*
     * @see com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice#
     * getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }
}