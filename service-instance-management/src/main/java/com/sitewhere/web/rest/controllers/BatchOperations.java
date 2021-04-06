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
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.batch.BatchElementMarshalHelper;
import com.sitewhere.microservice.api.batch.BatchOperationMarshalHelper;
import com.sitewhere.microservice.api.batch.BatchUtils;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.microservice.api.schedule.ScheduledJobHelper;
import com.sitewhere.rest.model.batch.MarshaledBatchOperation;
import com.sitewhere.rest.model.batch.request.BatchCommandInvocationRequest;
import com.sitewhere.rest.model.batch.request.InvocationByAssignmentCriteriaRequest;
import com.sitewhere.rest.model.batch.request.InvocationByDeviceCriteriaRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.batch.BatchOperationSearchCriteria;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for batch operations.
 */
@RestController
@RequestMapping("/api/batch")
public class BatchOperations {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(BatchOperations.class);

    /**
     * Get batch operation by token.
     * 
     * @param batchToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{batchToken}")
    public MarshaledBatchOperation getBatchOperationByToken(@PathVariable String batchToken) throws SiteWhereException {
	IBatchOperation batch = getBatchManagement().getBatchOperationByToken(batchToken);
	if (batch == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	return helper.convert(batch);
    }

    /**
     * List batch operations that match the given criteria.
     * 
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public SearchResults<IBatchOperation> listBatchOperations(
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	BatchOperationSearchCriteria criteria = new BatchOperationSearchCriteria(page, pageSize);

	ISearchResults<? extends IBatchOperation> results = getBatchManagement().listBatchOperations(criteria);
	BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	List<IBatchOperation> converted = new ArrayList<IBatchOperation>();
	for (IBatchOperation op : results.getResults()) {
	    converted.add(helper.convert(op));
	}
	return new SearchResults<IBatchOperation>(converted, results.getNumResults());
    }

    /**
     * List batch operation elements that match criteria.
     * 
     * @param operationToken
     * @param includeDevice
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{operationToken}/elements")
    public SearchResults<IBatchElement> listBatchOperationElements(@PathVariable String operationToken,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDevice,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	IBatchOperation batchOperation = assureBatchOperation(operationToken);
	BatchElementSearchCriteria criteria = new BatchElementSearchCriteria(page, pageSize);
	ISearchResults<? extends IBatchElement> results = getBatchManagement().listBatchElements(batchOperation.getId(),
		criteria);

	BatchElementMarshalHelper helper = new BatchElementMarshalHelper();
	helper.setIncludeDevice(includeDevice);
	List<IBatchElement> converted = new ArrayList<IBatchElement>();
	for (IBatchElement element : results.getResults()) {
	    converted.add(helper.convert(element, getDeviceManagement()));
	}
	return new SearchResults<IBatchElement>(converted, results.getNumResults());
    }

    /**
     * Create a batch command invocation.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/command")
    public MarshaledBatchOperation createBatchCommandInvocation(@RequestBody BatchCommandInvocationRequest request)
	    throws SiteWhereException {
	IBatchOperation result = getBatchManagement().createBatchCommandInvocation(request);
	BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	return helper.convert(result);
    }

    /**
     * Create a batch operation that invokes a command for all devices that match
     * the given criteria.
     * 
     * @param request
     * @param scheduleToken
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/command/criteria/device")
    public ResponseEntity<?> createInvocationsByDeviceCriteria(@RequestBody InvocationByDeviceCriteriaRequest request,
	    @RequestParam(required = false) String scheduleToken) throws SiteWhereException {
	if (scheduleToken != null) {
	    IScheduledJobCreateRequest job = ScheduledJobHelper
		    .createBatchCommandInvocationJobForDeviceCriteria(request, scheduleToken);
	    return ResponseEntity.ok(getScheduleManagement().createScheduledJob(job));
	} else {
	    // Resolve tokens for devices matching criteria.
	    List<String> deviceTokens = BatchUtils.resolveDeviceTokensForDeviceCriteria(request, getDeviceManagement(),
		    getAssetManagement());

	    // Create batch command invocation.
	    BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
	    invoke.setToken(request.getToken());
	    invoke.setCommandToken(request.getCommandToken());
	    invoke.setParameterValues(request.getParameterValues());
	    invoke.setDeviceTokens(deviceTokens);

	    IBatchOperation result = getBatchManagement().createBatchCommandInvocation(invoke);
	    BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	    return ResponseEntity.ok(helper.convert(result));
	}
    }

    /**
     * Create batch command invocation based on device assignment criteria.
     * 
     * @param request
     * @param scheduleToken
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/command/criteria/assignment")
    public ResponseEntity<?> createInvocationsByAssignmentCriteria(
	    @RequestBody InvocationByAssignmentCriteriaRequest request,
	    @RequestParam(required = false) String scheduleToken) throws SiteWhereException {
	if (scheduleToken != null) {
	    IScheduledJobCreateRequest job = ScheduledJobHelper
		    .createBatchCommandInvocationJobForAssignmentCriteria(request, scheduleToken);
	    return ResponseEntity.ok(getScheduleManagement().createScheduledJob(job));
	} else {
	    // Resolve tokens for devices matching criteria.
	    List<String> deviceTokens = BatchUtils.resolveDeviceTokensForAssignmentCriteria(request,
		    getDeviceManagement(), getAssetManagement());

	    // Create batch command invocation.
	    BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
	    invoke.setToken(request.getToken());
	    invoke.setCommandToken(request.getCommandToken());
	    invoke.setParameterValues(request.getParameterValues());
	    invoke.setDeviceTokens(deviceTokens);

	    IBatchOperation result = getBatchManagement().createBatchCommandInvocation(invoke);
	    BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	    return ResponseEntity.ok(helper.convert(result));
	}
    }

    /**
     * Get a device command by unique id. Throw an exception if not found.
     * 
     * @param deviceCommandId
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceCommand assureDeviceCommand(UUID deviceCommandId) throws SiteWhereException {
	IDeviceCommand command = getDeviceManagement().getDeviceCommand(deviceCommandId);
	if (command == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
	}
	return command;
    }

    /**
     * Verify that the batch operation associated with the given token exists.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IBatchOperation assureBatchOperation(String token) throws SiteWhereException {
	IBatchOperation batchOperation = getBatchManagement().getBatchOperationByToken(token);
	if (batchOperation == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	return batchOperation;
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    private IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected IBatchManagement getBatchManagement() {
	return getMicroservice().getBatchManagementApiChannel();
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}