/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search;

import java.util.List;

/**
 * Base interface for results returned from paged queries.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public interface ISearchResults<T> {

    /**
     * Get the total number of results.
     * 
     * @return
     */
    public long getNumResults();

    /**
     * Get the results.
     * 
     * @return
     */
    public List<T> getResults();
}