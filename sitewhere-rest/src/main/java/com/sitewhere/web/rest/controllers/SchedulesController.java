/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

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
import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.rest.annotations.Concerns;
import com.sitewhere.web.rest.annotations.Concerns.ConcernType;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.Schedules;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for schedule operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/schedules")
@Api(value = "schedules", description = "Operations related to SiteWhere schedules.")
@DocumentedController(name = "Schedules")
public class SchedulesController extends RestController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SchedulesController.class);

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
	@Documented(examples = {
			@Example(stage = Stage.Request, json = Schedules.CreateScheduleRequest.class, description = "createScheduleRequest.md"),
			@Example(stage = Stage.Response, json = Schedules.CreateScheduleResponse.class, description = "createScheduleResponse.md") })
	public ISchedule createSchedule(@RequestBody ScheduleCreateRequest request,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createSchedule", LOGGER);
		try {
			return getScheduleManagement(servletRequest).createSchedule(request);
		} finally {
			Tracer.stop(LOGGER);
		}
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
	@Documented(examples = { @Example(stage = Stage.Response, json = Schedules.CreateScheduleResponse.class, description = "getScheduleByTokenResponse.md") })
	public ISchedule getScheduleByToken(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getScheduleByToken", LOGGER);
		try {
			return getScheduleManagement(servletRequest).getScheduleByToken(token);
		} finally {
			Tracer.stop(LOGGER);
		}
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
	@Documented(examples = {
			@Example(stage = Stage.Request, json = Schedules.UpdateScheduleRequest.class, description = "updateScheduleRequest.md"),
			@Example(stage = Stage.Response, json = Schedules.UpdateScheduleResponse.class, description = "updateScheduleResponse.md") })
	public ISchedule updateSchedule(@RequestBody ScheduleCreateRequest request,
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateSchedule", LOGGER);
		try {
			return getScheduleManagement(servletRequest).updateSchedule(token, request);
		} finally {
			Tracer.stop(LOGGER);
		}
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
	@Documented(examples = { @Example(stage = Stage.Response, json = Schedules.ListSchedulesResponse.class, description = "listSchedulesResponse.md") })
	public ISearchResults<ISchedule> listSchedules(
			@ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") @Concerns(values = { ConcernType.Paging }) int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") @Concerns(values = { ConcernType.Paging }) int pageSize,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listSchedules", LOGGER);
		try {
			SearchCriteria criteria = new SearchCriteria(page, pageSize);
			return getScheduleManagement(servletRequest).listSchedules(criteria);
		} finally {
			Tracer.stop(LOGGER);
		}
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
	@Documented(examples = { @Example(stage = Stage.Response, json = Schedules.CreateScheduleResponse.class, description = "deleteScheduleResponse.md") })
	public ISchedule deleteSchedule(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteSchedule", LOGGER);
		try {
			return getScheduleManagement(servletRequest).deleteSchedule(token, force);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get the schedule management implementation for the current tenant.
	 * 
	 * @param servletRequest
	 * @return
	 * @throws SiteWhereException
	 */
	protected IScheduleManagement getScheduleManagement(HttpServletRequest servletRequest)
			throws SiteWhereException {
		return SiteWhere.getServer().getScheduleManagement(getTenant(servletRequest));
	}
}