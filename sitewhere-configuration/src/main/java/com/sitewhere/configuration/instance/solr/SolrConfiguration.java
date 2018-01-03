/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.instance.solr;

/**
 * Configuration settings for Apache Solr connectivity.
 * 
 * @author Derek
 */
public class SolrConfiguration {

    /** Default URL for contacting Solr server */
    private static final String DEFAULT_SOLR_URL = "http://localhost:8983/solr";

    /** URL used to interact with Solr server */
    private String solrServerUrl = DEFAULT_SOLR_URL;

    public String getSolrServerUrl() {
	return solrServerUrl;
    }

    public void setSolrServerUrl(String solrServerUrl) {
	this.solrServerUrl = solrServerUrl;
    }
}