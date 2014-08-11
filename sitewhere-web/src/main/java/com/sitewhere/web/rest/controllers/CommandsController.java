/*
 * CommandsController.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for device command operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/commands")
@Api(value = "", description = "Operations related to SiteWhere device commands.")
public class CommandsController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(CommandsController.class);

	/**
	 * Update an existing device command.
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update device command information")
	public IDeviceCommand updateDeviceCommand(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@RequestBody DeviceCommandCreateRequest request) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateDeviceCommand", LOGGER);
		try {
			return SiteWhere.getServer().getDeviceManagement().updateDeviceCommand(token, request);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get a device command by unique token.
	 * 
	 * @param hardwareId
	 * @return
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get a device command by unique token")
	public IDeviceCommand getDeviceCommandByToken(
			@ApiParam(value = "Token", required = true) @PathVariable String token) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getDeviceCommandByToken", LOGGER);
		try {
			return assertDeviceCommandByToken(token);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Delete an existing device command.
	 * 
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete a device command based on token")
	public IDeviceCommand deleteDeviceCommand(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteDeviceCommand", LOGGER);
		try {
			return SiteWhere.getServer().getDeviceManagement().deleteDeviceCommand(token, force);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Gets a device command by token and throws an exception if not found.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceCommand assertDeviceCommandByToken(String token) throws SiteWhereException {
		IDeviceCommand result = SiteWhere.getServer().getDeviceManagement().getDeviceCommandByToken(token);
		if (result == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return result;
	}
}