/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for site operations.
 */
@Path("/api/zones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "zones")
public class Zones {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Zones.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a new zone.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create new zone")
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
    @ApiOperation(value = "Get zone by token")
    public Response getZone(
	    @ApiParam(value = "Unique token that identifies zone", required = true) @PathParam("zoneToken") String zoneToken)
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
    @ApiOperation(value = "Update an existing zone")
    public Response updateZone(
	    @ApiParam(value = "Unique token that identifies zone", required = true) @PathParam("zoneToken") String zoneToken,
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
    @ApiOperation(value = "List zones that match criteria")
    public Response listZones(
	    @ApiParam(value = "Token that identifies an area", required = false) @QueryParam("areaToken") String areaToken,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
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
    @ApiOperation(value = "Delete zone by unique token")
    public Response deleteZone(
	    @ApiParam(value = "Unique token that identifies zone", required = true) @PathParam("zoneToken") String zoneToken)
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
	return getMicroservice().getDeviceManagementApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}