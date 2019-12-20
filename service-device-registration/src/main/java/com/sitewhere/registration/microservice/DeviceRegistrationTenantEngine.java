/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.microservice;

import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.registration.configuration.DeviceRegistrationTenantConfiguration;
import com.sitewhere.registration.configuration.DeviceRegistrationTenantEngineModule;
import com.sitewhere.registration.kafka.DeviceRegistrationEventsConsumer;
import com.sitewhere.registration.kafka.UnregisteredEventsConsumer;
import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.registration.spi.kafka.IDeviceRegistrationEventsConsumer;
import com.sitewhere.registration.spi.kafka.IUnregisteredEventsConsumer;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements device
 * registration functionality.
 */
public class DeviceRegistrationTenantEngine extends MicroserviceTenantEngine<DeviceRegistrationTenantConfiguration>
	implements IDeviceRegistrationTenantEngine {

    /** Kafka consumer for unregistered device events */
    private IUnregisteredEventsConsumer unregisteredEventsConsumer;

    /** Kafka consumer for new device registrations */
    private IDeviceRegistrationEventsConsumer deviceRegistrationEventsConsumer;

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
	return new DeviceRegistrationTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Unregistered events consumer.
	this.unregisteredEventsConsumer = new UnregisteredEventsConsumer();

	// Device registration events consumer.
	this.deviceRegistrationEventsConsumer = new DeviceRegistrationEventsConsumer();

	// // Load configured registration manager.
	// this.registrationManager = (IRegistrationManager) getModuleContext()
	// .getBean(DeviceRegistrationBeans.BEAN_REGISTRATION_MANAGER);

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize unregistered events consumer.
	init.addInitializeStep(this, getUnregisteredEventsConsumer(), true);

	// Initialize device registration events consumer.
	init.addInitializeStep(this, getDeviceRegistrationEventsConsumer(), true);

	// Initialize registration manager.
	init.addInitializeStep(this, getRegistrationManager(), true);

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

	// Start unregistered events consumer.
	start.addStartStep(this, getUnregisteredEventsConsumer(), true);

	// Start device registration events consumer.
	start.addStartStep(this, getDeviceRegistrationEventsConsumer(), true);

	// Start registration manager.
	start.addStartStep(this, getRegistrationManager(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(io.sitewhere.k8s.crd.tenant.engine.dataset.
     * TenantEngineDatasetTemplate,
     * com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(TenantEngineDatasetTemplate template, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
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

	// Stop device registration events consumer.
	stop.addStopStep(this, getDeviceRegistrationEventsConsumer());

	// Stop unregistered events consumer.
	stop.addStopStep(this, getUnregisteredEventsConsumer());

	// Stop registration manager.
	stop.addStopStep(this, getRegistrationManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine#
     * getUnregisteredEventsConsumer()
     */
    @Override
    public IUnregisteredEventsConsumer getUnregisteredEventsConsumer() {
	return unregisteredEventsConsumer;
    }

    public void setUnregisteredEventsConsumer(IUnregisteredEventsConsumer unregisteredEventsConsumer) {
	this.unregisteredEventsConsumer = unregisteredEventsConsumer;
    }

    /*
     * @see
     * com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine#
     * getDeviceRegistrationEventsConsumer()
     */
    @Override
    public IDeviceRegistrationEventsConsumer getDeviceRegistrationEventsConsumer() {
	return deviceRegistrationEventsConsumer;
    }

    public void setDeviceRegistrationEventsConsumer(
	    IDeviceRegistrationEventsConsumer deviceRegistrationEventsConsumer) {
	this.deviceRegistrationEventsConsumer = deviceRegistrationEventsConsumer;
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

    public void setRegistrationManager(IRegistrationManager registrationManager) {
	this.registrationManager = registrationManager;
    }
}