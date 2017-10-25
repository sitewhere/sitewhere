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

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.device.batch.BatchUtils;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.device.request.BatchCommandInvocationRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
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
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/batch")
@Api(value = "batch")
public class BatchOperations extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    @RequestMapping(value = "/{batchToken}", method = RequestMethod.GET)
    @ApiOperation(value = "Get batch operation by unique token")
    @Secured({ SiteWhereRoles.REST })
    public IBatchOperation getBatchOperationByToken(
	    @ApiParam(value = "Unique token that identifies batch operation", required = true) @PathVariable String batchToken,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IBatchOperation batch = getBatchManagement().getBatchOperation(batchToken);
	if (batch == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	return BatchOperation.copy(batch);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List batch operations")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IBatchOperation> listBatchOperations(
	    @ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<IBatchOperation> results = getBatchManagement().listBatchOperations(includeDeleted, criteria);
	List<IBatchOperation> opsConv = new ArrayList<IBatchOperation>();
	for (IBatchOperation op : results.getResults()) {
	    opsConv.add(BatchOperation.copy(op));
	}
	return new SearchResults<IBatchOperation>(opsConv, results.getNumResults());
    }

    @RequestMapping(value = "/{operationToken}/elements", method = RequestMethod.GET)
    @ApiOperation(value = "List batch operation elements")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IBatchElement> listBatchOperationElements(
	    @ApiParam(value = "Unique token that identifies batch operation", required = true) @PathVariable String operationToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	BatchElementSearchCriteria criteria = new BatchElementSearchCriteria(page, pageSize);
	ISearchResults<IBatchElement> results = getBatchManagement().listBatchElements(operationToken, criteria);
	return results;
    }

    @RequestMapping(value = "/command", method = RequestMethod.POST)
    @ApiOperation(value = "Create new batch command invocation")
    @Secured({ SiteWhereRoles.REST })
    public IBatchOperation createBatchCommandInvocation(@RequestBody BatchCommandInvocationRequest request,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IBatchOperation result = getBatchManagement().createBatchCommandInvocation(request);
	return BatchOperation.copy(result);
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
    @ApiOperation(value = "Create batch command operation based on criteria")
    @Secured({ SiteWhereRoles.REST })
    public IBatchOperation createBatchCommandByCriteria(@RequestBody BatchCommandForCriteriaRequest request,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	// Resolve hardware ids for devices matching criteria.
	List<String> hardwareIds = BatchUtils.getHardwareIds(request, getDeviceManagement());

	// Create batch command invocation.
	BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
	invoke.setToken(request.getToken());
	invoke.setCommandToken(request.getCommandToken());
	invoke.setParameterValues(request.getParameterValues());
	invoke.setHardwareIds(hardwareIds);

	IBatchOperation result = getBatchManagement().createBatchCommandInvocation(invoke);
	return BatchOperation.copy(result);
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
    @ApiOperation(value = "Schedule batch command operation based on criteria")
    @Secured({ SiteWhereRoles.REST })
    public IScheduledJob scheduleBatchCommandByCriteria(@RequestBody BatchCommandForCriteriaRequest request,
	    @ApiParam(value = "Schedule token", required = true) @PathVariable String scheduleToken,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	assureDeviceCommand(request.getCommandToken(), servletRequest);
	// IScheduledJobCreateRequest job = ScheduledJobHelper
	// .createBatchCommandInvocationJobByCriteria(UUID.randomUUID().toString(),
	// request, scheduleToken);
	// return
	// SiteWhere.getServer().getScheduleManagement(getTenant(servletRequest)).createScheduledJob(job);
	return null;
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
	IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(token);
	if (command == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
	}
	return command;
    }

    private IDeviceManagement getDeviceManagement() {
	return null;
    }

    protected IBatchManagement getBatchManagement() {
	return null;
    }
}