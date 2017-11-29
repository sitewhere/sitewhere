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