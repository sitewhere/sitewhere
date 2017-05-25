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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.rest.model.device.event.request.DeviceEventCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.Assignments;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for event operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/events")
@Api(value = "events", description = "Operations related to SiteWhere device events.")
@DocumentedController(name = "Events")
public class EventsController extends RestController {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Used by AJAX calls to find an event by unique id.
     * 
     * @param eventId
     * @return
     */
    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get event by unique id")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Assignments.CreateAssignmentMeasurementsResponse.class, description = "getEventByIdMeasurementsResponse.md"),
	    @Example(stage = Stage.Response, json = Assignments.CreateAssignmentLocationResponse.class, description = "getEventByIdLocationResponse.md"),
	    @Example(stage = Stage.Response, json = Assignments.CreateAssignmentAlertResponse.class, description = "getEventByIdAlertResponse.md") })
    public IDeviceEvent getEventById(@ApiParam(value = "Event id", required = true) @PathVariable String eventId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getEventById", LOGGER);
	try {
	    return SiteWhere.getServer().getDeviceEventManagement(getTenant(servletRequest))
		    .getDeviceEventById(eventId);
	} finally {
	    Tracer.stop(LOGGER);
	}
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
    @ResponseBody
    @ApiOperation(value = "Update event by unique id")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Assignments.CreateAssignmentMeasurementsResponse.class, description = "getEventByIdMeasurementsResponse.md"),
	    @Example(stage = Stage.Response, json = Assignments.CreateAssignmentLocationResponse.class, description = "getEventByIdLocationResponse.md"),
	    @Example(stage = Stage.Response, json = Assignments.CreateAssignmentAlertResponse.class, description = "getEventByIdAlertResponse.md") })
    public IDeviceEvent updateEvent(@ApiParam(value = "Event id", required = true) @PathVariable String eventId,
	    @RequestBody DeviceEventCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "updateEvent", LOGGER);
	try {
	    return SiteWhere.getServer().getDeviceEventManagement(getTenant(servletRequest)).updateDeviceEvent(eventId,
		    request);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Get an event by its alternate (external) id.
     * 
     * @param altId
     * @return
     */
    @RequestMapping(value = "/alternate/{alternateId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get event by alternate (external) id")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceEvent getEventByAlternateId(
	    @ApiParam(value = "Alternate id", required = true) @PathVariable String alternateId,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getEventByAlternateId", LOGGER);
	try {
	    return SiteWhere.getServer().getDeviceEventManagement(getTenant(servletRequest))
		    .getDeviceEventByAlternateId(alternateId);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }
}