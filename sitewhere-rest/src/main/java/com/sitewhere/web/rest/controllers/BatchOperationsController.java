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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.device.batch.BatchUtils;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.device.request.BatchCommandInvocationRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.server.scheduling.ScheduledJobHelper;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.rest.annotations.Concerns;
import com.sitewhere.web.rest.annotations.Concerns.ConcernType;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.BatchOperations;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for batch operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/batch")
@Api(value = "batch", description = "Operations related to SiteWhere batch operations.")
@DocumentedController(name = "Batch Operations")
public class BatchOperationsController extends RestController {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    @RequestMapping(value = "/{batchToken}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get batch operation by unique token")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = BatchOperations.GetBatchOperationResponse.class, description = "getBatchOperationByTokenResponse.md") })
    public IBatchOperation getBatchOperationByToken(
	    @ApiParam(value = "Unique token that identifies batch operation", required = true) @PathVariable String batchToken,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getBatchOperationByToken", LOGGER);
	try {
	    IBatchOperation batch = SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest))
		    .getBatchOperation(batchToken);
	    if (batch == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	    }
	    return BatchOperation.copy(batch);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List batch operations")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = BatchOperations.ListBatchOperationsResponse.class, description = "listBatchOperationsResponse.md") })
    public ISearchResults<IBatchOperation> listBatchOperations(
	    @ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") @Concerns(values = {
		    ConcernType.Paging }) int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") @Concerns(values = {
		    ConcernType.Paging }) int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "listDeviceGroups", LOGGER);
	try {
	    SearchCriteria criteria = new SearchCriteria(page, pageSize);
	    ISearchResults<IBatchOperation> results = SiteWhere.getServer()
		    .getDeviceManagement(getTenant(servletRequest)).listBatchOperations(includeDeleted, criteria);
	    List<IBatchOperation> opsConv = new ArrayList<IBatchOperation>();
	    for (IBatchOperation op : results.getResults()) {
		opsConv.add(BatchOperation.copy(op));
	    }
	    return new SearchResults<IBatchOperation>(opsConv, results.getNumResults());
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    @RequestMapping(value = "/{operationToken}/elements", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List batch operation elements")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = BatchOperations.ListBatchOperationElementsResponse.class, description = "listBatchOperationElementsResponse.md") })
    public ISearchResults<IBatchElement> listBatchOperationElements(
	    @ApiParam(value = "Unique token that identifies batch operation", required = true) @PathVariable String operationToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") @Concerns(values = {
		    ConcernType.Paging }) int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") @Concerns(values = {
		    ConcernType.Paging }) int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "listDeviceGroupElements", LOGGER);
	try {
	    BatchElementSearchCriteria criteria = new BatchElementSearchCriteria(page, pageSize);
	    ISearchResults<IBatchElement> results = SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest))
		    .listBatchElements(operationToken, criteria);
	    return results;
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    @RequestMapping(value = "/command", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Create new batch command invocation")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Request, json = BatchOperations.BatchCommandInvocationCreateRequest.class, description = "createBatchCommandInvocationRequest.md"),
	    @Example(stage = Stage.Response, json = BatchOperations.GetBatchOperationResponse.class, description = "createBatchCommandInvocationResponse.md") })
    public IBatchOperation createBatchCommandInvocation(@RequestBody BatchCommandInvocationRequest request,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "createBatchCommandInvocation", LOGGER);
	try {
	    IBatchOperation result = SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest))
		    .createBatchCommandInvocation(request);
	    return BatchOperation.copy(result);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Create a batch operation that invokes a command for all devices that
     * match the given criteria.
     * 
     * @param request
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/command/criteria", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Create batch command operation based on criteria")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Request, json = BatchOperations.BatchCommandInvocationByCriteriaSpecRequest.class, description = "createBatchCommandByCriteriaSpecRequest.md"),
	    @Example(stage = Stage.Request, json = BatchOperations.BatchCommandInvocationByCriteriaGroupRequest.class, description = "createBatchCommandByCriteriaGroupRequest.md"),
	    @Example(stage = Stage.Request, json = BatchOperations.BatchCommandInvocationByCriteriaGroupRoleRequest.class, description = "createBatchCommandByCriteriaGroupRoleRequest.md"),
	    @Example(stage = Stage.Response, json = BatchOperations.GetBatchOperationResponse.class, description = "createBatchCommandByCriteriaResponse.md") })
    public IBatchOperation createBatchCommandByCriteria(@RequestBody BatchCommandForCriteriaRequest request,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "createBatchCommandByCriteria", LOGGER);
	try {
	    // Resolve hardware ids for devices matching criteria.
	    List<String> hardwareIds = BatchUtils.getHardwareIds(request, getTenant(servletRequest));

	    // Create batch command invocation.
	    BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
	    invoke.setToken(request.getToken());
	    invoke.setCommandToken(request.getCommandToken());
	    invoke.setParameterValues(request.getParameterValues());
	    invoke.setHardwareIds(hardwareIds);

	    IBatchOperation result = SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest))
		    .createBatchCommandInvocation(invoke);
	    return BatchOperation.copy(result);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Schedule job that will create a new batch command invocation based on the
     * given criteria.
     * 
     * @param request
     * @param scheduleToken
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/command/criteria/schedules/{scheduleToken}", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Schedule batch command operation based on criteria")
    @Secured({ SiteWhereRoles.REST })
    @Documented
    public IScheduledJob scheduleBatchCommandByCriteria(@RequestBody BatchCommandForCriteriaRequest request,
	    @ApiParam(value = "Schedule token", required = true) @PathVariable String scheduleToken,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "scheduleBatchCommandByCriteria", LOGGER);
	try {
	    assureDeviceCommand(request.getCommandToken(), servletRequest);
	    IScheduledJobCreateRequest job = ScheduledJobHelper
		    .createBatchCommandInvocationJobByCriteria(UUID.randomUUID().toString(), request, scheduleToken);
	    return SiteWhere.getServer().getScheduleManagement(getTenant(servletRequest)).createScheduledJob(job);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Get a device command by unique token. Throw an exception if not found.
     * 
     * @param token
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceCommand assureDeviceCommand(String token, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDeviceCommand command = SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest))
		.getDeviceCommandByToken(token);
	if (command == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
	}
	return command;
    }
}