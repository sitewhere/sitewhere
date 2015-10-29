/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;

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
import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.scheduling.ScheduledJobMarshalHelper;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.user.SiteWhereRoles;
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
 * Controller for scheduled jobs.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/jobs")
@Api(value = "jobs", description = "Operations related to SiteWhere scheduled jobs.")
@DocumentedController(name = "Scheduled Jobs")
public class ScheduledJobsController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ScheduledJobsController.class);

	/**
	 * Create a new scheduled job.
	 * 
	 * @param request
	 * @param servletRequest
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create new scheduled job")
	@Secured({ SiteWhereRoles.REST })
	@Documented(examples = {
			@Example(stage = Stage.Request, json = Schedules.CreateScheduledJobRequest.class, description = "createScheduledJobRequest.md"),
			@Example(stage = Stage.Response, json = Schedules.CreateScheduledJobResponse.class, description = "createScheduledJobResponse.md") })
	public IScheduledJob createScheduledJob(@RequestBody ScheduledJobCreateRequest request,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createScheduledJob", LOGGER);
		try {
			return getScheduleManagement(servletRequest).createScheduledJob(request);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	@RequestMapping(value = "/{token}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get scheduled job by token")
	@Secured({ SiteWhereRoles.REST })
	@Documented(examples = { @Example(stage = Stage.Response, json = Schedules.CreateScheduledJobResponse.class, description = "getScheduledJobByTokenResponse.md") })
	public IScheduledJob getScheduledJobByToken(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getScheduledJobByToken", LOGGER);
		try {
			return getScheduleManagement(servletRequest).getScheduledJobByToken(token);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Update an existing scheduled job.
	 * 
	 * @param request
	 * @param token
	 * @param servletRequest
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update existing scheduled job")
	@Secured({ SiteWhereRoles.REST })
	@Documented(examples = {
			@Example(stage = Stage.Request, json = Schedules.UpdateScheduledJobRequest.class, description = "updateScheduledJobRequest.md"),
			@Example(stage = Stage.Response, json = Schedules.UpdateScheduledJobResponse.class, description = "updateScheduledJobResponse.md") })
	public IScheduledJob updateScheduledJob(@RequestBody ScheduledJobCreateRequest request,
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateScheduledJob", LOGGER);
		try {
			return getScheduleManagement(servletRequest).updateScheduledJob(token, request);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List scheduled jobs that match the criteria.
	 * 
	 * @param includeContext
	 * @param page
	 * @param pageSize
	 * @param servletRequest
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List scheduled jobs matching criteria")
	@Secured({ SiteWhereRoles.REST })
	@Documented(examples = { @Example(stage = Stage.Response, json = Schedules.ListScheduledjobsResponse.class, description = "listScheduledJobsResponse.md") })
	public ISearchResults<IScheduledJob> listScheduledJobs(
			@ApiParam(value = "Include context information", required = false) @RequestParam(defaultValue = "false") boolean includeContext,
			@ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") @Concerns(values = { ConcernType.Paging }) int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") @Concerns(values = { ConcernType.Paging }) int pageSize,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listScheduledJobs", LOGGER);
		try {
			SearchCriteria criteria = new SearchCriteria(page, pageSize);
			ISearchResults<IScheduledJob> results =
					getScheduleManagement(servletRequest).listScheduledJobs(criteria);
			if (!includeContext) {
				return results;
			} else {
				List<IScheduledJob> converted = new ArrayList<IScheduledJob>();
				ScheduledJobMarshalHelper helper =
						new ScheduledJobMarshalHelper(getTenant(servletRequest), true);
				for (IScheduledJob job : results.getResults()) {
					converted.add(helper.convert(job));
				}
				return new SearchResults<IScheduledJob>(converted, results.getNumResults());
			}
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Delete an existing scheduled job.
	 * 
	 * @param token
	 * @param force
	 * @param servletRequest
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete scheduled job")
	@Secured({ SiteWhereRoles.REST })
	@Documented(examples = { @Example(stage = Stage.Response, json = Schedules.CreateScheduledJobResponse.class, description = "deleteScheduledJobResponse.md") })
	public IScheduledJob deleteScheduledJob(
			@ApiParam(value = "Token", required = true) @PathVariable String token,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteScheduledJob", LOGGER);
		try {
			return getScheduleManagement(servletRequest).deleteScheduledJob(token, force);
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