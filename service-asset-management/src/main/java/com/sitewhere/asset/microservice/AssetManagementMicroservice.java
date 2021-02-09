/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.asset.microservice;

import javax.enterprise.context.ApplicationScoped;

import com.sitewhere.asset.configuration.AssetManagementConfiguration;
import com.sitewhere.asset.configuration.AssetManagementModule;
import com.sitewhere.asset.grpc.AssetManagementGrpcServer;
import com.sitewhere.asset.spi.grpc.IAssetManagementGrpcServer;
import com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice;
import com.sitewhere.asset.spi.microservice.IAssetManagementTenantEngine;
import com.sitewhere.grpc.client.device.DeviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.IMicroserviceModule;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Microservice that provides asset management functionality.
 */
@ApplicationScoped
public class AssetManagementMicroservice extends
	MultitenantMicroservice<MicroserviceIdentifier, AssetManagementConfiguration, IAssetManagementTenantEngine>
	implements IAssetManagementMicroservice {

    /** Provides server for asset management GRPC requests */
    private IAssetManagementGrpcServer assetManagementGrpcServer;

    /** Device management API demux */
    private IDeviceManagementApiChannel<?> deviceManagementApiChannel;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return "Asset Management";
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.AssetManagement;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationClass()
     */
    @Override
    public Class<AssetManagementConfiguration> getConfigurationClass() {
	return AssetManagementConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#createConfigurationModule()
     */
    @Override
    public IMicroserviceModule<AssetManagementConfiguration> createConfigurationModule() {
	return new AssetManagementModule(getMicroserviceConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public IAssetManagementTenantEngine createTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException {
	return new AssetManagementTenantEngine(engine);
    }

    /*
     * @see
     * com.sitewhere.microservice.multitenant.MultitenantMicroservice#initialize(com
     * .sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create GRPC components.
	createGrpcComponents();

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize device management GRPC server.
	init.addInitializeStep(this, getAssetManagementGrpcServer(), true);

	// Initialize device management API channel.
	init.addInitializeStep(this, getDeviceManagementApiChannel(), true);

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

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start asset management GRPC server.
	start.addStartStep(this, getAssetManagementGrpcServer(), true);

	// Start device mangement API channel.
	start.addStartStep(this, getDeviceManagementApiChannel(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#stop(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop asset management GRPC server.
	stop.addStopStep(this, getAssetManagementGrpcServer());

	// Stop device mangement API channel.
	stop.addStopStep(this, getDeviceManagementApiChannel());

	// Execute shutdown steps.
	stop.execute(monitor);

	super.stop(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Create asset management GRPC server.
	this.assetManagementGrpcServer = new AssetManagementGrpcServer(this);

	// Device management.
	this.deviceManagementApiChannel = new DeviceManagementApiChannel(getInstanceSettings());
    }

    /*
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice#
     * getAssetManagementGrpcServer()
     */
    @Override
    public IAssetManagementGrpcServer getAssetManagementGrpcServer() {
	return assetManagementGrpcServer;
    }

    /*
     * @see com.sitewhere.asset.spi.microservice.IAssetManagementMicroservice#
     * getDeviceManagementApiChannel()
     */
    @Override
    public IDeviceManagementApiChannel<?> getDeviceManagementApiChannel() {
	return deviceManagementApiChannel;
    }
}