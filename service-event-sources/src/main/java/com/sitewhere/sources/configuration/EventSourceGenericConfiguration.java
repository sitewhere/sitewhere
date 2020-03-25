/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Configuration entry for an event source.
 */
public class EventSourceGenericConfiguration {

    /** Unique id */
    private String id;

    /** Event source type */
    private String type;

    /** Payload decoder */
    private JsonNode decoder;

    /** Type-specific configuration parameters */
    private JsonNode configuration;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public JsonNode getDecoder() {
	return decoder;
    }

    public void setDecoder(JsonNode decoder) {
	this.decoder = decoder;
    }

    public JsonNode getConfiguration() {
	return configuration;
    }

    public void setConfiguration(JsonNode configuration) {
	this.configuration = configuration;
    }
}
