/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.registration.kafka.UnregisteredEventsConsumer;
import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.registration.spi.kafka.IUnregisteredEventsConsumer;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.spring.DeviceRegistrationBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements device
 * registration functionality.
 * 
 * @author Derek
 */
public class DeviceRegistrationTenantEngine extends MicroserviceTenantEngine
	implements IDeviceRegistrationTenantEngine {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Kafka consumer for unregistered device events */
    private IUnregisteredEventsConsumer unregisteredEventsConsumer;

    /** Device registration manager */
    private IRegistrationManager registrationManager;

    public DeviceRegistrationTenantEngine(IMultitenantMicroservice<?> microservice, ITenant tenant) {
	super(microservice, tenant);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.unregisteredEventsConsumer = new UnregisteredEventsConsumer(this);

	// Load configured registration manager.
	this.registrationManager = (IRegistrationManager) getModuleContext()
		.getBean(DeviceRegistrationBeans.BEAN_REGISTRATION_MANAGER);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start unregistered events consumer.
	start.addStartStep(this, getUnregisteredEventsConsumer(), true);

	// Start registration manager.
	start.addStartStep(this, getRegistrationManager(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.spi.microservice.multitenant.ITenantTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(ITenantTemplate template, ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

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
     * getRegistrationManager()
     */
    @Override
    public IRegistrationManager getRegistrationManager() {
	return registrationManager;
    }

    public void setRegistrationManager(IRegistrationManager registrationManager) {
	this.registrationManager = registrationManager;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}