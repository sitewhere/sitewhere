/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration.eventsource;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.microservice.configuration.json.JsonConfiguration;
import com.sitewhere.sources.configuration.EventSourceGenericConfiguration;
import com.sitewhere.spi.SiteWhereException;

/**
 * Base class for common event source configuration.
 */
public abstract class EventSourceConfiguration extends JsonConfiguration {

    /** Unique event source id */
    private String id;

    /** Decoder type */
    private String decoder;

    public void apply(EventSourceGenericConfiguration configuration) throws SiteWhereException {
	this.id = configuration.getId();
	this.decoder = configuration.getDecoder();
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

    public String getDecoder() {
	return decoder;
    }

    public void setDecoder(String decoder) {
	this.decoder = decoder;
    }
}
