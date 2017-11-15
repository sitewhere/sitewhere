/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search.spi;

import java.util.List;

import com.sitewhere.spi.search.ISearchProvider;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages a list of search providers that can be used by SiteWhere.
 * 
 * @author Derek
 */
public interface ISearchProviderManager extends ILifecycleComponent {

    /**
     * Get list of available search providers.
     * 
     * @return
     */
    public List<ISearchProvider> getSearchProviders();

    /**
     * Get search provider with the given unique id.
     * 
     * @param id
     * @return
     */
    public ISearchProvider getSearchProvider(String id);
}