/*
 * SearchProviderManager.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.external.ISearchProvider;
import com.sitewhere.spi.search.external.ISearchProviderManager;

/**
 * Manages a list of {@link ISearchProvider} that are available for querying device events
 * 
 * @author Derek
 */
public class SearchProviderManager implements ISearchProviderManager {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SearchProviderManager.class);

	/** List of available search providers */
	private List<ISearchProvider> searchProviders;

	/** Map of search providers by id */
	private Map<String, ISearchProvider> providersById = new HashMap<String, ISearchProvider>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Search provider manager starting...");
		for (ISearchProvider provider : getSearchProviders()) {
			provider.start();
			providersById.put(provider.getId(), provider);
		}
		LOGGER.info("Search provider manager started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		for (ISearchProvider provider : getSearchProviders()) {
			provider.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.search.external.ISearchProviderManager#getSearchProviders()
	 */
	public List<ISearchProvider> getSearchProviders() {
		return searchProviders;
	}

	public void setSearchProviders(List<ISearchProvider> searchProviders) {
		this.searchProviders = searchProviders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.search.external.ISearchProviderManager#getSearchProvider(java
	 * .lang.String)
	 */
	@Override
	public ISearchProvider getSearchProvider(String id) {
		return providersById.get(id);
	}
}