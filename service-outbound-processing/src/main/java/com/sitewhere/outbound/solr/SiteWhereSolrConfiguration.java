/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.solr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Class for base Solr client configuration settings.
 * 
 * @author Derek
 */
public class SiteWhereSolrConfiguration extends TenantLifecycleComponent
	implements IDiscoverableTenantLifecycleComponent {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Bean name where global Solr configuration is expected */
    public static final String SOLR_CONFIGURATION_BEAN = "swSolrConfiguration";

    /** Default URL for contacting Solr server */
    private static final String DEFAULT_SOLR_URL = "http://localhost:8983/solr";

    /** URL used to interact with Solr server */
    private String solrServerUrl = DEFAULT_SOLR_URL;

    /** Solr client instance */
    private SolrClient solrClient;

    public SiteWhereSolrConfiguration() {
	super(LifecycleComponentType.Other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	LOGGER.info("Solr initializing with URL: " + getSolrServerUrl());
	setSolrClient(new HttpSolrClient.Builder(getSolrServerUrl()).build());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public String getSolrServerUrl() {
	return solrServerUrl;
    }

    public void setSolrServerUrl(String solrServerUrl) {
	this.solrServerUrl = solrServerUrl;
    }

    public SolrClient getSolrClient() {
	return solrClient;
    }

    public void setSolrClient(SolrClient solrClient) {
	this.solrClient = solrClient;
    }
}