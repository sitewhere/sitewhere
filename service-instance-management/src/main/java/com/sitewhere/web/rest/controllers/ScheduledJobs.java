/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.marshaling.ScheduledJobMarshalHelper;

import io.swagger.annotations.Api;

/**
 * Controller for scheduled jobs.
 */
@Path("/api/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "jobs")
@Tag(name = "Scheduled Jobs", description = "Scheduled jobs allow operations to be scheduled.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class ScheduledJobs {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(ScheduledJobs.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new scheduled job.
     * 
     * @param request
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create new scheduled job", description = "Create new scheduled job")
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
    @Operation(summary = "Get scheduled job by token", description = "Get scheduled job by token")
    public Response getScheduledJobByToken(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token)
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
    @Operation(summary = "Update existing scheduled job", description = "Update existing scheduled job")
    public Response updateScheduledJob(@RequestBody ScheduledJobCreateRequest request,
	    @Parameter(description = "Token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IScheduledJob job = getScheduleManagement().getScheduledJobByToken(token);
	if (job == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduledJobToken, ErrorLevel.ERROR);
	}
	return Response.ok(getScheduleManagement().updateScheduledJob(job.getId(), request)).build();
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
    @Operation(summary = "List scheduled jobs matching criteria", description = "List scheduled jobs matching criteria")
    public Response listScheduledJobs(
	    @Parameter(description = "Include context information", required = false) @QueryParam("includeContext") @DefaultValue("false") boolean includeContext,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends IScheduledJob> results = getScheduleManagement().listScheduledJobs(criteria);
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
    @Operation(summary = "Delete scheduled job", description = "Delete scheduled job")
    public Response deleteScheduledJob(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IScheduledJob job = getScheduleManagement().getScheduledJobByToken(token);
	if (job == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduledJobToken, ErrorLevel.ERROR);
	}
	return Response.ok(getScheduleManagement().deleteScheduledJob(job.getId())).build();
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

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}