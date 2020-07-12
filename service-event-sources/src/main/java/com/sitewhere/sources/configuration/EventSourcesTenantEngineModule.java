/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration;

import com.sitewhere.microservice.multitenant.TenantEngineModule;
import com.sitewhere.sources.manager.EventSourcesManager;
import com.sitewhere.sources.spi.IEventSourcesManager;
import com.sitewhere.sources.spi.microservice.IEventSourcesTenantEngine;

/**
 * Guice module used for configuring components associated with an event sources
 * tenant engine.
 */
public class EventSourcesTenantEngineModule extends TenantEngineModule<EventSourcesTenantConfiguration> {

    public EventSourcesTenantEngineModule(IEventSourcesTenantEngine tenantEngine,
	    EventSourcesTenantConfiguration configuration) {
	super(tenantEngine, configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IEventSourcesTenantEngine.class).toInstance((IEventSourcesTenantEngine) getTenantEngine());
	bind(EventSourcesTenantConfiguration.class).toInstance(getConfiguration());
	bind(IEventSourcesManager.class).to(EventSourcesManager.class);
    }
}
