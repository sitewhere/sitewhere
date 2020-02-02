/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.configuration;

import com.sitewhere.connectors.manager.OutboundConnectorsManager;
import com.sitewhere.connectors.spi.IOutboundConnectorsManager;
import com.sitewhere.microservice.multitenant.TenantEngineModule;

/**
 * Guice module used for configuring components associated with an outbound
 * connectors tenant engine.
 */
public class OutboundConnectorsTenantEngineModule extends TenantEngineModule<OutboundConnectorsTenantConfiguration> {

    public OutboundConnectorsTenantEngineModule(OutboundConnectorsTenantConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(OutboundConnectorsTenantConfiguration.class).toInstance(getConfiguration());
	bind(IOutboundConnectorsManager.class).to(OutboundConnectorsManager.class);
    }
}
