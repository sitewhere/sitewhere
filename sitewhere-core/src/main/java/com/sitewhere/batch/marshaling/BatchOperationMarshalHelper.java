/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.marshaling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.rest.model.batch.MarshaledBatchOperation;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchOperation;

/**
 * Configurable helper class that allows {@link BatchOperation} model objects to
 * be created from {@link IBatchOperation} SPI objects.
 */
public class BatchOperationMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LoggerFactory.getLogger(BatchOperationMarshalHelper.class);

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledBatchOperation convert(IBatchOperation source) throws SiteWhereException {
	if (source == null) {
	    return null;
	}
	MarshaledBatchOperation operation = new MarshaledBatchOperation();
	operation.setId(source.getId());
	operation.setToken(source.getToken());
	operation.setOperationType(source.getOperationType());
	operation.setParameters(source.getParameters());
	operation.setProcessingStatus(source.getProcessingStatus());
	operation.setProcessingStartedDate(source.getProcessingStartedDate());
	operation.setProcessingEndedDate(source.getProcessingEndedDate());

	PersistentEntity.copy(source, operation);
	return operation;
    }
}