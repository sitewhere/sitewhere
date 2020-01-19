/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration.eventsource;

import org.apache.commons.text.lookup.StringLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles variable substitution in configuration attributes.
 */
public class SiteWhereStringLookup implements StringLookup {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(SiteWhereStringLookup.class);

    /*
     * @see org.apache.commons.text.lookup.StringLookup#lookup(java.lang.String)
     */
    @Override
    public String lookup(String key) {
	// Handle variable reference.
	if (key.indexOf(':') > -1) {
	    String[] parts = key.split(":");
	    String env = parts[0];
	    String defaultValue = parts[1];
	    String envValue = System.getenv().get(env);
	    LOGGER.info(
		    String.format("Looked up '%s' as ENV value %s=%s default=%s", key, env, envValue, defaultValue));
	    return envValue != null ? envValue : defaultValue;
	}
	return key;
    }
}
