/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for schedule operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/schedules")
@Api(value = "schedules")
public class Schedules extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Schedules.class);

    /**
     * Create a schedule.
     * 
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new schedule")
    @Secured({ SiteWhereRoles.REST })
    public ISchedule createSchedule(@RequestBody ScheduleCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getScheduleManagement().createSchedule(request);
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
    @ApiOperation(value = "Get schedule by token")
    @Secured({ SiteWhereRoles.REST })
    public ISchedule getScheduleByToken(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getScheduleManagement().getScheduleByToken(token);
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
    @ApiOperation(value = "Update an existing schedule")
    @Secured({ SiteWhereRoles.REST })
    public ISchedule updateSchedule(@RequestBody ScheduleCreateRequest request,
	    @ApiParam(value = "Token", required = true) @PathVariable String token, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getScheduleManagement().updateSchedule(token, request);
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
    @ApiOperation(value = "List schedules matching criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<ISchedule> listSchedules(
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	return getScheduleManagement().listSchedules(criteria);
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
    @ApiOperation(value = "Delete a schedule")
    @Secured({ SiteWhereRoles.REST })
    public ISchedule deleteSchedule(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getScheduleManagement().deleteSchedule(token, force);
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiDemux().getApiChannel();
    }
}