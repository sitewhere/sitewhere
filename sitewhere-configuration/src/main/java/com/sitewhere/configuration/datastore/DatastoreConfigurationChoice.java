/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.datastore;

/**
 * Provides information about chosen datastore configuration after it is parsed.
 * 
 * @author Derek
 */
public class DatastoreConfigurationChoice {

    /** Configuration type */
    private DatastoreConfigurationType type;

    /** Configuration object */
    private Object configuration;

    public DatastoreConfigurationChoice(DatastoreConfigurationType type, Object configuration) {
	this.type = type;
	this.configuration = configuration;
    }

    public DatastoreConfigurationType getType() {
	return type;
    }

    public void setType(DatastoreConfigurationType type) {
	this.type = type;
    }

    public Object getConfiguration() {
	return configuration;
    }

    public void setConfiguration(Object configuration) {
	this.configuration = configuration;
    }
}