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
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.device.DeviceTypeMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStatusCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;

/**
 * Controller for device specification operations.
 */
@Path("/api/devicetypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "devicetypes")
@Tag(name = "Device Types", description = "Device types define common characteristics for related devices.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class DeviceTypes {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceTypes.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a device type.
     * 
     * @param request
     * @return
     */
    @POST
    @Operation(summary = "Create new device type", description = "Create new device type")
    public Response createDeviceType(@RequestBody DeviceTypeCreateRequest request) throws SiteWhereException {
	IDeviceType result = getDeviceManagement().createDeviceType(request);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return Response.ok(helper.convert(result)).build();
    }

    /**
     * Get a device type by unique token.
     * 
     * @param token
     * @param includeAsset
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}")
    @Operation(summary = "Get device type by unique token", description = "Get device type by unique token")
    public Response getDeviceTypeByToken(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Include detailed asset information", required = false) @QueryParam("includeAsset") @DefaultValue("true") boolean includeAsset)
	    throws SiteWhereException {
	IDeviceType result = assertDeviceTypeByToken(token);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return Response.ok(helper.convert(result)).build();
    }

    /**
     * Update an existing device type.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{token}")
    @Operation(summary = "Update existing device type", description = "Update existing device type")
    public Response updateDeviceType(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @RequestBody DeviceTypeCreateRequest request) throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	IDeviceType result = getDeviceManagement().updateDeviceType(deviceType.getId(), request);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return Response.ok(helper.convert(result)).build();
    }

    /**
     * Get label for device type based on a specific generator.
     * 
     * @param token
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/label/{generatorId}")
    @Operation(summary = "Get label for device type", description = "Get label for device type")
    public Response getDeviceTypeLabel(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	ILabel label = getLabelGeneration().getDeviceTypeLabel(generatorId, deviceType.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * List device types that meet the given criteria.
     * 
     * @param token
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List device types that match criteria", description = "List device types that match criteria")
    public Response listDeviceTypes(
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends IDeviceType> results = getDeviceManagement().listDeviceTypes(criteria);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	List<IDeviceType> typesConv = new ArrayList<IDeviceType>();
	for (IDeviceType type : results.getResults()) {
	    typesConv.add(helper.convert(type));
	}
	return Response.ok(new SearchResults<IDeviceType>(typesConv, results.getNumResults())).build();
    }

    /**
     * Delete an existing device type.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{token}")
    @Operation(summary = "Delete existing device type", description = "Delete existing device type")
    public Response deleteDeviceType(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceType result = getDeviceManagement().deleteDeviceType(existing.getId());
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getDeviceManagement());
	return Response.ok(helper.convert(result)).build();
    }

    /**
     * Create a device command.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/commands")
    @Operation(summary = "Create device command", description = "Create device command")
    public Response createDeviceCommand(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @RequestBody DeviceCommandCreateRequest request) throws SiteWhereException {
	return Response.ok(getDeviceManagement().createDeviceCommand(request)).build();
    }

    /**
     * Get a device command by unique token.
     * 
     * @param token
     * @param commandToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/commands/{commandToken}")
    @Operation(summary = "Get device command by unique token", description = "Get device command by unique token")
    public Response getDeviceCommandByToken(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Command Token", required = true) @PathParam("commandToken") String commandToken)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(existing.getId(), commandToken);
	return Response.ok(command).build();
    }

    /**
     * Update an existing device command.
     * 
     * @param token
     * @param commandToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{token}/commands/{commandToken}")
    @Operation(summary = "Update an existing device command", description = "Update an existing device command")
    public Response updateDeviceCommand(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Command Token", required = true) @PathParam("commandToken") String commandToken,
	    @RequestBody DeviceCommandCreateRequest request) throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(existing.getId(), commandToken);
	return Response.ok(getDeviceManagement().updateDeviceCommand(command.getId(), request)).build();
    }

    /**
     * Delete an existing device command.
     * 
     * @param token
     * @param commandToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{token}/commands/{commandToken}")
    @Operation(summary = "Delete device command", description = "Delete device command by unique token")
    public Response deleteDeviceCommand(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Command Token", required = true) @PathParam("commandToken") String commandToken)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(existing.getId(), commandToken);
	return Response.ok(getDeviceManagement().deleteDeviceCommand(command.getId())).build();
    }

    /**
     * Create a device status.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/statuses")
    @Operation(summary = "Create device status", description = "Create device status")
    public Response createDeviceStatus(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @RequestBody DeviceStatusCreateRequest request) throws SiteWhereException {
	return Response.ok(getDeviceManagement().createDeviceStatus(request)).build();
    }

    /**
     * Get device status by unique token.
     * 
     * @param token
     * @param statusToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/statuses/{statusToken}")
    @Operation(summary = "Get device status by unique token", description = "Get device status by unique token")
    public Response getDeviceStatusByToken(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Status Token", required = true) @PathParam("statusToken") String statusToken)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceStatus status = getDeviceManagement().getDeviceStatusByToken(existing.getId(), statusToken);
	return Response.ok(status).build();
    }

    /**
     * Update an existing device status.
     * 
     * @param token
     * @param statusToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{token}/statuses/{statusToken}")
    @Operation(summary = "Update an existing device status", description = "Update an existing device status")
    public Response updateDeviceStatus(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Status Token", required = true) @PathParam("statusToken") String statusToken,
	    @RequestBody DeviceStatusCreateRequest request) throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceStatus status = getDeviceManagement().getDeviceStatusByToken(existing.getId(), statusToken);
	return Response.ok(getDeviceManagement().updateDeviceStatus(status.getId(), request)).build();
    }

    /**
     * Delete an existing device command.
     * 
     * @param token
     * @param statusToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{token}/statuses/{statusToken}")
    @Operation(summary = "Delete device status", description = "Delete device status by unique token")
    public Response deleteDeviceStatus(
	    @Parameter(description = "Token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Status Token", required = true) @PathParam("statusToken") String statusToken)
	    throws SiteWhereException {
	IDeviceType existing = assertDeviceTypeByToken(token);
	IDeviceStatus status = getDeviceManagement().getDeviceStatusByToken(existing.getId(), statusToken);
	return Response.ok(getDeviceManagement().deleteDeviceStatus(status.getId())).build();
    }

    /**
     * Gets a device type by token and throws an exception if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceType assertDeviceTypeByToken(String token) throws SiteWhereException {
	IDeviceType result = getDeviceManagement().getDeviceTypeByToken(token);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceTypeToken, ErrorLevel.ERROR);
	}
	return result;
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}