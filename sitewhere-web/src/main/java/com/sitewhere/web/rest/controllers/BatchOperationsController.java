/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.device.batch.BatchUtils;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.device.request.BatchCommandInvocationRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Controller for batch operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/batch")
@Api(value = "batch", description = "Operations related to SiteWhere batch operations.")
public class BatchOperationsController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(BatchOperationsController.class);

	@RequestMapping(value = "/command", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new batch command invocation")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public IBatchOperation createBatchCommandInvocation(@RequestBody BatchCommandInvocationRequest request)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createBatchCommandInvocation", LOGGER);
		try {
			IBatchOperation result =
					SiteWhere.getServer().getDeviceManagement().createBatchCommandInvocation(request);
			return BatchOperation.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	@RequestMapping(value = "/command/criteria", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new batch command invocation based on criteria")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public IBatchOperation createBatchCommandByCriteria(@RequestBody BatchCommandForCriteriaRequest request)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createBatchCommandByCriteria", LOGGER);
		try {
			// Resolve hardware ids for devices matching criteria.
			List<String> hardwareIds = BatchUtils.getHardwareIds(request);

			// Create batch command invocation.
			BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
			invoke.setToken(request.getToken());
			invoke.setCommandToken(request.getCommandToken());
			invoke.setParameterValues(request.getParameterValues());
			invoke.setHardwareIds(hardwareIds);

			IBatchOperation result =
					SiteWhere.getServer().getDeviceManagement().createBatchCommandInvocation(invoke);
			return BatchOperation.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}
}