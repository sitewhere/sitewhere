/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.solr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import com.sitewhere.configuration.instance.solr.SolrConfiguration;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Creates client connection to an Apache Solr instance.
 * 
 * @author Derek
 */
public class SolrConnection extends TenantEngineLifecycleComponent implements IDiscoverableTenantLifecycleComponent {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(SolrConnection.class);

    /** Solr configuration */
    private SolrConfiguration solrConfiguration;

    /** Solr client instance */
    private SolrClient solrClient;

    public SolrConnection(SolrConfiguration solrConfiguration) {
	super(LifecycleComponentType.Other);
	this.solrConfiguration = solrConfiguration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent#
     * isRequired()
     */
    @Override
    public boolean isRequired() {
	return true;
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
	LOGGER.info("Solr initializing with URL: " + getSolrConfiguration().getSolrServerUrl());
	setSolrClient(new HttpSolrClient.Builder(getSolrConfiguration().getSolrServerUrl()).build());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    public SolrConfiguration getSolrConfiguration() {
	return solrConfiguration;
    }

    public void setSolrConfiguration(SolrConfiguration solrConfiguration) {
	this.solrConfiguration = solrConfiguration;
    }

    public SolrClient getSolrClient() {
	return solrClient;
    }

    public void setSolrClient(SolrClient solrClient) {
	this.solrClient = solrClient;
    }
}