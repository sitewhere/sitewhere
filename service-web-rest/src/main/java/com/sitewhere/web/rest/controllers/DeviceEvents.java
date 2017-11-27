/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.device.event.request.DeviceEventCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for event operations.
 * 
 * @author Derek Adams
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/events")
@Api(value = "events")
public class DeviceEvents extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Used by AJAX calls to find an event by unique id.
     * 
     * @param eventId
     * @return
     */
    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get event by unique id")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceEvent getEventById(@ApiParam(value = "Event id", required = true) @PathVariable String eventId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getDeviceEventManagement().getDeviceEventById(eventId);
    }

    /**
     * Update information for an existing device event.
     * 
     * @param eventId
     * @param request
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{eventId}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update event by unique id")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceEvent updateEvent(@ApiParam(value = "Event id", required = true) @PathVariable String eventId,
	    @RequestBody DeviceEventCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getDeviceEventManagement().updateDeviceEvent(eventId, request);
    }

    /**
     * Get an event by its alternate (external) id.
     * 
     * @param altId
     * @return
     */
    @RequestMapping(value = "/alternate/{alternateId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get event by alternate (external) id")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceEvent getEventByAlternateId(
	    @ApiParam(value = "Alternate id", required = true) @PathVariable String alternateId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getDeviceEventManagement().getDeviceEventByAlternateId(alternateId);
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }
}