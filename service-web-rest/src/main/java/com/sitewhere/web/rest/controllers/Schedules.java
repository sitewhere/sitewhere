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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.SiteWhere;
import com.sitewhere.web.rest.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for schedule operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/schedules")
@Api(value = "schedules")
public class Schedules extends RestController {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Create a schedule.
     * 
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Create new schedule")
    @Secured({ SiteWhereRoles.REST })
    public ISchedule createSchedule(@RequestBody ScheduleCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getScheduleManagement(servletRequest).createSchedule(request);
    }

    /**
     * Get a schedule by token.
     * 
     * @param token
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get schedule by token")
    @Secured({ SiteWhereRoles.REST })
    public ISchedule getScheduleByToken(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getScheduleManagement(servletRequest).getScheduleByToken(token);
    }

    /**
     * Update an existing schedule.
     * 
     * @param request
     * @param token
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "Update an existing schedule")
    @Secured({ SiteWhereRoles.REST })
    public ISchedule updateSchedule(@RequestBody ScheduleCreateRequest request,
	    @ApiParam(value = "Token", required = true) @PathVariable String token, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getScheduleManagement(servletRequest).updateSchedule(token, request);
    }

    /**
     * List schedules that match criteria.
     * 
     * @param page
     * @param pageSize
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List schedules matching criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<ISchedule> listSchedules(
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	return getScheduleManagement(servletRequest).listSchedules(criteria);
    }

    /**
     * Delete a schedule.
     * 
     * @param token
     * @param force
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Delete a schedule")
    @Secured({ SiteWhereRoles.REST })
    public ISchedule deleteSchedule(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getScheduleManagement(servletRequest).deleteSchedule(token, force);
    }

    /**
     * Get the schedule management implementation for the current tenant.
     * 
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    protected IScheduleManagement getScheduleManagement(HttpServletRequest servletRequest) throws SiteWhereException {
	return SiteWhere.getServer().getScheduleManagement(getTenant(servletRequest));
    }
}