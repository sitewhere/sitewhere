/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.configuration.manager;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Configuration entry for an event source.
 */
public class LabelGeneratorGenericConfiguration {

    /** Unique id */
    private String id;

    /** Generator name */
    private String name;

    /** Event source type */
    private String type;

    /** Type-specific configuration parameters */
    private JsonNode configuration;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

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
