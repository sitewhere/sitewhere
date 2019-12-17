/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.microservice;

import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.sources.configuration.EventSourcesTenantConfiguration;
import com.sitewhere.sources.configuration.EventSourcesTenantEngineModule;
import com.sitewhere.sources.spi.IEventSourcesManager;
import com.sitewhere.sources.spi.microservice.IEventSourcesTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements event
 * sources functionality.
 */
public class EventSourcesTenantEngine extends MicroserviceTenantEngine<EventSourcesTenantConfiguration>
	implements IEventSourcesTenantEngine {

    /** Event sources manager */
    private IEventSourcesManager eventSourcesManager;

    public EventSourcesTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<EventSourcesTenantConfiguration> getConfigurationClass() {
	return EventSourcesTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationModule()
     */
    @Override
    public ITenantEngineModule<EventSourcesTenantConfiguration> getConfigurationModule() {
	return new EventSourcesTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// this.eventSourcesManager = (IEventSourcesManager) getModuleContext()
	// .getBean(EventSourcesBeans.BEAN_EVENT_SOURCES_MANAGER);

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize event sources manager.
	init.addInitializeStep(this, getEventSourcesManager(), true);

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

	// Start event sources manager.
	start.addStartStep(this, getEventSourcesManager(), true);

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
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop event sources manager.
	start.addStopStep(this, getEventSourcesManager());

	// Execute shutdown steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.sources.spi.microservice.IEventSourcesTenantEngine#
     * getEventSourcesManager()
     */
    @Override
    public IEventSourcesManager getEventSourcesManager() {
	return eventSourcesManager;
    }

    public void setEventSourcesManager(IEventSourcesManager eventSourcesManager) {
	this.eventSourcesManager = eventSourcesManager;
    }
}