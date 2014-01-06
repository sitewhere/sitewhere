/*
 * Pager.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.common;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Encapsulates paging functionality.
 * 
 * @author Derek
 */
public class Pager<T> {

	/** Result list */
	private List<T> results = new ArrayList<T>();

	/** Search criteria */
	private ISearchCriteria criteria;

	/** Number of records left to skip */
	private long toSkip;

	/** Number of records matched */
	private long matched;

	/** Total records matched */
	private long total;

	public Pager(ISearchCriteria criteria) {
		this.criteria = criteria;
		if (criteria.getPageNumber() >= 1) {
			this.toSkip = ((criteria.getPageNumber() - 1) * criteria.getPageSize());
		} else {
			this.toSkip = 0;
		}
		this.total = 0;
	}

	/**
	 * Process a record. Return false if no more processing is needed (page of records has
	 * been found).
	 * 
	 * @param record
	 */
	public void process(T record) {
		total++;
		if (toSkip > 0) {
			toSkip--;
		} else if (matched < criteria.getPageSize()) {
			results.add(record);
			matched++;
		}
	}

	public ISearchCriteria getSearchCriteria() {
		return criteria;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}
	
	public long getTotal() {
		return total;
	}
}