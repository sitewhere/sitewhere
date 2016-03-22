/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.tenant;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;

/**
 * Default implementation of {@link ITenantSearchCriteria} used by REST services.
 * 
 * @author Derek
 */
public class TenantSearchCriteria extends SearchCriteria implements ITenantSearchCriteria {

	/** User id */
	private String userId;

	/** Include tenant runtime information */
	private boolean includeRuntimeInfo;

	public TenantSearchCriteria(int pageNumber, int pageSize) {
		super(pageNumber, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.search.user.ITenantSearchCriteria#getUserId()
	 */
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.search.user.ITenantSearchCriteria#isIncludeRuntimeInfo()
	 */
	public boolean isIncludeRuntimeInfo() {
		return includeRuntimeInfo;
	}

	public void setIncludeRuntimeInfo(boolean includeRuntimeInfo) {
		this.includeRuntimeInfo = includeRuntimeInfo;
	}
}