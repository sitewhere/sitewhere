/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.batch.BatchOperationTypes;
import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.rest.model.batch.request.BatchOperationCreateRequest;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;

/**
 * Common methods needed by batch management implementations.
 * 
 * @author Derek
 */
public class BatchManagementPersistence extends Persistence {

    /**
     * Common logic for creating a batch operation based on an incoming request.
     * 
     * @param source
     * @param uuid
     * @return
     * @throws SiteWhereException
     */
    public static BatchOperation batchOperationCreateLogic(IBatchOperationCreateRequest source, String uuid)
	    throws SiteWhereException {
	BatchOperation batch = new BatchOperation();
	batch.setToken(uuid);
	batch.setOperationType(source.getOperationType());
	batch.getParameters().putAll(source.getParameters());

	Persistence.initializeEntityMetadata(batch);
	MetadataProvider.copy(source.getMetadata(), batch);
	return batch;
    }

    /**
     * Common logic for updating batch operation information.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void batchOperationUpdateLogic(IBatchOperationUpdateRequest request, BatchOperation target)
	    throws SiteWhereException {
	if (request.getProcessingStatus() != null) {
	    target.setProcessingStatus(request.getProcessingStatus());
	}
	if (request.getProcessingStartedDate() != null) {
	    target.setProcessingStartedDate(request.getProcessingStartedDate());
	}
	if (request.getProcessingEndedDate() != null) {
	    target.setProcessingEndedDate(request.getProcessingEndedDate());
	}

	if (request.getMetadata() != null) {
	    target.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), target);
	}
	Persistence.setUpdatedEntityMetadata(target);
    }

    /**
     * Common logic for creating a batch operation element.
     * 
     * @param batchOperationToken
     * @param hardwareId
     * @param index
     * @return
     * @throws SiteWhereException
     */
    public static BatchElement batchElementCreateLogic(String batchOperationToken, String hardwareId, long index)
	    throws SiteWhereException {
	BatchElement element = new BatchElement();
	element.setBatchOperationToken(batchOperationToken);
	element.setHardwareId(hardwareId);
	element.setIndex(index);
	element.setProcessingStatus(ElementProcessingStatus.Unprocessed);
	element.setProcessedDate(null);
	return element;
    }

    /**
     * Common logic for updating a batch operation element.
     * 
     * @param request
     * @param element
     * @throws SiteWhereException
     */
    public static void batchElementUpdateLogic(IBatchElementUpdateRequest request, BatchElement element)
	    throws SiteWhereException {
	if (request.getProcessingStatus() != null) {
	    element.setProcessingStatus(request.getProcessingStatus());
	}
	if (request.getProcessedDate() != null) {
	    element.setProcessedDate(request.getProcessedDate());
	}
	if (request.getMetadata() != null) {
	    element.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), element);
	}
    }

    /**
     * Encodes batch command invocation parameters into the generic
     * {@link IBatchOperationCreateRequest} format.
     * 
     * @param request
     * @param uuid
     * @return
     * @throws SiteWhereException
     */
    public static IBatchOperationCreateRequest batchCommandInvocationCreateLogic(IBatchCommandInvocationRequest request,
	    String uuid) throws SiteWhereException {
	BatchOperationCreateRequest batch = new BatchOperationCreateRequest();
	batch.setToken(uuid);
	batch.setOperationType(BatchOperationTypes.OPERATION_BATCH_COMMAND_INVOCATION);
	batch.setHardwareIds(request.getHardwareIds());
	batch.getParameters().put(IBatchCommandInvocationRequest.PARAM_COMMAND_TOKEN, request.getCommandToken());
	Map<String, String> params = new HashMap<String, String>();
	for (String key : request.getParameterValues().keySet()) {
	    params.put(key, request.getParameterValues().get(key));
	}
	batch.setMetadata(params);
	return batch;
    }
}