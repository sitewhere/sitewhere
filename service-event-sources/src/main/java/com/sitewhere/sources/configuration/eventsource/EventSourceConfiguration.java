/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration.eventsource;

import org.apache.commons.text.StringSubstitutor;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.sources.configuration.EventSourceGenericConfiguration;
import com.sitewhere.spi.SiteWhereException;

/**
 * Base class for common event source configuration.
 */
public abstract class EventSourceConfiguration {

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

    /**
     * Parse an integer value using variable substitution.
     * 
     * @param fieldName
     * @param json
     * @param defaultValue
     * @return
     * @throws SiteWhereException
     */
    public int configurableInt(String fieldName, JsonNode json, int defaultValue) throws SiteWhereException {
	JsonNode field = json.get(fieldName);
	if (field == null) {
	    return defaultValue;
	}
	StringSubstitutor sub = new StringSubstitutor(new SiteWhereStringLookup());
	try {
	    return field.isTextual() ? Integer.parseInt(sub.replace(field.textValue())) : field.asInt();
	} catch (NumberFormatException e) {
	    throw new SiteWhereException(
		    String.format("Unable to parse integer configuration parameter '%s' with value of '%s'.", fieldName,
			    field.toString()));
	}
    }

    /**
     * Parse a string value using variable substitution.
     * 
     * @param fieldName
     * @param json
     * @param defaultValue
     * @return
     * @throws SiteWhereException
     */
    public String configurableString(String fieldName, JsonNode json, String defaultValue) throws SiteWhereException {
	JsonNode field = json.get(fieldName);
	if (field == null) {
	    return defaultValue;
	}
	StringSubstitutor sub = new StringSubstitutor(new SiteWhereStringLookup());
	return sub.replace(field.textValue());
    }

    /**
     * Parse a boolean value using variable substitution.
     * 
     * @param fieldName
     * @param json
     * @param defaultValue
     * @return
     * @throws SiteWhereException
     */
    public boolean configurableBoolean(String fieldName, JsonNode json, boolean defaultValue)
	    throws SiteWhereException {
	JsonNode field = json.get(fieldName);
	if (field == null) {
	    return defaultValue;
	}
	StringSubstitutor sub = new StringSubstitutor(new SiteWhereStringLookup());
	try {
	    return field.isBoolean() ? field.asBoolean() : Boolean.parseBoolean(sub.replace(field.textValue()));
	} catch (NumberFormatException e) {
	    throw new SiteWhereException(
		    String.format("Unable to parse boolean configuration parameter '%s' with value of '%s'.", fieldName,
			    field.toString()));
	}
    }

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
