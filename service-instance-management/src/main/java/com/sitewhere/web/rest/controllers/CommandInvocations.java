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
import com.sitewhere.microservice.api.device.DeviceCommandInvocationMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.view.DeviceInvocationSummaryBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for command invocation operations.
 */
@Path("/invocations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "invocations")
public class CommandInvocations {

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(CommandInvocations.class);

    /**
     * Get a command invocation by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/id/{id}")
    @ApiOperation(value = "Get command invocation by unique id.")
    public Response getDeviceCommandInvocation(@ApiParam(value = "Unique id", required = true) @PathParam("id") UUID id)
	    throws SiteWhereException {
	IDeviceEvent found = getDeviceEventManagement().getDeviceEventById(id);
	if (!(found instanceof IDeviceCommandInvocation)) {
	    throw new SiteWhereException("Event with the corresponding id is not a command invocation.");
	}
	DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper(getDeviceManagement());
	return Response.ok(helper.convert((IDeviceCommandInvocation) found)).build();
    }

    /**
     * Get a summarized version of the given device command invocation.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/id/{id}/summary")
    @ApiOperation(value = "Get command invocation summary")
    public Response getDeviceCommandInvocationSummary(
	    @ApiParam(value = "Unique id", required = true) @PathParam("id") UUID id) throws SiteWhereException {
	IDeviceEvent found = getDeviceEventManagement().getDeviceEventById(id);
	if (!(found instanceof IDeviceCommandInvocation)) {
	    throw new SiteWhereException("Event with the corresponding id is not a command invocation.");
	}
	IDeviceCommandInvocation invocation = (IDeviceCommandInvocation) found;
	DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper(getDeviceManagement());
	helper.setIncludeCommand(true);
	MarshaledDeviceCommandInvocation converted = helper.convert(invocation);
	ISearchResults<IDeviceCommandResponse> responses = getDeviceEventManagement()
		.listDeviceCommandInvocationResponses(found.getId());
	return Response.ok(DeviceInvocationSummaryBuilder.build(converted, responses.getResults(),
		getDeviceManagement(), getDeviceEventManagement())).build();
    }

    /**
     * List all responses for a command invocation.
     * 
     * @param invocationId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/id/{invocationId}/responses")
    @ApiOperation(value = "List responses for command invocation")
    public Response listCommandInvocationResponses(
	    @ApiParam(value = "Invocation id", required = true) @PathParam("invocationId") UUID invocationId)
	    throws SiteWhereException {
	return Response.ok(getDeviceEventManagement().listDeviceCommandInvocationResponses(invocationId)).build();
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(getMicroservice().getDeviceEventManagementApiChannel());
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}