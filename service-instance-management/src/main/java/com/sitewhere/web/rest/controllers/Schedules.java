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
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.ISchedule;

import io.swagger.annotations.Api;

/**
 * Controller for schedule operations.
 */
@Path("/api/schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "schedules")
@Tag(name = "Schedules", description = "Schedules provide fixed or recurring execution of various functions.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Schedules {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Schedules.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a schedule.
     * 
     * @param request
     * @return
     */
    @POST
    @Operation(summary = "Create new schedule", description = "Create new schedule")
    public Response createSchedule(@RequestBody ScheduleCreateRequest request) throws SiteWhereException {
	return Response.ok(getScheduleManagement().createSchedule(request)).build();
    }

    /**
     * Get a schedule by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}")
    @Operation(summary = "Get schedule by token", description = "Get schedule by token")
    public Response getScheduleByToken(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	return Response.ok(getScheduleManagement().getScheduleByToken(token)).build();
    }

    /**
     * Update an existing schedule.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{token}")
    @Operation(summary = "Update an existing schedule", description = "Update an existing schedule")
    public Response updateSchedule(@RequestBody ScheduleCreateRequest request,
	    @Parameter(description = "Token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	ISchedule schedule = getScheduleManagement().getScheduleByToken(token);
	if (schedule == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
	}
	return Response.ok(getScheduleManagement().updateSchedule(schedule.getId(), request)).build();
    }

    /**
     * List schedules that match criteria.
     * 
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List schedules matching criteria", description = "List schedules matching criteria")
    public Response listSchedules(
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	return Response.ok(getScheduleManagement().listSchedules(criteria)).build();
    }

    /**
     * Delete a schedule.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{token}")
    @Operation(summary = "Delete a schedule", description = "Delete a schedule")
    public Response deleteSchedule(@Parameter(description = "Token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	ISchedule schedule = getScheduleManagement().getScheduleByToken(token);
	if (schedule == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidScheduleToken, ErrorLevel.ERROR);
	}
	return Response.ok(getScheduleManagement().deleteSchedule(schedule.getId())).build();
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}