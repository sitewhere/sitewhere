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
 * Generic format for specify information about an event source decoder.
 */
public class DecoderGenericConfiguration {

    /** Event source type */
    private String type;

    /** Type-specific configuration parameters */
    private JsonNode configuration;

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public JsonNode getConfiguration() {
	return configuration;
    }

    public void setConfiguration(JsonNode configuration) {
	this.configuration = configuration;
    }
}
