/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch;

import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;

public class BatchManagementTriggers extends BatchManagementDecorator {

    public BatchManagementTriggers(IBatchManagement delegate) {
	super(delegate);
    }

    /*
     * @see
     * com.sitewhere.batch.BatchManagementDecorator#createBatchCommandInvocation(com
     * .sitewhere.spi.batch.request.IBatchCommandInvocationRequest)
     */
    @Override
    public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
	    throws SiteWhereException {
	IBatchOperation result = super.createBatchCommandInvocation(request);
	getBatchOperationManager().process(result);
	return result;
    }

    protected IBatchOperationManager getBatchOperationManager() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getBatchOperationManager();
    }
}