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
import java.util.UUID;

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

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.DeviceGroupElementMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceGroupMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for device group operations.
 */
@Path("/devicegroups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "devicegroups")
public class DeviceGroups {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceGroups.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a device group.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create new device group")
    public Response createDeviceGroup(@RequestBody DeviceGroupCreateRequest request) throws SiteWhereException {
	return Response.ok(getDeviceManagement().createDeviceGroup(request)).build();
    }

    /**
     * Get device group by unique token.
     * 
     * @param groupToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{groupToken}")
    @ApiOperation(value = "Get a device group by unique token")
    public Response getDeviceGroupByToken(
	    @ApiParam(value = "Unique token that identifies group", required = true) @PathParam("groupToken") String groupToken)
	    throws SiteWhereException {
	return Response.ok(assureDeviceGroup(groupToken)).build();
    }

    /**
     * Update an existing device group.
     * 
     * @param groupToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{groupToken}")
    @ApiOperation(value = "Update an existing device group")
    public Response updateDeviceGroup(
	    @ApiParam(value = "Device group token", required = true) @PathParam("groupToken") String groupToken,
	    @RequestBody DeviceGroupCreateRequest request) throws SiteWhereException {
	IDeviceGroup group = assureDeviceGroup(groupToken);
	return Response.ok(getDeviceManagement().updateDeviceGroup(group.getId(), request)).build();
    }

    /**
     * Get label for device group based on a specific generator.
     * 
     * @param groupToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{groupToken}/label/{generatorId}")
    @Produces("image/png")
    @ApiOperation(value = "Get label for area")
    public Response getDeviceGroupLabel(
	    @ApiParam(value = "Device group token", required = true) @PathParam("groupToken") String groupToken,
	    @ApiParam(value = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	IDeviceGroup group = assureDeviceGroup(groupToken);
	ILabel label = getLabelGeneration().getDeviceGroupLabel(generatorId, group.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * Delete an existing device group.
     * 
     * @param groupToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{groupToken}")
    @ApiOperation(value = "Delete device group by unique token")
    public Response deleteDeviceGroup(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathParam("groupToken") String groupToken)
	    throws SiteWhereException {
	IDeviceGroup group = assureDeviceGroup(groupToken);
	return Response.ok(getDeviceManagement().deleteDeviceGroup(group.getId())).build();
    }

    /**
     * List device groups that match criteria.
     * 
     * @param role
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @ApiOperation(value = "List device groups that match criteria")
    public Response listDeviceGroups(@ApiParam(value = "Role", required = false) @QueryParam("role") String role,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends IDeviceGroup> results;
	if (role == null) {
	    results = getDeviceManagement().listDeviceGroups(criteria);
	} else {
	    results = getDeviceManagement().listDeviceGroupsWithRole(role, criteria);
	}
	DeviceGroupMarshalHelper helper = new DeviceGroupMarshalHelper();
	List<IDeviceGroup> groupsConv = new ArrayList<IDeviceGroup>();
	for (IDeviceGroup group : results.getResults()) {
	    groupsConv.add(helper.convert(group));
	}
	return Response.ok(new SearchResults<IDeviceGroup>(groupsConv, results.getNumResults())).build();
    }

    /**
     * List elements from a device group that meet the given criteria.
     * 
     * @param groupToken
     * @param includeDetails
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{groupToken}/elements")
    @ApiOperation(value = "List elements in a device group")
    public Response listDeviceGroupElements(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathParam("groupToken") String groupToken,
	    @ApiParam(value = "Include detailed element information", required = false) @QueryParam("includeDetails") @DefaultValue("false") boolean includeDetails,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(getDeviceManagement())
		.setIncludeDetails(includeDetails);
	SearchCriteria criteria = new SearchCriteria(page, pageSize);

	IDeviceGroup group = assureDeviceGroup(groupToken);
	ISearchResults<? extends IDeviceGroupElement> results = getDeviceManagement()
		.listDeviceGroupElements(group.getId(), criteria);
	List<IDeviceGroupElement> elmConv = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElement elm : results.getResults()) {
	    elmConv.add(helper.convert(elm, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceGroupElement>(elmConv, results.getNumResults())).build();
    }

    /**
     * Add a list of device group elements to an existing group.
     * 
     * @param groupToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{groupToken}/elements")
    @SuppressWarnings("unchecked")
    @ApiOperation(value = "Add elements to device group")
    public Response addDeviceGroupElements(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathParam("groupToken") String groupToken,
	    @RequestBody List<DeviceGroupElementCreateRequest> request) throws SiteWhereException {
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(getDeviceManagement())
		.setIncludeDetails(false);
	List<IDeviceGroupElementCreateRequest> elements = (List<IDeviceGroupElementCreateRequest>) (List<? extends IDeviceGroupElementCreateRequest>) request;

	// Validate the list of new elements.
	validateDeviceGroupElements(request, getDeviceManagement());

	IDeviceGroup group = assureDeviceGroup(groupToken);
	List<? extends IDeviceGroupElement> results = getDeviceManagement().addDeviceGroupElements(group.getId(),
		elements, true);
	List<IDeviceGroupElement> converted = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElement elm : results) {
	    converted.add(helper.convert(elm, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceGroupElement>(converted)).build();
    }

    /**
     * Validate new elements to assure they reference real objects.
     * 
     * @param elements
     * @param deviceManagement
     * @throws SiteWhereException
     */
    protected void validateDeviceGroupElements(List<DeviceGroupElementCreateRequest> elements,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	for (DeviceGroupElementCreateRequest request : elements) {
	    if (request.getDeviceToken() != null) {
		IDevice device = deviceManagement.getDeviceByToken(request.getDeviceToken());
		if (device == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
		}
	    }
	    if (request.getNestedGroupToken() != null) {
		IDeviceGroup group = deviceManagement.getDeviceGroupByToken(request.getNestedGroupToken());
		if (group == null) {
		    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
		}
	    }
	}
    }

    /**
     * Delete a single device group element.
     * 
     * @param groupToken
     * @param elementId
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{groupToken}/elements/{elementId}")
    @ApiOperation(value = "Delete elements from device group")
    public Response deleteDeviceGroupElement(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathParam("groupToken") String groupToken,
	    @ApiParam(value = "Element id", required = true) @PathParam("elementId") UUID elementId)
	    throws SiteWhereException {
	List<UUID> elements = new ArrayList<>();
	elements.add(elementId);
	return Response.ok(deleteDeviceGroupElements(groupToken, elements)).build();
    }

    /**
     * Delete a list of elements from an existing device group.
     * 
     * @param groupToken
     * @param elementIds
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{groupToken}/elements")
    @ApiOperation(value = "Delete elements from device group")
    public Response deleteDeviceGroupElements(
	    @ApiParam(value = "Unique token that identifies device group", required = true) @PathParam("groupToken") String groupToken,
	    @RequestBody List<UUID> elementIds) throws SiteWhereException {
	DeviceGroupElementMarshalHelper helper = new DeviceGroupElementMarshalHelper(getDeviceManagement())
		.setIncludeDetails(false);

	List<? extends IDeviceGroupElement> results = getDeviceManagement().removeDeviceGroupElements(elementIds);
	List<IDeviceGroupElement> converted = new ArrayList<IDeviceGroupElement>();
	for (IDeviceGroupElement elm : results) {
	    converted.add(helper.convert(elm, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceGroupElement>(converted)).build();
    }

    /**
     * Assure that a device group exists for the given token.
     * 
     * @param groupToken
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceGroup assureDeviceGroup(String groupToken) throws SiteWhereException {
	IDeviceGroup group = getDeviceManagement().getDeviceGroupByToken(groupToken);
	if (group == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
	}
	return group;
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagementApiChannel();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}