/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.batch.BatchUtils;
import com.sitewhere.batch.marshaling.BatchElementMarshalHelper;
import com.sitewhere.batch.marshaling.BatchOperationMarshalHelper;
import com.sitewhere.rest.model.batch.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.batch.request.BatchCommandInvocationRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.batch.BatchOperationSearchCriteria;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.schedule.ScheduledJobHelper;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for batch operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/batch")
@Api(value = "batch")
public class BatchOperations extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(BatchOperations.class);

    @RequestMapping(value = "/{batchToken}", method = RequestMethod.GET)
    @ApiOperation(value = "Get batch operation by unique token")
    @Secured({ SiteWhereRoles.REST })
    public IBatchOperation getBatchOperationByToken(
	    @ApiParam(value = "Unique token that identifies batch operation", required = true) @PathVariable String batchToken,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IBatchOperation batch = getBatchManagement().getBatchOperationByToken(batchToken);
	if (batch == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	return helper.convert(batch);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List batch operations")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IBatchOperation> listBatchOperations(
	    @ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	BatchOperationSearchCriteria criteria = new BatchOperationSearchCriteria(page, pageSize);
	criteria.setIncludeDeleted(includeDeleted);

	ISearchResults<IBatchOperation> results = getBatchManagement().listBatchOperations(criteria);
	BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	List<IBatchOperation> converted = new ArrayList<IBatchOperation>();
	for (IBatchOperation op : results.getResults()) {
	    converted.add(helper.convert(op));
	}
	return new SearchResults<IBatchOperation>(converted, results.getNumResults());
    }

    @RequestMapping(value = "/{operationToken}/elements", method = RequestMethod.GET)
    @ApiOperation(value = "List batch operation elements")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IBatchElement> listBatchOperationElements(
	    @ApiParam(value = "Unique batch operation token", required = true) @PathVariable String operationToken,
	    @ApiParam(value = "Include device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IBatchOperation batchOperation = assureBatchOperation(operationToken);
	BatchElementSearchCriteria criteria = new BatchElementSearchCriteria(page, pageSize);
	ISearchResults<IBatchElement> results = getBatchManagement().listBatchElements(batchOperation.getId(),
		criteria);

	BatchElementMarshalHelper helper = new BatchElementMarshalHelper();
	helper.setIncludeDevice(includeDevice);
	List<IBatchElement> converted = new ArrayList<IBatchElement>();
	for (IBatchElement element : results.getResults()) {
	    converted.add(helper.convert(element, getDeviceManagement()));
	}
	return new SearchResults<IBatchElement>(converted, results.getNumResults());
    }

    @RequestMapping(value = "/command", method = RequestMethod.POST)
    @ApiOperation(value = "Create new batch command invocation")
    @Secured({ SiteWhereRoles.REST })
    public IBatchOperation createBatchCommandInvocation(@RequestBody BatchCommandInvocationRequest request,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IBatchOperation result = getBatchManagement().createBatchCommandInvocation(request);
	BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	return helper.convert(result);
    }

    /**
     * Create a batch operation that invokes a command for all devices that match
     * the given criteria.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/command/criteria", method = RequestMethod.POST)
    @ApiOperation(value = "Create batch command operation based on criteria")
    @Secured({ SiteWhereRoles.REST })
    public Object createBatchCommandByCriteria(@RequestBody BatchCommandForCriteriaRequest request,
	    @ApiParam(value = "Schedule token", required = false) @RequestParam(required = false) String scheduleToken)
	    throws SiteWhereException {
	if (scheduleToken != null) {
	    IScheduledJobCreateRequest job = ScheduledJobHelper
		    .createBatchCommandInvocationJobByCriteria(UUID.randomUUID().toString(), request, scheduleToken);
	    return getScheduleManagement().createScheduledJob(job);
	} else {
	    // Resolve tokens for devices matching criteria.
	    List<String> deviceTokens = BatchUtils.resolveDeviceTokensForCriteria(request, getDeviceManagement(),
		    getAssetManagement());

	    // Create batch command invocation.
	    BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
	    invoke.setToken(request.getToken());
	    invoke.setCommandToken(request.getCommandToken());
	    invoke.setParameterValues(request.getParameterValues());
	    invoke.setDeviceTokens(deviceTokens);

	    IBatchOperation result = getBatchManagement().createBatchCommandInvocation(invoke);
	    BatchOperationMarshalHelper helper = new BatchOperationMarshalHelper();
	    return helper.convert(result);
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
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }

    private IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagementApiDemux().getApiChannel();
    }

    protected IBatchManagement getBatchManagement() {
	return getMicroservice().getBatchManagementApiDemux().getApiChannel();
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiDemux().getApiChannel();
    }
}