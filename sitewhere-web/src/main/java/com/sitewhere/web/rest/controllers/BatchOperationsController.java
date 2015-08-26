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

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.device.batch.BatchUtils;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.device.request.BatchCommandInvocationRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.BatchElementSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for batch operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/batch")
@Api(value = "batch", description = "Operations related to SiteWhere batch operations.")
public class BatchOperationsController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(BatchOperationsController.class);

	@RequestMapping(value = "/{batchToken}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get a batch operation by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public IBatchOperation getBatchOperationByToken(
			@ApiParam(value = "Unique token that identifies batch operation", required = true) @PathVariable String batchToken,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getBatchOperationByToken", LOGGER);
		try {
			IBatchOperation batch =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).getBatchOperation(
							batchToken);
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
	@ApiOperation(value = "List all batch operations")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IBatchOperation> listBatchOperations(
			@ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listDeviceGroups", LOGGER);
		try {
			SearchCriteria criteria = new SearchCriteria(page, pageSize);
			ISearchResults<IBatchOperation> results =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).listBatchOperations(
							includeDeleted, criteria);
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
	@ApiOperation(value = "List elements from a batch operation")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IBatchElement> listBatchOperationElements(
			@ApiParam(value = "Unique token that identifies batch operation", required = true) @PathVariable String operationToken,
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listDeviceGroupElements", LOGGER);
		try {
			BatchElementSearchCriteria criteria = new BatchElementSearchCriteria(page, pageSize);
			ISearchResults<IBatchElement> results =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).listBatchElements(
							operationToken, criteria);
			return results;
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	@RequestMapping(value = "/command", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new batch command invocation")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public IBatchOperation createBatchCommandInvocation(@RequestBody BatchCommandInvocationRequest request,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createBatchCommandInvocation", LOGGER);
		try {
			IBatchOperation result =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).createBatchCommandInvocation(
							request);
			return BatchOperation.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	@RequestMapping(value = "/command/criteria", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new batch command invocation based on criteria")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
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

			IBatchOperation result =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).createBatchCommandInvocation(
							invoke);
			return BatchOperation.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}
}