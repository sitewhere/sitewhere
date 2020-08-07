/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.microservice.api.search.ISearchProvider;
import com.sitewhere.microservice.lifecycle.LifecycleComponent;
import com.sitewhere.search.spi.ISearchProvidersManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;

/**
 * Manages a list of {@link ISearchProvider} that are available for querying
 * device events
 */
public class SearchProviderManager extends LifecycleComponent implements ISearchProvidersManager {

    /** List of available search providers */
    private List<ISearchProvider> searchProviders = new ArrayList<ISearchProvider>();

    /** Map of search providers by id */
    private Map<String, ISearchProvider> providersById = new HashMap<String, ISearchProvider>();

    public SearchProviderManager() {
	super(LifecycleComponentType.SearchProviderManager);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getProvidersById().clear();
	for (ISearchProvider provider : getSearchProviders()) {
	    try {
		initializeNestedComponent(provider, monitor, true);
		getProvidersById().put(provider.getId(), provider);
	    } catch (SiteWhereException e) {
		getLogger().error("Error initializing search provider.", e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (ISearchProvider provider : getSearchProviders()) {
	    try {
		if (provider.getLifecycleStatus() == LifecycleStatus.Stopped) {
		    startNestedComponent(provider, monitor, true);
		} else {
		    getLogger().error("Not starting search provider due to invalid state.");
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Error starting search provider.", e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (ISearchProvider provider : getSearchProviders()) {
	    try {
		if (provider.getLifecycleStatus() == LifecycleStatus.Started) {
		    stopNestedComponent(provider, monitor);
		} else {
		    getLogger().error("Not stopping search provider due to invalid state.");
		}
	    } catch (SiteWhereException e) {
		getLogger().error("Error stopping search provider.", e);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.search.external.ISearchProviderManager#
     * getSearchProviders()
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
     * @see com.sitewhere.spi.search.external.ISearchProviderManager#
     * getSearchProvider(java .lang.String)
     */
    @Override
    public ISearchProvider getSearchProvider(String id) {
	return providersById.get(id);
    }

    protected Map<String, ISearchProvider> getProvidersById() {
	return providersById;
    }

    protected void setProvidersById(Map<String, ISearchProvider> providersById) {
	this.providersById = providersById;
    }
}