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
package com.sitewhere.devicestate.microservice;

import javax.enterprise.context.ApplicationScoped;

import com.sitewhere.devicestate.configuration.DeviceStateConfiguration;
import com.sitewhere.devicestate.configuration.DeviceStateModule;
import com.sitewhere.devicestate.grpc.DeviceStateGrpcServer;
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
 * Microservice that provides device state mangagement functionality.
 */
@ApplicationScoped
public class DeviceStateMicroservice
	extends MultitenantMicroservice<MicroserviceIdentifier, DeviceStateConfiguration, IDeviceStateTenantEngine>
	implements IDeviceStateMicroservice {

    /** Provides server for device management GRPC requests */
    private IDeviceStateGrpcServer deviceStateGrpcServer;

    /** Device management API channel */
    private CachedDeviceManagementApiChannel deviceManagement;

    /** Asset management API channel */
    private CachedAssetManagementApiChannel assetManagement;

    /** Device event management API channel */
    private IDeviceEventManagementApiChannel<?> deviceEventManagementApiChannel;

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return "State Management";
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.DeviceState;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationClass()
     */
    @Override
    public Class<DeviceStateConfiguration> getConfigurationClass() {
	return DeviceStateConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#createConfigurationModule()
     */
    @Override
    public IMicroserviceModule<DeviceStateConfiguration> createConfigurationModule() {
	return new DeviceStateModule(getMicroserviceConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public IDeviceStateTenantEngine createTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException {
	return new DeviceStateTenantEngine(engine);
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

	// Composite step for initializing microservice.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize device state GRPC server.
	init.addInitializeStep(this, getDeviceStateGrpcServer(), true);

	// Initialize device management API channel.
	init.addInitializeStep(this, getDeviceManagement(), true);

	// Initialize asset management API channel.
	init.addInitializeStep(this, getAssetManagement(), true);

	// Initialize device event management API channel.
	init.addInitializeStep(this, getDeviceEventManagementApiChannel(), true);

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

	// Start device state GRPC server.
	start.addStartStep(this, getDeviceStateGrpcServer(), true);

	// Start device mangement API channel.
	start.addStartStep(this, getDeviceManagement(), true);

	// Start asset mangement API channel.
	start.addStartStep(this, getAssetManagement(), true);

	// Start device event mangement API channel.
	start.addStartStep(this, getDeviceEventManagementApiChannel(), true);

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

	// Stop device mangement API channel.
	stop.addStopStep(this, getDeviceManagement());

	// Stop asset mangement API channel.
	stop.addStopStep(this, getAssetManagement());

	// Stop device event mangement API channel.
	stop.addStopStep(this, getDeviceEventManagementApiChannel());

	// Stop device state GRPC server.
	stop.addStopStep(this, getDeviceStateGrpcServer());

	// Execute shutdown steps.
	stop.execute(monitor);

	super.stop(monitor);
    }

    /**
     * Create GRPC components required by the microservice.
     */
    private void createGrpcComponents() {
	// Create device state GRPC server.
	this.deviceStateGrpcServer = new DeviceStateGrpcServer(this);

	// Device management.
	IDeviceManagementApiChannel<?> dmWrapped = new DeviceManagementApiChannel(getInstanceSettings());
	this.deviceManagement = new CachedDeviceManagementApiChannel(dmWrapped,
		new CachedDeviceManagementApiChannel.CacheSettings());

	// Asset management.
	IAssetManagementApiChannel<?> amWrapped = new AssetManagementApiChannel(getInstanceSettings());
	this.assetManagement = new CachedAssetManagementApiChannel(amWrapped,
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

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice#
     * getDeviceManagement()
     */
    @Override
    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice#
     * getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice#
     * getDeviceEventManagementApiChannel()
     */
    @Override
    public IDeviceEventManagementApiChannel<?> getDeviceEventManagementApiChannel() {
	return deviceEventManagementApiChannel;
    }
}