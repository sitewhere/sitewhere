/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search;

import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Common criteria used in searches that return a list of results. Includes parameters for
 * paging of results.
 * 
 * @author Derek
 */
public class SearchCriteria implements ISearchCriteria {

	/** Page number to view */
	private Integer pageNumber;

	/** Number of records in a page of results */
	private Integer pageSize;

	public SearchCriteria(int pageNumber, int pageSize) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}