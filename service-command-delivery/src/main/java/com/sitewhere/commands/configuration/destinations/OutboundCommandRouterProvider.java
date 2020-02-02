/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration.destinations;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sitewhere.commands.configuration.CommandDeliveryTenantConfiguration;
import com.sitewhere.commands.routing.NoOpCommandRouter;
import com.sitewhere.commands.routing.OutboundCommandRouter;

/**
 * Provides a outbound command router based on tenant configuration.
 */
public class OutboundCommandRouterProvider implements Provider<OutboundCommandRouter> {

    /** Injected configuration */
    private CommandDeliveryTenantConfiguration configuration;

    @Inject
    public OutboundCommandRouterProvider(CommandDeliveryTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.google.inject.Provider#get()
     */
    @Override
    public OutboundCommandRouter get() {
	NoOpCommandRouter router = new NoOpCommandRouter();
	return router;
    }

    protected CommandDeliveryTenantConfiguration getConfiguration() {
	return configuration;
    }
}
