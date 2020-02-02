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
import com.sitewhere.commands.destination.CommandDestinationsManager;

/**
 * Provides a command destinations manager based on tenant configuration.
 */
public class CommandDestinationsManagerProvider implements Provider<CommandDestinationsManager> {

    /** Injected configuration */
    private CommandDeliveryTenantConfiguration configuration;

    @Inject
    public CommandDestinationsManagerProvider(CommandDeliveryTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.google.inject.Provider#get()
     */
    @Override
    public CommandDestinationsManager get() {
	CommandDestinationsManager manager = new CommandDestinationsManager();
	return manager;
    }

    protected CommandDeliveryTenantConfiguration getConfiguration() {
	return configuration;
    }
}
