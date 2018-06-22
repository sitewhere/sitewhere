/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.instance.solr;

/**
 * Provides information about chosen Solr configuration after it is parsed.
 * 
 * @author Derek
 */
public class SolrConfigurationChoice {

    /** Configuration type */
    private SolrConfigurationType type;

    /** Configuration object */
    private Object configuration;

    public SolrConfigurationChoice(SolrConfigurationType type, Object configuration) {
	this.type = type;
	this.configuration = configuration;
    }

    public SolrConfigurationType getType() {
	return type;
    }

    public void setType(SolrConfigurationType type) {
	this.type = type;
    }

    public Object getConfiguration() {
	return configuration;
    }

    public void setConfiguration(Object configuration) {
	this.configuration = configuration;
    }
}