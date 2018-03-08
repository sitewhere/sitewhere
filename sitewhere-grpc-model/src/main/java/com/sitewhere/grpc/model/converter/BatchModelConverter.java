/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.grpc.model.BatchModel.GBatchCommandInvocationCreateRequest;
import com.sitewhere.grpc.model.BatchModel.GBatchOperation;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationCreateRequest;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationElement;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationElementSearchCriteria;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationElementSearchResults;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationElementUpdateRequest;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationSearchCriteria;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationSearchResults;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationStatus;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationUpdateRequest;
import com.sitewhere.grpc.model.BatchModel.GElementProcessingStatus;
import com.sitewhere.grpc.model.CommonModel.GOptionalBoolean;
import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.rest.model.batch.request.BatchCommandInvocationRequest;
import com.sitewhere.rest.model.batch.request.BatchElementUpdateRequest;
import com.sitewhere.rest.model.batch.request.BatchOperationCreateRequest;
import com.sitewhere.rest.model.batch.request.BatchOperationUpdateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.batch.BatchOperationSearchCriteria;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;

/**
 * Convert batch entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class BatchModelConverter {

    /**
     * Convert batch operation status from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static BatchOperationStatus asApiBatchOperationStatus(GBatchOperationStatus grpc) throws SiteWhereException {
	switch (grpc) {
	case BATCH_OPERATION_STATUS_UNPROCESSED:
	    return BatchOperationStatus.Unprocessed;
	case BATCH_OPERATION_STATUS_PROCESSING:
	    return BatchOperationStatus.Processing;
	case BATCH_OPERATION_STATUS_FINISHED_WITH_ERRORS:
	    return BatchOperationStatus.FinishedWithErrors;
	case BATCH_OPERATION_STATUS_FINISHED_SUCCESSFULLY:
	    return BatchOperationStatus.FinishedSuccessfully;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown batch operation status: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert batch operation status from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GBatchOperationStatus asGrpcBatchOperationStatus(BatchOperationStatus api) throws SiteWhereException {
	switch (api) {
	case Unprocessed:
	    return GBatchOperationStatus.BATCH_OPERATION_STATUS_UNPROCESSED;
	case Processing:
	    return GBatchOperationStatus.BATCH_OPERATION_STATUS_PROCESSING;
	case FinishedWithErrors:
	    return GBatchOperationStatus.BATCH_OPERATION_STATUS_FINISHED_WITH_ERRORS;
	case FinishedSuccessfully:
	    return GBatchOperationStatus.BATCH_OPERATION_STATUS_FINISHED_SUCCESSFULLY;
	}
	throw new SiteWhereException("Unknown batch operation status: " + api.name());
    }

    /**
     * Convert batch operation create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static BatchOperationCreateRequest asApiBatchOperationCreateRequest(GBatchOperationCreateRequest grpc)
	    throws SiteWhereException {
	BatchOperationCreateRequest api = new BatchOperationCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setOperationType(grpc.getOperationType());
	api.setParameters(grpc.getParametersMap());
	api.setDeviceTokens(grpc.getDeviceTokensList());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert batch operation create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GBatchOperationCreateRequest asGrpcBatchOperationCreateRequest(IBatchOperationCreateRequest api)
	    throws SiteWhereException {
	GBatchOperationCreateRequest.Builder grpc = GBatchOperationCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setOperationType(api.getOperationType());
	grpc.putAllParameters(api.getParameters());
	grpc.addAllDeviceTokens(api.getDeviceTokens());
	grpc.putAllMetadata(api.getMetadata());
	return grpc.build();
    }

    /**
     * Convert batch command invocation create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static BatchCommandInvocationRequest asApiBatchCommandInvocationRequest(
	    GBatchCommandInvocationCreateRequest grpc) throws SiteWhereException {
	BatchCommandInvocationRequest api = new BatchCommandInvocationRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setCommandToken(grpc.getCommandToken());
	api.setDeviceTokens(grpc.getDeviceTokensList());
	api.setParameterValues(grpc.getParametersMap());
	return api;
    }

    /**
     * Convert batch command invocation create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GBatchCommandInvocationCreateRequest asGrpcBatchCommandInvocationRequest(
	    IBatchCommandInvocationRequest api) throws SiteWhereException {
	GBatchCommandInvocationCreateRequest.Builder grpc = GBatchCommandInvocationCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setCommandToken(api.getCommandToken());
	grpc.addAllDeviceTokens(api.getDeviceTokens());
	grpc.putAllParameters(api.getParameterValues());
	return grpc.build();
    }

    /**
     * Convert batch operation update request from GRPC to API.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static BatchOperationUpdateRequest asApiBatchOperationUpdateRequest(GBatchOperationUpdateRequest grpc)
	    throws SiteWhereException {
	BatchOperationUpdateRequest api = new BatchOperationUpdateRequest();
	api.setProcessingStatus(BatchModelConverter.asApiBatchOperationStatus(grpc.getProcessingStatus()));
	api.setProcessingStartedDate(
		grpc.hasProcessingStartedDate() ? CommonModelConverter.asDate(grpc.getProcessingStartedDate()) : null);
	api.setProcessingEndedDate(
		grpc.hasProcessingEndedDate() ? CommonModelConverter.asDate(grpc.getProcessingEndedDate()) : null);
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert batch operation update request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GBatchOperationUpdateRequest asGrpcBatchOperationUpdateRequest(IBatchOperationUpdateRequest api)
	    throws SiteWhereException {
	GBatchOperationUpdateRequest.Builder grpc = GBatchOperationUpdateRequest.newBuilder();
	grpc.setProcessingStatus(BatchModelConverter.asGrpcBatchOperationStatus(api.getProcessingStatus()));
	if (api.getProcessingStartedDate() != null) {
	    grpc.setProcessingStartedDate(CommonModelConverter.asGrpcTimestamp(api.getProcessingStartedDate()));
	}
	if (api.getProcessingEndedDate() != null) {
	    grpc.setProcessingEndedDate(CommonModelConverter.asGrpcTimestamp(api.getProcessingEndedDate()));
	}
	grpc.putAllMetadata(api.getMetadata());
	return grpc.build();
    }

    /**
     * Convert batch operation search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static BatchOperationSearchCriteria asApiBatchOperationSearchCriteria(GBatchOperationSearchCriteria grpc)
	    throws SiteWhereException {
	BatchOperationSearchCriteria api = new BatchOperationSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	if (grpc.hasIncludeDeleted()) {
	    api.setIncludeDeleted(grpc.getIncludeDeleted().getValue());
	}
	return api;
    }

    /**
     * Convert batch operation search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GBatchOperationSearchCriteria asGrpcBatchOperationSearchCriteria(IBatchOperationSearchCriteria api)
	    throws SiteWhereException {
	GBatchOperationSearchCriteria.Builder grpc = GBatchOperationSearchCriteria.newBuilder();
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	grpc.setIncludeDeleted(GOptionalBoolean.newBuilder().setValue(api.isIncludeDeleted()).build());
	return grpc.build();
    }

    /**
     * Convert batch operation search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IBatchOperation> asApiBatchOperationSearchResults(
	    GBatchOperationSearchResults response) throws SiteWhereException {
	List<IBatchOperation> results = new ArrayList<IBatchOperation>();
	for (GBatchOperation grpc : response.getBatchOperationsList()) {
	    results.add(BatchModelConverter.asApiBatchOperation(grpc));
	}
	return new SearchResults<IBatchOperation>(results, response.getCount());
    }

    /**
     * Convert batch operation from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static BatchOperation asApiBatchOperation(GBatchOperation grpc) throws SiteWhereException {
	BatchOperation api = new BatchOperation();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setToken(grpc.getToken());
	api.setOperationType(grpc.getOperationType());
	api.setParameters(grpc.getParametersMap());
	api.setMetadata(grpc.getMetadataMap());
	api.setProcessingStatus(BatchModelConverter.asApiBatchOperationStatus(grpc.getProcessingStatus()));
	api.setProcessingStartedDate(
		grpc.hasProcessingStartedDate() ? CommonModelConverter.asDate(grpc.getProcessingStartedDate()) : null);
	api.setProcessingEndedDate(
		grpc.hasProcessingEndedDate() ? CommonModelConverter.asDate(grpc.getProcessingEndedDate()) : null);
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert batch operation from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GBatchOperation asGrpcBatchOperation(IBatchOperation api) throws SiteWhereException {
	GBatchOperation.Builder grpc = GBatchOperation.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setToken(api.getToken());
	grpc.setOperationType(api.getOperationType());
	grpc.putAllParameters(api.getParameters());
	grpc.putAllMetadata(api.getMetadata());
	grpc.setProcessingStatus(BatchModelConverter.asGrpcBatchOperationStatus(api.getProcessingStatus()));
	if (api.getProcessingStartedDate() != null) {
	    grpc.setProcessingStartedDate(CommonModelConverter.asGrpcTimestamp(api.getProcessingStartedDate()));
	}
	if (api.getProcessingEndedDate() != null) {
	    grpc.setProcessingEndedDate(CommonModelConverter.asGrpcTimestamp(api.getProcessingEndedDate()));
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert batch element processing status from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ElementProcessingStatus asApiElementProcessingStatus(GElementProcessingStatus grpc)
	    throws SiteWhereException {
	if (grpc == null) {
	    return null;
	}
	switch (grpc) {
	case BATCH_ELEMENT_STATUS_UNPROCESSED:
	    return ElementProcessingStatus.Unprocessed;
	case BATCH_ELEMENT_STATUS_PROCESSING:
	    return ElementProcessingStatus.Processing;
	case BATCH_ELEMENT_STATUS_FAILED:
	    return ElementProcessingStatus.Failed;
	case BATCH_ELEMENT_STATUS_SUCCEEDED:
	    return ElementProcessingStatus.Succeeded;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown element processing status: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert batch element processing status from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GElementProcessingStatus asGrpcElementProcessingStatus(ElementProcessingStatus api)
	    throws SiteWhereException {
	if (api == null) {
	    return null;
	}
	switch (api) {
	case Unprocessed:
	    return GElementProcessingStatus.BATCH_ELEMENT_STATUS_UNPROCESSED;
	case Processing:
	    return GElementProcessingStatus.BATCH_ELEMENT_STATUS_PROCESSING;
	case Failed:
	    return GElementProcessingStatus.BATCH_ELEMENT_STATUS_FAILED;
	case Succeeded:
	    return GElementProcessingStatus.BATCH_ELEMENT_STATUS_SUCCEEDED;
	}
	throw new SiteWhereException("Unknown element processing status: " + api.name());
    }

    /**
     * Convert batch element update request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static BatchElementUpdateRequest asApiBatchElementUpdateRequest(GBatchOperationElementUpdateRequest grpc)
	    throws SiteWhereException {
	BatchElementUpdateRequest api = new BatchElementUpdateRequest();
	api.setProcessingStatus(BatchModelConverter.asApiElementProcessingStatus(grpc.getProcessingStatus()));
	api.setProcessedDate(grpc.hasProcessedDate() ? CommonModelConverter.asDate(grpc.getProcessedDate()) : null);
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert batch element update request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GBatchOperationElementUpdateRequest asGrpcBatchElementUpdateRequest(IBatchElementUpdateRequest api)
	    throws SiteWhereException {
	GBatchOperationElementUpdateRequest.Builder grpc = GBatchOperationElementUpdateRequest.newBuilder();
	grpc.setProcessingStatus(BatchModelConverter.asGrpcElementProcessingStatus(api.getProcessingStatus()));
	if (api.getProcessedDate() != null) {
	    grpc.setProcessedDate(CommonModelConverter.asGrpcTimestamp(api.getProcessedDate()));
	}
	grpc.putAllMetadata(api.getMetadata());
	return grpc.build();
    }

    /**
     * Convert batch element search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static BatchElementSearchCriteria asApiBatchElementSearchCriteria(GBatchOperationElementSearchCriteria grpc)
	    throws SiteWhereException {
	BatchElementSearchCriteria api = new BatchElementSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	api.setProcessingStatus(BatchModelConverter.asApiElementProcessingStatus(grpc.getProcessingStatus()));
	return api;
    }

    /**
     * Convert batch element search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GBatchOperationElementSearchCriteria asGrpcBatchElementSearchCriteria(IBatchElementSearchCriteria api)
	    throws SiteWhereException {
	GBatchOperationElementSearchCriteria.Builder grpc = GBatchOperationElementSearchCriteria.newBuilder();
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	if (api.getProcessingStatus() != null) {
	    grpc.setProcessingStatus(BatchModelConverter.asGrpcElementProcessingStatus(api.getProcessingStatus()));
	}
	return grpc.build();
    }

    /**
     * Convert batch operation search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IBatchElement> asApiBatchElementSearchResults(
	    GBatchOperationElementSearchResults response) throws SiteWhereException {
	List<IBatchElement> results = new ArrayList<IBatchElement>();
	for (GBatchOperationElement grpc : response.getBatchOperationElementsList()) {
	    results.add(BatchModelConverter.asApiBatchElement(grpc));
	}
	return new SearchResults<IBatchElement>(results, response.getCount());
    }

    /**
     * Convert batch element from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static BatchElement asApiBatchElement(GBatchOperationElement grpc) throws SiteWhereException {
	BatchElement api = new BatchElement();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setBatchOperationId(CommonModelConverter.asApiUuid(grpc.getBatchOperationId()));
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setProcessingStatus(BatchModelConverter.asApiElementProcessingStatus(grpc.getProcessingStatus()));
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert batch element from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static GBatchOperationElement asGrpcBatchElement(IBatchElement api) throws SiteWhereException {
	GBatchOperationElement.Builder grpc = GBatchOperationElement.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setBatchOperationId(CommonModelConverter.asGrpcUuid(api.getBatchOperationId()));
	grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	if (api.getProcessingStatus() != null) {
	    grpc.setProcessingStatus(BatchModelConverter.asGrpcElementProcessingStatus(api.getProcessingStatus()));
	}
	grpc.putAllMetadata(api.getMetadata());
	return grpc.build();
    }
}
