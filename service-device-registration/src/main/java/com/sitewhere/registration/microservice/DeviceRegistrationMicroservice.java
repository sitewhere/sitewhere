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
package com.sitewhere.registration.microservice;

import javax.enterprise.context.ApplicationScoped;

import com.sitewhere.grpc.client.asset.AssetManagementApiChannel;
import com.sitewhere.grpc.client.device.CachedDeviceManagementApiChannel;
import com.sitewhere.grpc.client.device.DeviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.registration.configuration.DeviceRegistrationConfiguration;
import com.sitewhere.registration.configuration.DeviceRegistrationModule;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationMicroservice;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.IMicroserviceModule;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Microservice that provides device registration functionality.
 */
@ApplicationScoped
public class DeviceRegistrationMicroservice extends
	MultitenantMicroservice<MicroserviceIdentifier, DeviceRegistrationConfiguration, IDeviceRegistrationTenantEngine>
	implements IDeviceRegistrationMicroservice {

    /** Device management API channel */
    private CachedDeviceManagementApiChannel deviceManagement;

    /** Asset management API channel */
    // private CachedAssetManagementApiChannel assetManagement;
    /** Asset management API channel */
    private IAssetManagementApiChannel<?> assetManagementApiChannel;

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return "Device Registration";
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.DeviceRegistration;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationClass()
     */
    @Override
    public Class<DeviceRegistrationConfiguration> getConfigurationClass() {
	return DeviceRegistrationConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#createConfigurationModule()
     */
    @Override
    public IMicroserviceModule<DeviceRegistrationConfiguration> createConfigurationModule() {
	return new DeviceRegistrationModule(getMicroserviceConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public IDeviceRegistrationTenantEngine createTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException {
	return new DeviceRegistrationTenantEngine(engine);
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

	// Start device management API channel.
	start.addStartStep(this, getDeviceManagement(), true);

	// Start asset management API channel.
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

	// Stop device management API channel.
	stop.addStopStep(this, getDeviceManagement());

	// Stop asset management API channel.
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
	IDeviceManagementApiChannel<?> wrappedDeviceManagement = new DeviceManagementApiChannel(getInstanceSettings());
	this.deviceManagement = new CachedDeviceManagementApiChannel(wrappedDeviceManagement,
		new CachedDeviceManagementApiChannel.CacheSettings());
	// Asset management microservice connectivity.
	this.assetManagementApiChannel = new AssetManagementApiChannel(getInstanceSettings());
    }

    /*
     * @see
     * com.sitewhere.registration.spi.microservice.IDeviceRegistrationMicroservice#
     * getDeviceManagement()
     */
    @Override
    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.microservice.IDeviceRegistrationMicroservice#
     * getAssetManagement()
     */
    @Override
    public IAssetManagement getAssetManagement() {
	return assetManagementApiChannel;
    }
}