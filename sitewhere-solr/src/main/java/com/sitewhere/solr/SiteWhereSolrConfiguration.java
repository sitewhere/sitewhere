/*
 * SiteWhereSolrConfiguration.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.solr;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.InitializingBean;

/**
 * Class for base Solr client configuration settings.
 * 
 * @author Derek
 */
public class SiteWhereSolrConfiguration implements InitializingBean {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereSolrConfiguration.class);

	/** Default URL for contacting Solr server */
	private static final String DEFAULT_SOLR_URL = "http://localhost:8983/solr";

	/** URL used to interact with Solr server */
	private String solrServerUrl = DEFAULT_SOLR_URL;

	/** Solr server instance */
	private HttpSolrServer solrServer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("Solr initializing with URL: " + getSolrServerUrl());
		setSolrServer(new HttpSolrServer(getSolrServerUrl()));
	}

	public String getSolrServerUrl() {
		return solrServerUrl;
	}

	public void setSolrServerUrl(String solrServerUrl) {
		this.solrServerUrl = solrServerUrl;
	}

	public HttpSolrServer getSolrServer() {
		return solrServer;
	}

	public void setSolrServer(HttpSolrServer solrServer) {
		this.solrServer = solrServer;
	}
}