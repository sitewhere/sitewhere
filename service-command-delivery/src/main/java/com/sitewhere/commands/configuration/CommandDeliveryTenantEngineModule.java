/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration;

import com.sitewhere.commands.configuration.destinations.CommandDestinationsManagerProvider;
import com.sitewhere.commands.configuration.destinations.OutboundCommandRouterProvider;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.microservice.multitenant.TenantEngineModule;

/**
 * Guice module used for configuring components associated with a command
 * delivery tenant engine.
 */
public class CommandDeliveryTenantEngineModule extends TenantEngineModule<CommandDeliveryTenantConfiguration> {

    public CommandDeliveryTenantEngineModule(CommandDeliveryTenantConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(CommandDeliveryTenantConfiguration.class).toInstance(getConfiguration());
	bind(ICommandDestinationsManager.class).toProvider(CommandDestinationsManagerProvider.class);
	bind(IOutboundCommandRouter.class).toProvider(OutboundCommandRouterProvider.class);
    }
}
