/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search;

import java.util.List;

import com.sitewhere.spi.search.ISearchResults;

/**
 * Used to return search result metadata in response to AJAX calls.
 * 
 * @author dadams
 */
public class SearchResults<T> implements ISearchResults<T> {

    /** Number of total results */
    private long numResults;

    /** List of results */
    private List<T> results;

    public SearchResults(List<T> all) {
	setNumResults(all.size());
	setResults(all);
    }

    public SearchResults(List<T> page, long total) {
	setNumResults(total);
	setResults(page);
    }

    public long getNumResults() {
	return numResults;
    }

    public void setNumResults(long numResults) {
	this.numResults = numResults;
    }

    public List<T> getResults() {
	return results;
    }

    public void setResults(List<T> results) {
	this.results = results;
    }
}