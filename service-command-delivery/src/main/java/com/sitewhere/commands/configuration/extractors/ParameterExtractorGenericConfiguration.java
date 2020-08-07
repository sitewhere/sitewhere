/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration.extractors;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Generic information used to configure a parameter extractor for a command
 * destination.
 */
public class ParameterExtractorGenericConfiguration {

    /** Destination type */
    private String type;

    /** Custom configuration for type */
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
