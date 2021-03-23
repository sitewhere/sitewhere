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

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.spi.SiteWhereException;

import io.swagger.annotations.Api;

/**
 * Controller for event operations.
 */
@Path("/api/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "events")
@Tag(name = "Device Events", description = "Services used to interact with device events outside the context of a device assignment.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class DeviceEvents {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceEvents.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Find a device event by unique id.
     * 
     * @param eventId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/id/{eventId}")
    @Operation(summary = "Get event by unique id", description = "Get event by unique id")
    public Response getEventById(
	    @Parameter(description = "Event id", required = true) @PathParam("eventId") String eventId)
	    throws SiteWhereException {
	return Response.ok(getDeviceEventManagement().getDeviceEventById(UUID.fromString(eventId))).build();
    }

    /**
     * Find a device event by alternate id.
     * 
     * @param alternateId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/alternate/{alternateId}")
    @Operation(summary = "Get event by alternate (external) id", description = "Get event by alternate (external) id")
    public Response getEventByAlternateId(
	    @Parameter(description = "Alternate id", required = true) @PathParam("alternateId") String alternateId)
	    throws SiteWhereException {
	return Response.ok(getDeviceEventManagement().getDeviceEventByAlternateId(alternateId)).build();
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}