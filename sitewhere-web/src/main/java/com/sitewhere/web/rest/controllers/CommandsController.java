/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.Commands;
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
@Api(value = "commands", description = "Operations related to SiteWhere device commands.")
@DocumentedController(name = "Device Commands")
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
	@ApiOperation(value = "Update an existing device command")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = {
			@Example(stage = Stage.Request, json = Commands.DeviceCommandUpdateRequest.class, description = "updateDeviceCommandRequest.md"),
			@Example(stage = Stage.Response, json = Commands.DeviceCommandUpdateResponse.class, description = "updateDeviceCommandResponse.md") })
	public IDeviceCommand updateDeviceCommand(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@RequestBody DeviceCommandCreateRequest request, HttpServletRequest servletRequest)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateDeviceCommand", LOGGER);
		try {
			return SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).updateDeviceCommand(
					token, request);
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
	@ApiOperation(value = "Get device command by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = { @Example(stage = Stage.Response, json = Commands.DeviceCommandByTokenResponse.class, description = "getDeviceCommandByTokenResponse.md") })
	public IDeviceCommand getDeviceCommandByToken(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getDeviceCommandByToken", LOGGER);
		try {
			return assertDeviceCommandByToken(token, servletRequest);
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
	@ApiOperation(value = "Delete device command by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	@Documented(examples = { @Example(stage = Stage.Response, json = Commands.DeviceCommandByTokenResponse.class, description = "deleteDeviceCommandResponse.md") })
	public IDeviceCommand deleteDeviceCommand(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteDeviceCommand", LOGGER);
		try {
			return SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).deleteDeviceCommand(
					token, force);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Gets a device command by token and throws an exception if not found.
	 * 
	 * @param token
	 * @param servletRequest
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceCommand assertDeviceCommandByToken(String token, HttpServletRequest servletRequest)
			throws SiteWhereException {
		IDeviceCommand result =
				SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).getDeviceCommandByToken(
						token);
		if (result == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return result;
	}
}