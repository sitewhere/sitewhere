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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestControllerBase;
import com.sitewhere.web.rest.marshaling.ScheduledJobMarshalHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for scheduled jobs.
 * 
 * @author Derek Adams
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/jobs")
@Api(value = "jobs")
public class ScheduledJobs extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(ScheduledJobs.class);

    /**
     * Create a new scheduled job.
     * 
     * @param request
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new scheduled job")
    @Secured({ SiteWhereRoles.REST })
    public IScheduledJob createScheduledJob(@RequestBody ScheduledJobCreateRequest request,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getScheduleManagement().createScheduledJob(request);
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    @ApiOperation(value = "Get scheduled job by token")
    @Secured({ SiteWhereRoles.REST })
    public IScheduledJob getScheduledJobByToken(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getScheduleManagement().getScheduledJobByToken(token);
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
    @ApiOperation(value = "Update existing scheduled job")
    @Secured({ SiteWhereRoles.REST })
    public IScheduledJob updateScheduledJob(@RequestBody ScheduledJobCreateRequest request,
	    @ApiParam(value = "Token", required = true) @PathVariable String token, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	return getScheduleManagement().updateScheduledJob(token, request);
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
    @ApiOperation(value = "List scheduled jobs matching criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IScheduledJob> listScheduledJobs(
	    @ApiParam(value = "Include context information", required = false) @RequestParam(defaultValue = "false") boolean includeContext,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<IScheduledJob> results = getScheduleManagement().listScheduledJobs(criteria);
	if (!includeContext) {
	    return results;
	} else {
	    List<IScheduledJob> converted = new ArrayList<IScheduledJob>();
	    ScheduledJobMarshalHelper helper = new ScheduledJobMarshalHelper(getScheduleManagement(),
		    getDeviceManagement(), getAssetResolver(), true);
	    for (IScheduledJob job : results.getResults()) {
		converted.add(helper.convert(job));
	    }
	    return new SearchResults<IScheduledJob>(converted, results.getNumResults());
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
    @ApiOperation(value = "Delete scheduled job")
    @Secured({ SiteWhereRoles.REST })
    public IScheduledJob deleteScheduledJob(@ApiParam(value = "Token", required = true) @PathVariable String token,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return getScheduleManagement().deleteScheduledJob(token, force);
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiDemux().getApiChannel();
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }

    private IAssetResolver getAssetResolver() {
	return getMicroservice().getAssetResolver();
    }
}