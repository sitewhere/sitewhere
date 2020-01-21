/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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

import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.spi.SiteWhereException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for event operations.
 */
@Path("/api/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "events")
public class DeviceEvents {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceEvents.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Find a device event by unique id.
     * 
     * @param eventId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/id/{eventId}")
    @ApiOperation(value = "Get event by unique id")
    public Response getEventById(@ApiParam(value = "Event id", required = true) @PathParam("eventId") String eventId)
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
    @ApiOperation(value = "Get event by alternate (external) id")
    public Response getEventByAlternateId(
	    @ApiParam(value = "Alternate id", required = true) @PathParam("alternateId") String alternateId)
	    throws SiteWhereException {
	return Response.ok(getDeviceEventManagement().getDeviceEventByAlternateId(alternateId)).build();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(getMicroservice().getDeviceEventManagementApiChannel());
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}