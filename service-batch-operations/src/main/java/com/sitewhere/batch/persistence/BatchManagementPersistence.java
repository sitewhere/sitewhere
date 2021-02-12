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
package com.sitewhere.batch.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.batch.BatchOperationTypes;
import com.sitewhere.microservice.persistence.Persistence;
import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.rest.model.batch.request.BatchOperationCreateRequest;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.device.IDevice;

/**
 * Common methods needed by batch management implementations.
 */
public class BatchManagementPersistence extends Persistence {

    /**
     * Common logic for creating a batch operation based on an incoming request.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public static BatchOperation batchOperationCreateLogic(IBatchOperationCreateRequest request)
	    throws SiteWhereException {
	BatchOperation operation = new BatchOperation();
	Persistence.entityCreateLogic(request, operation);

	operation.setOperationType(request.getOperationType());
	operation.getParameters().putAll(request.getParameters());

	return operation;
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
	Persistence.entityUpdateLogic(request, target);

	if (request.getProcessingStatus() != null) {
	    target.setProcessingStatus(request.getProcessingStatus());
	}
	if (request.getProcessingStartedDate() != null) {
	    target.setProcessingStartedDate(request.getProcessingStartedDate());
	}
	if (request.getProcessingEndedDate() != null) {
	    target.setProcessingEndedDate(request.getProcessingEndedDate());
	}
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
    public static BatchElement batchElementCreateLogic(IBatchOperation operation, IDevice device)
	    throws SiteWhereException {
	BatchElement element = new BatchElement();
	element.setId(UUID.randomUUID());
	element.setBatchOperationId(operation.getId());
	element.setDeviceId(device.getId());
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
    public static void batchElementUpdateLogic(IBatchElementCreateRequest request, BatchElement element)
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
     * @return
     * @throws SiteWhereException
     */
    public static IBatchOperationCreateRequest batchCommandInvocationCreateLogic(IBatchCommandInvocationRequest request)
	    throws SiteWhereException {
	BatchOperationCreateRequest batch = new BatchOperationCreateRequest();
	batch.setToken(request.getToken());
	batch.setOperationType(BatchOperationTypes.OPERATION_BATCH_COMMAND_INVOCATION);
	batch.setDeviceTokens(request.getDeviceTokens());
	batch.getParameters().put(IBatchCommandInvocationRequest.PARAM_COMMAND_TOKEN, request.getCommandToken());
	Map<String, String> params = new HashMap<String, String>();
	for (String key : request.getParameterValues().keySet()) {
	    params.put(key, request.getParameterValues().get(key));
	}
	batch.setMetadata(params);
	return batch;
    }
}