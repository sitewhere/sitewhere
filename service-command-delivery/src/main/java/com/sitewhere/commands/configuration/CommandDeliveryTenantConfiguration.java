/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.commands.configuration.destinations.GenericCommandDestinationConfiguration;
import com.sitewhere.commands.configuration.router.GenericRouterConfiguration;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps command delivery tenant engine YAML configuration to objects.
 */
public class CommandDeliveryTenantConfiguration implements ITenantEngineConfiguration {

    /** Router configuration */
    private GenericRouterConfiguration router;

    /** List of command destination configurations */
    private List<GenericCommandDestinationConfiguration> commandDestinations = new ArrayList<>();

    public GenericRouterConfiguration getRouter() {
	return router;
    }

    public void setRouter(GenericRouterConfiguration router) {
	this.router = router;
    }

    public List<GenericCommandDestinationConfiguration> getCommandDestinations() {
	return commandDestinations;
    }

    public void setCommandDestinations(List<GenericCommandDestinationConfiguration> commandDestinations) {
	this.commandDestinations = commandDestinations;
    }
}
