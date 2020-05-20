/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration.destinations.coap;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.commands.configuration.destinations.CommandDestinationConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration for a CoAP command destination.
 */
public class CoapConfiguration extends CommandDestinationConfiguration {

    public CoapConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see com.sitewhere.commands.configuration.destinations.
     * CommandDestinationConfiguration#loadFrom(com.fasterxml.jackson.databind.
     * JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
    }
}