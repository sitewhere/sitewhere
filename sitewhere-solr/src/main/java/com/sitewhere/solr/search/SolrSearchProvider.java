/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.solr.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.servlet.SolrRequestParsers;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.solr.SiteWhereSolrConfiguration;
import com.sitewhere.solr.SiteWhereSolrFactory;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.external.IDeviceEventSearchProvider;
import com.sitewhere.spi.search.external.ISearchProvider;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ISearchProvider} that executes queries against a
 * Solr server.
 * 
 * @author Derek
 */
public class SolrSearchProvider extends LifecycleComponent implements IDeviceEventSearchProvider {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Id returned for provider */
    private static final String ID = "solr";

    /** Name returned for provider */
    private static final String NAME = "Apache Solr";

    /** Provider id */
    private String id = ID;

    /** Provider name */
    private String name = NAME;

    /** Solr configuration */
    private SiteWhereSolrConfiguration solr;

    public SolrSearchProvider() {
	super(LifecycleComponentType.SearchProvider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
	LOGGER.info("Solr search provider starting.");
	if (getSolr() == null) {
	    throw new SiteWhereException("No Solr configuration provided to " + getClass().getName());
	}
	try {
	    LOGGER.info("Attempting to ping Solr server to verify availability...");
	    SolrPingResponse response = getSolr().getSolrServer().ping();
	    int pingTime = response.getQTime();
	    LOGGER.info("Solr server location verified. Ping responded in " + pingTime + " ms.");
	} catch (SolrServerException e) {
	    throw new SiteWhereException("Ping failed. Verify that Solr server is available.", e);
	} catch (IOException e) {
	    throw new SiteWhereException("Exception in ping. Verify that Solr server is available.", e);
	}
	LOGGER.info("Solr search provider started.");
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
	LOGGER.info("Stopped Solr search provider.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.search.external.IDeviceEventSearchProvider#executeQuery
     * (java. lang.String)
     */
    @Override
    public List<IDeviceEvent> executeQuery(String query) throws SiteWhereException {
	try {
	    LOGGER.info("About to execute Solr search with query string: " + query);
	    List<IDeviceEvent> results = new ArrayList<IDeviceEvent>();
	    MultiMapSolrParams params = SolrRequestParsers.parseQueryString(query);
	    QueryResponse response = getSolr().getSolrServer().query(params);
	    SolrDocumentList docs = response.getResults();
	    for (SolrDocument doc : docs) {
		results.add(SiteWhereSolrFactory.parseDocument(doc));
	    }
	    return results;
	} catch (SolrServerException e) {
	    throw new SiteWhereException("Unable to execute query.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.external.IDeviceEventSearchProvider#
     * getLocationsNear(double , double, double,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public List<IDeviceLocation> getLocationsNear(double latitude, double longitude, double distance,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	ModifiableSolrParams params = new ModifiableSolrParams();
	try {
	    QueryResponse response = getSolr().getSolrServer().query(params);
	    SolrDocumentList docs = response.getResults();
	    while (docs.iterator().hasNext()) {
		SolrDocument doc = docs.iterator().next();
		doc.getFieldNames();
	    }
	    List<IDeviceLocation> results = new ArrayList<IDeviceLocation>();
	    return results;
	} catch (SolrServerException e) {
	    throw new SiteWhereException("Unable to execute 'getLocationsNear' query.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.external.ISearchProvider#getId()
     */
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.external.ISearchProvider#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public SiteWhereSolrConfiguration getSolr() {
	return solr;
    }

    public void setSolr(SiteWhereSolrConfiguration solr) {
	this.solr = solr;
    }
}