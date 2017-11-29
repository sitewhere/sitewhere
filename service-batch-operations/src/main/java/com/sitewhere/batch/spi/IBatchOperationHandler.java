/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.device.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Handler registered for a type of batch operation.
 * 
 * @author Derek
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
	    IBatchElementUpdateRequest request) throws SiteWhereException;
}