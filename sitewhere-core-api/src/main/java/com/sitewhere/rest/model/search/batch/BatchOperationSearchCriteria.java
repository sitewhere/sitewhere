/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.batch;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;

/**
 * Model object for batch operation search criteria.
 * 
 * @author Derek
 */
public class BatchOperationSearchCriteria extends SearchCriteria implements IBatchOperationSearchCriteria {

    /** Flag for whether to include deleted operations */
    private boolean includeDeleted;

    public BatchOperationSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    /*
     * @see
     * com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria#isIncludeDeleted
     * ()
     */
    @Override
    public boolean isIncludeDeleted() {
	return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
	this.includeDeleted = includeDeleted;
    }
}