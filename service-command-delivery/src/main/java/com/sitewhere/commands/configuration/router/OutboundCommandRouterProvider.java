/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration.router;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sitewhere.commands.configuration.CommandDeliveryTenantConfiguration;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.spi.SiteWhereException;

/**
 * Provides a outbound command router based on tenant configuration.
 */
public class OutboundCommandRouterProvider implements Provider<IOutboundCommandRouter> {

    /** Injected configuration */
    private CommandDeliveryTenantConfiguration configuration;

    /** Injected handle to command destinations manager */
    private ICommandDestinationsManager manager;

    @Inject
    public OutboundCommandRouterProvider(CommandDeliveryTenantConfiguration configuration,
	    ICommandDestinationsManager manager) {
	this.configuration = configuration;
    }

    /*
     * @see com.google.inject.Provider#get()
     */
    @Override
    public IOutboundCommandRouter get() {
	try {
	    return OutboundCommandRouterParser.parse(getManager(), getConfiguration());
	} catch (SiteWhereException e) {
	    return null;
	}
    }

    protected CommandDeliveryTenantConfiguration getConfiguration() {
	return configuration;
    }

    protected ICommandDestinationsManager getManager() {
	return manager;
    }
}
