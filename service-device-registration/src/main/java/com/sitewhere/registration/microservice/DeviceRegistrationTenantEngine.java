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

import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.registration.configuration.DeviceRegistrationTenantConfiguration;
import com.sitewhere.registration.configuration.DeviceRegistrationTenantEngineModule;
import com.sitewhere.registration.kafka.RegistrationEventsPipeline;
import com.sitewhere.registration.kafka.UnregisteredEventsPipeline;
import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.registration.spi.kafka.IRegistrationEventsPipeline;
import com.sitewhere.registration.spi.kafka.IUnregisteredEventsPipeline;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements device
 * registration functionality.
 */
public class DeviceRegistrationTenantEngine extends MicroserviceTenantEngine<DeviceRegistrationTenantConfiguration>
	implements IDeviceRegistrationTenantEngine {

    /** Kafka pipeline for unregistered device events */
    private IUnregisteredEventsPipeline unregisteredEventsPipeline;

    /** Kafka pipeline for new device registrations */
    private IRegistrationEventsPipeline registrationEventsPipeline;

    /** Device registration manager */
    private IRegistrationManager registrationManager;

    public DeviceRegistrationTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<DeviceRegistrationTenantConfiguration> getConfigurationClass() {
	return DeviceRegistrationTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<DeviceRegistrationTenantConfiguration> createConfigurationModule() {
	return new DeviceRegistrationTenantEngineModule(this, getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * getTenantBootstrapPrerequisites()
     */
    @Override
    public IFunctionIdentifier[] getTenantBootstrapPrerequisites() {
	return new IFunctionIdentifier[] { MicroserviceIdentifier.DeviceManagement };
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	this.registrationManager = getInjector().getInstance(IRegistrationManager.class);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Unregistered events pipeline.
	this.unregisteredEventsPipeline = new UnregisteredEventsPipeline();

	// Device registration events pipeline.
	this.registrationEventsPipeline = new RegistrationEventsPipeline();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize registration manager.
	init.addInitializeStep(this, getRegistrationManager(), true);

	// Initialize unregistered events pipeline.
	init.addInitializeStep(this, getUnregisteredEventsPipeline(), true);

	// Initialize device registration events pipeline.
	init.addInitializeStep(this, getRegistrationEventsPipeline(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start registration manager.
	start.addStartStep(this, getRegistrationManager(), true);

	// Start unregistered events pipeline.
	start.addStartStep(this, getUnregisteredEventsPipeline(), true);

	// Start device registration events pipeline.
	start.addStartStep(this, getRegistrationEventsPipeline(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop device registration events pipeline.
	stop.addStopStep(this, getRegistrationEventsPipeline());

	// Stop unregistered events pipeline.
	stop.addStopStep(this, getUnregisteredEventsPipeline());

	// Stop registration manager.
	stop.addStopStep(this, getRegistrationManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine#
     * getUnregisteredEventsPipeline()
     */
    @Override
    public IUnregisteredEventsPipeline getUnregisteredEventsPipeline() {
	return unregisteredEventsPipeline;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine#
     * getRegistrationEventsPipeline()
     */
    @Override
    public IRegistrationEventsPipeline getRegistrationEventsPipeline() {
	return registrationEventsPipeline;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine#
     * getRegistrationManager()
     */
    @Override
    public IRegistrationManager getRegistrationManager() {
	return registrationManager;
    }
}