/*
 * ISearchProvider.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.external;

import com.sitewhere.spi.ISiteWhereLifecycle;

/**
 * Implemented by external search providers that index SiteWhere data.
 * 
 * @author Derek
 */
public interface ISearchProvider extends ISiteWhereLifecycle {

	/**
	 * Get a human-readable name for the search provider.
	 * 
	 * @return
	 */
	public String getName();
}