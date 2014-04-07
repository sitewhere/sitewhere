/*
 * SolrSearchProvider.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.solr.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.sitewhere.solr.SiteWhereSolrConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.external.IDeviceEventSearchProvider;
import com.sitewhere.spi.search.external.ISearchProvider;

/**
 * Implementation of {@link ISearchProvider} that executes queries against a Solr server.
 * 
 * @author Derek
 */
public class SolrSearchProvider implements IDeviceEventSearchProvider {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SolrSearchProvider.class);

	/** Name returned for provider */
	private static final String NAME = "Apache Solr Search Provider";

	/** Solr configuration */
	private SiteWhereSolrConfiguration solr;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
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
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Stopped Solr search provider.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.search.external.IDeviceEventSearchProvider#executeQuery(java.
	 * lang.String)
	 */
	@Override
	public List<IDeviceEvent> executeQuery(String query) throws SiteWhereException {
		try {
			List<IDeviceEvent> results = new ArrayList<IDeviceEvent>();
			SolrQuery sq = new SolrQuery(query);
			QueryResponse response = getSolr().getSolrServer().query(sq);
			SolrDocumentList docs = response.getResults();
			while (docs.iterator().hasNext()) {
				SolrDocument doc = docs.iterator().next();
				doc.getFieldNames();
			}
			return results;
		} catch (SolrServerException e) {
			throw new SiteWhereException("Unable to execute query.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.search.external.IDeviceEventSearchProvider#getLocationsNear(double
	 * , double, double, com.sitewhere.spi.search.IDateRangeSearchCriteria)
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
	 * @see com.sitewhere.spi.search.external.ISearchProvider#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	public SiteWhereSolrConfiguration getSolr() {
		return solr;
	}

	public void setSolr(SiteWhereSolrConfiguration solr) {
		this.solr = solr;
	}
}