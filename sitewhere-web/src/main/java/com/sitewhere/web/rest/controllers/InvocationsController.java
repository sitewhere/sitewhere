/*
 * InvocationsController.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.search.ISearchResults;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for command invocation operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/invocations")
@Api(value = "", description = "Operations related to SiteWhere command invocations.")
public class InvocationsController extends SiteWhereController {

	/**
	 * Get a command invocation by unique id.
	 * 
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get device command invocation by unique id.")
	public IDeviceCommandInvocation getDeviceCommandInvocation(
			@ApiParam(value = "Unique id", required = true) @PathVariable String id)
			throws SiteWhereException {
		IDeviceCommandInvocation found =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceCommandInvocation(id);
		return DeviceCommandInvocation.copy(found);
	}

	/**
	 * List all responses for a command invocation.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{id}/responses", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List all responses for a device command invocation.")
	public ISearchResults<IDeviceCommandResponse> listCommandInvocationResponses(
			@ApiParam(value = "Invocation id", required = true) @PathVariable String id)
			throws SiteWhereException {
		return SiteWhereServer.getInstance().getDeviceManagement().listDeviceCommandInvocationResponses(id);
	}
}