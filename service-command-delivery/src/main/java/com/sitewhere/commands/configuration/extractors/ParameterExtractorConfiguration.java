/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration.extractors;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.commands.configuration.destinations.CommandDestinationGenericConfiguration;
import com.sitewhere.microservice.configuration.json.JsonConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Base class for common command destination parameter extractor configuration.
 */
public abstract class ParameterExtractorConfiguration extends JsonConfiguration {

    public ParameterExtractorConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    public void apply(CommandDestinationGenericConfiguration configuration) throws SiteWhereException {
	loadFrom(configuration.getParameterExtractor().getConfiguration());
    }

    /**
     * Load subclass from JSON.
     * 
     * @param json
     * @throws SiteWhereException
     */
    public abstract void loadFrom(JsonNode json) throws SiteWhereException;
}
