/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.batch;

import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.microservice.api.batch.BatchManagementDecorator;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;

/**
 * Attaches logic to batch management API invocations.
 */
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
	getBatchOperationManager().addUnprocessedBatchOperation(result, request.getDeviceTokens());
	return result;
    }

    protected IBatchOperationManager getBatchOperationManager() {
	return ((IBatchOperationsTenantEngine) getTenantEngine()).getBatchOperationManager();
    }
}