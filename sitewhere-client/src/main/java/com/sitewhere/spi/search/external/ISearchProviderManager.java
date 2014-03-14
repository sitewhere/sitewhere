/*
 * ISearchProviderManager.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.external;

import java.util.List;

import com.sitewhere.spi.ISiteWhereLifecycle;

/**
 * Manages a list of search providers that can be used by SiteWhere.
 * 
 * @author Derek
 */
public interface ISearchProviderManager extends ISiteWhereLifecycle {

	/**
	 * Get list of available search providers.
	 * 
	 * @return
	 */
	public List<ISearchProvider> getSearchProviders();

	/**
	 * Get the default search provider.
	 * 
	 * @return
	 */
	public ISearchProvider getDefaultSearchProvider();
}