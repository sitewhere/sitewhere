/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search.microservice;

import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.search.configuration.EventSearchTenantConfiguration;
import com.sitewhere.search.configuration.EventSearchTenantEngineModule;
import com.sitewhere.search.spi.ISearchProvidersManager;
import com.sitewhere.search.spi.microservice.IEventSearchTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements event
 * search functionality.
 */
public class EventSearchTenantEngine extends MicroserviceTenantEngine<EventSearchTenantConfiguration>
	implements IEventSearchTenantEngine {

    /** Manages the outbound connectors for this tenant */
    private ISearchProvidersManager searchProvidersManager;

    public EventSearchTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<EventSearchTenantConfiguration> getConfigurationClass() {
	return EventSearchTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<EventSearchTenantConfiguration> createConfigurationModule() {
	return new EventSearchTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// // Create search providers manager.
	// this.searchProvidersManager = (ISearchProvidersManager) getModuleContext()
	// .getBean(EventSearchBeans.BEAN_SEARCH_PROVIDERS_MANAGER);

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize search providers manager.
	init.addInitializeStep(this, getSearchProvidersManager(), true);

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

	// Start search providers manager.
	start.addStartStep(this, getSearchProvidersManager(), true);

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
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop search providers manager.
	start.addStopStep(this, getSearchProvidersManager());

	// Execute shutdown steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.search.spi.microservice.IEventSearchTenantEngine#
     * getSearchProvidersManager()
     */
    @Override
    public ISearchProvidersManager getSearchProvidersManager() {
	return searchProvidersManager;
    }

    protected void setSearchProvidersManager(ISearchProvidersManager searchProvidersManager) {
	this.searchProvidersManager = searchProvidersManager;
    }
}