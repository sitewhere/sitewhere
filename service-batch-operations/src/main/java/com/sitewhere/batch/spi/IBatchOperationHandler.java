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
package com.sitewhere.batch.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchElementCreateRequest;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Handler registered for a type of batch operation.
 */
public interface IBatchOperationHandler extends ITenantEngineLifecycleComponent {

    /**
     * Get type of operation handled.
     * 
     * @return
     */
    public String getOperationType();

    /**
     * Process a single batch element.
     * 
     * @param operation
     * @param element
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ElementProcessingStatus process(IBatchOperation operation, IBatchElement element,
	    IBatchElementCreateRequest request) throws SiteWhereException;
}