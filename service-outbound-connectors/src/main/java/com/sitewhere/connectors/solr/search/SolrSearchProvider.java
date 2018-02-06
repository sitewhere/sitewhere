/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.solr.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.NoOpResponseParser;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.connectors.solr.SiteWhereSolrFactory;
import com.sitewhere.connectors.solr.SolrConnection;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.IDeviceEventSearchProvider;
import com.sitewhere.spi.search.ISearchProvider;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
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

    /** For JSON marshaling */
    private static ObjectMapper MAPPER = new ObjectMapper();

    /** Solr configuration */
    private SolrConnection solr;

    public SolrSearchProvider() {
	super(LifecycleComponentType.SearchProvider);
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
	LOGGER.info("Solr search provider starting.");
	if (getSolr() == null) {
	    throw new SiteWhereException("No Solr configuration provided to " + getClass().getName());
	}
	try {
	    LOGGER.info("Attempting to ping Solr server to verify availability...");
	    SolrPingResponse response = getSolr().getSolrClient().ping();
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

    /**
     * Create Solr parameters from an arbitrary query string.
     * 
     * @param queryString
     * @return
     */
    protected SolrParams createParamsFromQueryString(String queryString) {
	MultiValueMap<String, String> parsed = UriComponentsBuilder.fromHttpUrl("http://localhost?" + queryString)
		.build().getQueryParams();
	Map<String, String[]> params = new HashMap<String, String[]>();
	for (String key : parsed.keySet()) {
	    params.put(key, parsed.get(key).toArray(new String[0]));
	}
	return new ModifiableSolrParams(params);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.search.external.IDeviceEventSearchProvider#executeQuery
     * (java.lang.String)
     */
    @Override
    public List<IDeviceEvent> executeQuery(String queryString) throws SiteWhereException {
	try {
	    LOGGER.debug("About to execute Solr search with query string: " + queryString);
	    List<IDeviceEvent> results = new ArrayList<IDeviceEvent>();
	    SolrQuery solrQuery = new SolrQuery();
	    solrQuery.setQuery(queryString);
	    QueryResponse response = getSolr().getSolrClient().query(solrQuery);
	    SolrDocumentList docs = response.getResults();
	    for (SolrDocument doc : docs) {
		results.add(SiteWhereSolrFactory.parseDocument(doc));
	    }
	    return results;
	} catch (SolrServerException e) {
	    throw new SiteWhereException("Unable to execute query.", e);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to execute query.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.external.IDeviceEventSearchProvider#
     * executeQueryWithRawResponse(java.lang.String)
     */
    @Override
    public JsonNode executeQueryWithRawResponse(String queryString) throws SiteWhereException {
	try {
	    LOGGER.debug("About to execute Solr search with query string: " + queryString);

	    NoOpResponseParser rawJsonResponseParser = new NoOpResponseParser();
	    rawJsonResponseParser.setWriterType("json");

	    SolrQuery query = new SolrQuery();
	    query.add(createParamsFromQueryString(queryString));
	    QueryRequest request = new QueryRequest(query);
	    request.setResponseParser(rawJsonResponseParser);
	    NamedList<?> results = getSolr().getSolrClient().request(request);
	    return MAPPER.readTree((String) results.get("response"));
	} catch (SolrServerException e) {
	    throw new SiteWhereException("Unable to execute query.", e);
	} catch (IOException e) {
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
	    QueryResponse response = getSolr().getSolrClient().query(params);
	    SolrDocumentList docs = response.getResults();
	    while (docs.iterator().hasNext()) {
		SolrDocument doc = docs.iterator().next();
		doc.getFieldNames();
	    }
	    List<IDeviceLocation> results = new ArrayList<IDeviceLocation>();
	    return results;
	} catch (SolrServerException e) {
	    throw new SiteWhereException("Unable to execute 'getLocationsNear' query.", e);
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to execute query.", e);
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

    public SolrConnection getSolr() {
	return solr;
    }

    public void setSolr(SolrConnection solr) {
	this.solr = solr;
    }
}