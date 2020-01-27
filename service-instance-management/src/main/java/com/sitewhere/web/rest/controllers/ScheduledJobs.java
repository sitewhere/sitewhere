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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.marshaling.ScheduledJobMarshalHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for scheduled jobs.
 */
@Path("/api/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "jobs")
public class ScheduledJobs {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(ScheduledJobs.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a new scheduled job.
     * 
     * @param request
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create new scheduled job")
    public Response createScheduledJob(@RequestBody ScheduledJobCreateRequest request) throws SiteWhereException {
	return Response.ok(getScheduleManagement().createScheduledJob(request)).build();
    }

    /**
     * Get scheduled job by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}")
    @ApiOperation(value = "Get scheduled job by token")
    public Response getScheduledJobByToken(@ApiParam(value = "Token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	return Response.ok(getScheduleManagement().getScheduledJobByToken(token)).build();
    }

    /**
     * Update an existing scheduled job.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{token}")
    @ApiOperation(value = "Update existing scheduled job")
    public Response updateScheduledJob(@RequestBody ScheduledJobCreateRequest request,
	    @ApiParam(value = "Token", required = true) @PathParam("token") String token) throws SiteWhereException {
	return Response.ok(getScheduleManagement().updateScheduledJob(token, request)).build();
    }

    /**
     * List scheduled jobs that match the criteria.
     * 
     * @param includeContext
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @ApiOperation(value = "List scheduled jobs matching criteria")
    public Response listScheduledJobs(
	    @ApiParam(value = "Include context information", required = false) @QueryParam("includeContext") @DefaultValue("false") boolean includeContext,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<IScheduledJob> results = getScheduleManagement().listScheduledJobs(criteria);
	if (!includeContext) {
	    return Response.ok(results).build();
	} else {
	    List<IScheduledJob> converted = new ArrayList<IScheduledJob>();
	    ScheduledJobMarshalHelper helper = new ScheduledJobMarshalHelper(getScheduleManagement(),
		    getDeviceManagement(), getAssetManagement(), true);
	    for (IScheduledJob job : results.getResults()) {
		converted.add(helper.convert(job));
	    }
	    return Response.ok(new SearchResults<IScheduledJob>(converted, results.getNumResults())).build();
	}
    }

    /**
     * Delete an existing scheduled job.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{token}")
    @ApiOperation(value = "Delete scheduled job")
    public Response deleteScheduledJob(@ApiParam(value = "Token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	return Response.ok(getScheduleManagement().deleteScheduledJob(token)).build();
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiChannel();
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}