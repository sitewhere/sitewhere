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

/**
 * Controller for command invocation operations.
 */
@Path("/api/invocations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "invocations")
@Tag(name = "Command Invocations", description = "Services used to provide more information about command invocation events.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class CommandInvocations {

    @Inject
    private IInstanceManagementMicroservice microservice;

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
    @Operation(summary = "Get command invocation by unique id", description = "Get command invocation by unique id")
    public Response getDeviceCommandInvocation(
	    @Parameter(description = "Unique id", required = true) @PathParam("id") UUID id) throws SiteWhereException {
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
    @Operation(summary = "Get command invocation summary", description = "Get command invocation summary")
    public Response getDeviceCommandInvocationSummary(
	    @Parameter(description = "Unique id", required = true) @PathParam("id") UUID id) throws SiteWhereException {
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
    @Operation(summary = "List responses for command invocation", description = "List responses for command invocation")
    public Response listCommandInvocationResponses(
	    @Parameter(description = "Invocation id", required = true) @PathParam("invocationId") UUID invocationId)
	    throws SiteWhereException {
	return Response.ok(getDeviceEventManagement().listDeviceCommandInvocationResponses(invocationId)).build();
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}