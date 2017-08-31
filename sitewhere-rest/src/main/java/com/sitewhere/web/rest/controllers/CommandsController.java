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
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for device command operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/commands")
@Api(value = "commands", description = "Operations related to SiteWhere device commands.")
public class CommandsController extends RestController {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

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
    @Secured({ SiteWhereRoles.REST })
    public IDeviceCommand updateDeviceCommand(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @RequestBody DeviceCommandCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).updateDeviceCommand(token, request);
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
    @Secured({ SiteWhereRoles.REST })
    public IDeviceCommand getDeviceCommandByToken(
	    @ApiParam(value = "Token", required = true) @PathVariable String token, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return assertDeviceCommandByToken(token, servletRequest);
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
    @Secured({ SiteWhereRoles.REST })
    public IDeviceCommand deleteDeviceCommand(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).deleteDeviceCommand(token, force);
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
	IDeviceCommand result = SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest))
		.getDeviceCommandByToken(token);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR,
		    HttpServletResponse.SC_NOT_FOUND);
	}
	return result;
    }
}