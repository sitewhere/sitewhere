/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.configuration;

import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.microservice.multitenant.TenantEngineModule;

/**
 * Guice module used for configuring components associated with an inbound
 * processing tenant engine.
 */
public class InboundProcessingTenantEngineModule extends TenantEngineModule<InboundProcessingTenantConfiguration> {

    public InboundProcessingTenantEngineModule(IInboundProcessingTenantEngine tenantEngine,
	    InboundProcessingTenantConfiguration configuration) {
	super(tenantEngine, configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IInboundProcessingTenantEngine.class).toInstance((IInboundProcessingTenantEngine) getTenantEngine());
	bind(IInboundProcessingConfiguration.class).toInstance(getConfiguration());
    }
}
