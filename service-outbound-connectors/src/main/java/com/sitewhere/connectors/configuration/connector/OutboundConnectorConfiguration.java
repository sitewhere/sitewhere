/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.configuration.connector;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.connectors.configuration.OutboundConnectorGenericConfiguration;
import com.sitewhere.microservice.configuration.json.JsonConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Base class for common event source configuration.
 */
public abstract class OutboundConnectorConfiguration extends JsonConfiguration {

    /** Unique event source id */
    private String id;

    public OutboundConnectorConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    public void apply(OutboundConnectorGenericConfiguration configuration) throws SiteWhereException {
	this.id = configuration.getId();
	loadFrom(configuration.getConfiguration());
    }

    /**
     * Load subclass from JSON.
     * 
     * @param json
     * @throws SiteWhereException
     */
    public abstract void loadFrom(JsonNode json) throws SiteWhereException;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }
}
