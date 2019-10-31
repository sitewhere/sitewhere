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
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.communication.protobuf.DeviceTypeProtoBuilder;
import com.sitewhere.device.marshaling.DeviceTypeMarshalHelper;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStatusCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.label.ILabelGeneration;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for device specification operations.
 * 
 * @author Derek Adams
 */
@Path("/devicetypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "devicetypes")
public class DeviceTypes {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceTypes.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a device type.
     * 
     * @param request
     * @return
     */
    @POST
    @ApiOperation(value = "Create new device type")
    public Response createDeviceType(@RequestBody DeviceTypeCreateRequest request) throws SiteWhereException {
	IDeviceType result = getDeviceManagement().createDeviceType(request);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getCachedDeviceManagement());
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
    @ApiOperation(value = "Get device type by unique token")
    public Response getDeviceTypeByToken(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Include detailed asset information", required = false) @QueryParam("includeAsset") @DefaultValue("true") boolean includeAsset)
	    throws SiteWhereException {
	IDeviceType result = assertDeviceTypeByToken(token);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getCachedDeviceManagement());
	return Response.ok(helper.convert(result)).build();
    }

    /**
     * Get default protobuf definition for device type.
     * 
     * @param token
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/proto")
    @Produces("text/plain")
    @ApiOperation(value = "Get specification GPB by unique token")
    public Response getDeviceTypeProtoByToken(
	    @ApiParam(value = "Token", required = true) @PathParam("token") String token) throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	return Response.ok(DeviceTypeProtoBuilder.getProtoForDeviceType(deviceType, getDeviceManagement())).build();
    }

    /**
     * Get default protobuf definition file for device type.
     * 
     * @param hardwareId
     * @return
     */
    @GET
    @Path("/{token}/spec.proto")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ApiOperation(value = "Get device type GPB file by unique token")
    public Response getDeviceTypeProtoFileByToken(
	    @ApiParam(value = "Token", required = true) @PathParam("token") String token) throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	String proto = DeviceTypeProtoBuilder.getProtoForDeviceType(deviceType, getDeviceManagement());

	return Response.ok(proto)
		.header("Content-Disposition", "attachment; filename=Spec_" + deviceType.getToken() + ".proto").build();
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
    @ApiOperation(value = "Update existing device type")
    public Response updateDeviceType(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
	    @RequestBody DeviceTypeCreateRequest request) throws SiteWhereException {
	IDeviceType deviceType = assertDeviceTypeByToken(token);
	IDeviceType result = getDeviceManagement().updateDeviceType(deviceType.getId(), request);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getCachedDeviceManagement());
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
    @ApiOperation(value = "Get label for device type")
    public Response getDeviceTypeLabel(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Generator id", required = true) @PathParam("generatorId") String generatorId)
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
    @ApiOperation(value = "List device types that match criteria")
    public Response listDeviceTypes(
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<IDeviceType> results = getDeviceManagement().listDeviceTypes(criteria);
	DeviceTypeMarshalHelper helper = new DeviceTypeMarshalHelper(getCachedDeviceManagement());
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
    @ApiOperation(value = "Delete existing device type")
    public Response deleteDeviceType(@ApiParam(value = "Token", required = true) @PathParam("token") String token)
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
    @ApiOperation(value = "Create device command.")
    public Response createDeviceCommand(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
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
    @ApiOperation(value = "Get device command by unique token")
    public Response getDeviceCommandByToken(
	    @ApiParam(value = "Token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Command Token", required = true) @PathParam("commandToken") String commandToken)
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
    @ApiOperation(value = "Update an existing device command")
    public Response updateDeviceCommand(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Command Token", required = true) @PathParam("commandToken") String commandToken,
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
    @ApiOperation(value = "Delete device command by unique token")
    public Response deleteDeviceCommand(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Command Token", required = true) @PathParam("commandToken") String commandToken)
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
    @ApiOperation(value = "Create device status.")
    public Response createDeviceStatus(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
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
    @ApiOperation(value = "Get device status by unique token")
    public Response getDeviceStatusByToken(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Status Token", required = true) @PathParam("statusToken") String statusToken)
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
    @ApiOperation(value = "Update an existing device command")
    public Response updateDeviceStatus(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Status Token", required = true) @PathParam("statusToken") String statusToken,
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
    @ApiOperation(value = "Delete device command by unique token")
    public Response deleteDeviceStatus(@ApiParam(value = "Token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Status Token", required = true) @PathParam("statusToken") String statusToken)
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
	return getMicroservice().getDeviceManagementApiChannel();
    }

    protected IDeviceManagement getCachedDeviceManagement() {
	return getMicroservice().getCachedDeviceManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}