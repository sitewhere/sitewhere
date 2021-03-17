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
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.area.request.ZoneCreateRequest;
import com.sitewhere.rest.model.search.device.ZoneSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

import io.swagger.annotations.Api;

/**
 * Controller for site operations.
 */
@Path("/api/zones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "zones")
@Tag(name = "Zones", description = "Zones provide geospatial boundaries and metadata for defining meaningful spaces in an area.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Zones {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Zones.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new zone.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create new zone", description = "Create new zone")
    public Response createZone(@RequestBody ZoneCreateRequest request) throws SiteWhereException {
	return Response.ok(getDeviceManagement().createZone(request)).build();
    }

    /**
     * Get zone based on unique token.
     * 
     * @param zoneToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{zoneToken}")
    @Operation(summary = "Get zone by token", description = "Get zone by token")
    public Response getZone(
	    @Parameter(description = "Unique token that identifies zone", required = true) @PathParam("zoneToken") String zoneToken)
	    throws SiteWhereException {
	return Response.ok(assertZone(zoneToken)).build();
    }

    /**
     * Update information for a zone.
     * 
     * @param zoneToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{zoneToken}")
    @Operation(summary = "Update an existing zone", description = "Update an existing zone")
    public Response updateZone(
	    @Parameter(description = "Unique token that identifies zone", required = true) @PathParam("zoneToken") String zoneToken,
	    @RequestBody ZoneCreateRequest request) throws SiteWhereException {
	IZone existing = assertZone(zoneToken);
	return Response.ok(getDeviceManagement().updateZone(existing.getId(), request)).build();
    }

    /**
     * List zones that match criteria.
     * 
     * @param areaToken
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List zones that match criteria", description = "List zones that match criteria")
    public Response listZones(
	    @Parameter(description = "Token that identifies an area", required = false) @QueryParam("areaToken") String areaToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	ZoneSearchCriteria criteria = new ZoneSearchCriteria(page, pageSize);
	criteria.setAreaToken(areaToken);
	return Response.ok(getDeviceManagement().listZones(criteria)).build();
    }

    /**
     * Delete an existing zone.
     * 
     * @param zoneToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{zoneToken}")
    @Operation(summary = "Delete zone", description = "Delete zone by unique token")
    public Response deleteZone(
	    @Parameter(description = "Unique token that identifies zone", required = true) @PathParam("zoneToken") String zoneToken)
	    throws SiteWhereException {
	IZone existing = assertZone(zoneToken);
	return Response.ok(getDeviceManagement().deleteZone(existing.getId())).build();
    }

    /**
     * Get zone associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IZone assertZone(String token) throws SiteWhereException {
	IZone zone = getDeviceManagement().getZoneByToken(token);
	if (zone == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidZoneToken, ErrorLevel.ERROR);
	}
	return zone;
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}