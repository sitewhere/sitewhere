/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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

import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.DeviceAssignmentMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceGroupUtils;
import com.sitewhere.microservice.api.device.DeviceMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceAssignment;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for device operations.
 */
@Path("/api/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "devices")
public class Devices {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Devices.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a device.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create new device")
    public Response createDevice(@RequestBody DeviceCreateRequest request) throws SiteWhereException {
	IDevice result = getDeviceManagement().createDevice(request);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignment(false);
	return Response.ok(helper.convert(result, getAssetManagement())).build();
    }

    /**
     * Get device by unique token.
     * 
     * @param deviceToken
     * @param includeDeviceType
     * @param includeAssignment
     * @param includeNested
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{deviceToken}")
    @ApiOperation(value = "Get device by token")
    public Response getDeviceByToken(
	    @ApiParam(value = "Device token", required = true) @PathParam("deviceToken") String deviceToken,
	    @ApiParam(value = "Include device type information", required = false) @QueryParam("includeDeviceType") @DefaultValue("true") boolean includeDeviceType,
	    @ApiParam(value = "Include assignment if associated", required = false) @QueryParam("includeAssignment") @DefaultValue("true") boolean includeAssignment,
	    @ApiParam(value = "Include detailed nested device information", required = false) @QueryParam("includeNested") @DefaultValue("false") boolean includeNested)
	    throws SiteWhereException {
	IDevice result = assertDeviceByToken(deviceToken);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignment(includeAssignment);
	helper.setIncludeNested(includeNested);
	return Response.ok(helper.convert(result, getAssetManagement())).build();
    }

    /**
     * Update existing device.
     * 
     * @param deviceToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{deviceToken}")
    @ApiOperation(value = "Update an existing device")
    public Response updateDevice(
	    @ApiParam(value = "Device token", required = true) @PathParam("deviceToken") String deviceToken,
	    @RequestBody DeviceCreateRequest request) throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	IDevice result = getDeviceManagement().updateDevice(existing.getId(), request);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignment(true);
	return Response.ok(helper.convert(result, getAssetManagement())).build();
    }

    /**
     * Get label for device based on a specific generator.
     * 
     * @param deviceToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{deviceToken}/label/{generatorId}")
    @Produces("image/png")
    @ApiOperation(value = "Get label for device")
    public Response getDeviceLabel(
	    @ApiParam(value = "Device token", required = true) @PathParam("deviceToken") String deviceToken,
	    @ApiParam(value = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	ILabel label = getLabelGeneration().getDeviceLabel(generatorId, existing.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * Delete device identified by token.
     * 
     * @param deviceToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{deviceToken}")
    @ApiOperation(value = "Delete device based on unique hardware id")
    public Response deleteDevice(
	    @ApiParam(value = "Device token", required = true) @PathParam("deviceToken") String deviceToken)
	    throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	IDevice result = getDeviceManagement().deleteDevice(existing.getId());
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignment(true);
	return Response.ok(helper.convert(result, getAssetManagement())).build();
    }

    /**
     * List active assignments for a given device.
     * 
     * @param deviceToken
     * @param includeDevice
     * @param includeCustomer
     * @param includeArea
     * @param includeAsset
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{deviceToken}/assignments/active")
    @ApiOperation(value = "Get active assignments for device")
    public Response getActiveDeviceAssignments(
	    @ApiParam(value = "Device token", required = true) @PathParam("deviceToken") String deviceToken,
	    @ApiParam(value = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @ApiParam(value = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @ApiParam(value = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @ApiParam(value = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset)
	    throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	List<? extends IDeviceAssignment> assignments = getDeviceManagement()
		.getActiveDeviceAssignments(existing.getId());
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);

	List<MarshaledDeviceAssignment> converted = new ArrayList<>();
	for (IDeviceAssignment assignment : assignments) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}

	return Response.ok(converted).build();
    }

    /**
     * List device assignment history for a given device.
     * 
     * @param deviceToken
     * @param includeDevice
     * @param includeCustomer
     * @param includeArea
     * @param includeAsset
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{deviceToken}/assignments")
    @ApiOperation(value = "List assignment history for device")
    public Response listDeviceAssignmentHistory(
	    @ApiParam(value = "Device token", required = true) @PathParam("deviceToken") String deviceToken,
	    @ApiParam(value = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @ApiParam(value = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @ApiParam(value = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @ApiParam(value = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	// Create search criteria.
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	criteria.setDeviceTokens(Collections.singletonList(deviceToken));

	ISearchResults<? extends IDeviceAssignment> history = getDeviceManagement().listDeviceAssignments(criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);

	List<IDeviceAssignment> converted = new ArrayList<IDeviceAssignment>();
	for (IDeviceAssignment assignment : history.getResults()) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceAssignment>(converted, history.getNumResults())).build();
    }

    /**
     * Create a device element mapping.
     * 
     * @param deviceToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{deviceToken}/mappings")
    @ApiOperation(value = "Create new device element mapping")
    public Response addDeviceElementMapping(
	    @ApiParam(value = "Device token", required = true) @PathParam("deviceToken") String deviceToken,
	    @RequestBody DeviceElementMapping request) throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	IDevice updated = getDeviceManagement().createDeviceElementMapping(existing.getId(), request);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignment(false);
	return Response.ok(helper.convert(updated, getAssetManagement())).build();
    }

    /**
     * Delete a device element mapping.
     * 
     * @param deviceToken
     * @param path
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{deviceToken}/mappings/{path}")
    @ApiOperation(value = "Delete existing device element mapping")
    public Response deleteDeviceElementMapping(
	    @ApiParam(value = "Device token", required = true) @PathParam("deviceToken") String deviceToken,
	    @ApiParam(value = "Device element path", required = true) @PathParam("path") String path)
	    throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	IDevice updated = getDeviceManagement().deleteDeviceElementMapping(existing.getId(), path);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignment(false);
	return Response.ok(helper.convert(updated, getAssetManagement())).build();
    }

    /**
     * List devices that match given criteria.
     * 
     * @param deviceType
     * @param excludeAssigned
     * @param includeDeviceType
     * @param includeAssignment
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @ApiOperation(value = "List devices that match criteria")
    public Response listDevices(
	    @ApiParam(value = "Device type filter", required = false) @QueryParam("deviceType") String deviceType,
	    @ApiParam(value = "Exclude assigned devices", required = false) @QueryParam("excludeAssigned") @DefaultValue("false") boolean excludeAssigned,
	    @ApiParam(value = "Include device type information", required = false) @QueryParam("includeDeviceType") @DefaultValue("false") boolean includeDeviceType,
	    @ApiParam(value = "Include assignment information if associated", required = false) @QueryParam("includeAssignment") @DefaultValue("false") boolean includeAssignment,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(deviceType, excludeAssigned, page, pageSize,
		Assignments.parseDateOrFail(startDate), Assignments.parseDateOrFail(endDate));
	ISearchResults<? extends IDevice> results = getDeviceManagement().listDevices(criteria);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignment(includeAssignment);
	List<IDevice> devicesConv = new ArrayList<IDevice>();
	for (IDevice device : results.getResults()) {
	    devicesConv.add(helper.convert(device, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDevice>(devicesConv, results.getNumResults())).build();
    }

    /**
     * List devices assigned to a group.
     * 
     * @param groupToken
     * @param deviceType
     * @param includeDeleted
     * @param excludeAssigned
     * @param includeDeviceType
     * @param includeAssignment
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/group/{groupToken}")
    @ApiOperation(value = "List devices in device group")
    public Response listDevicesForGroup(
	    @ApiParam(value = "Group token", required = true) @PathParam("groupToken") String groupToken,
	    @ApiParam(value = "Device type filter", required = false) @QueryParam("deviceType") String deviceType,
	    @ApiParam(value = "Include deleted devices", required = false) @QueryParam("includeDeleted") @DefaultValue("false") boolean includeDeleted,
	    @ApiParam(value = "Exclude assigned devices", required = false) @QueryParam("excludeAssigned") @DefaultValue("false") boolean excludeAssigned,
	    @ApiParam(value = "Include device type information", required = false) @QueryParam("includeDeviceType") @DefaultValue("false") boolean includeDeviceType,
	    @ApiParam(value = "Include assignment information if associated", required = false) @QueryParam("includeAssignment") @DefaultValue("false") boolean includeAssignment,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(deviceType, excludeAssigned, page, pageSize,
		Assignments.parseDateOrFail(startDate), Assignments.parseDateOrFail(endDate));
	IDeviceGroup group = assertDeviceGroup(groupToken);
	List<IDevice> matches = DeviceGroupUtils.getDevicesInGroup(group, criteria, getDeviceManagement(),
		getAssetManagement());
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignment(includeAssignment);
	List<IDevice> devicesConv = new ArrayList<IDevice>();
	for (IDevice device : matches) {
	    devicesConv.add(helper.convert(device, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDevice>(devicesConv, matches.size())).build();
    }

    /**
     * List devices in groups with role.
     * 
     * @param role
     * @param deviceType
     * @param includeDeleted
     * @param excludeAssigned
     * @param includeDeviceType
     * @param includeAssignment
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/grouprole/{role}")
    @ApiOperation(value = "List devices in device groups with role")
    public Response listDevicesForGroupsWithRole(
	    @ApiParam(value = "Group role", required = true) @PathParam("role") String role,
	    @ApiParam(value = "Device type filter", required = false) @QueryParam("deviceType") String deviceType,
	    @ApiParam(value = "Include deleted devices", required = false) @QueryParam("includeDeleted") @DefaultValue("false") boolean includeDeleted,
	    @ApiParam(value = "Exclude assigned devices", required = false) @QueryParam("excludeAssigned") @DefaultValue("false") boolean excludeAssigned,
	    @ApiParam(value = "Include device type information", required = false) @QueryParam("includeDeviceType") @DefaultValue("false") boolean includeDeviceType,
	    @ApiParam(value = "Include assignment information if associated", required = false) @QueryParam("includeAssignment") @DefaultValue("false") boolean includeAssignment,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(deviceType, excludeAssigned, page, pageSize,
		Assignments.parseDateOrFail(startDate), Assignments.parseDateOrFail(endDate));
	Collection<IDevice> matches = DeviceGroupUtils.getDevicesInGroupsWithRole(role, criteria, getDeviceManagement(),
		getAssetManagement());
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignment(includeAssignment);
	List<IDevice> devicesConv = new ArrayList<IDevice>();
	for (IDevice device : matches) {
	    devicesConv.add(helper.convert(device, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDevice>(devicesConv, matches.size())).build();
    }

    /**
     * Add a batch of events for the current assignment of the given device. Note
     * that the token in the URL overrides the one specified in the
     * {@link DeviceEventBatch} object.
     * 
     * @param deviceToken
     * @param batch
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{deviceToken}/batch")
    @ApiOperation(value = "Add multiple events for device")
    public Response addDeviceEventBatch(
	    @ApiParam(value = "Device token", required = true) @PathParam("deviceToken") String deviceToken,
	    @RequestBody DeviceEventBatch batch) throws SiteWhereException {
	IDevice device = assertDeviceByToken(deviceToken);
	List<? extends IDeviceAssignment> active = getDeviceManagement().getActiveDeviceAssignments(device.getId());
	if (active.size() == 0) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.ERROR);
	}
	List<? extends IDeviceAssignment> assignments = getDeviceManagement()
		.getActiveDeviceAssignments(device.getId());

	IDeviceEventBatchResponse response = null;
	for (IDeviceAssignment assignment : assignments) {
	    // Set event dates if not set by client.
	    for (IDeviceLocationCreateRequest locReq : batch.getLocations()) {
		if (locReq.getEventDate() == null) {
		    ((DeviceLocationCreateRequest) locReq).setEventDate(new Date());
		}
	    }
	    for (IDeviceMeasurementCreateRequest measReq : batch.getMeasurements()) {
		if (measReq.getEventDate() == null) {
		    ((DeviceMeasurementCreateRequest) measReq).setEventDate(new Date());
		}
	    }
	    for (IDeviceAlertCreateRequest alertReq : batch.getAlerts()) {
		if (alertReq.getEventDate() == null) {
		    ((DeviceAlertCreateRequest) alertReq).setEventDate(new Date());
		}
	    }

	    response = getDeviceEventManagement().addDeviceEventBatch(assignment.getId(), batch);
	}

	// TODO: Only returns the last response. Should this be refactored?
	return Response.ok(response).build();
    }

    /**
     * Gets a device by unique token and throws an exception if not found.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected IDevice assertDeviceByToken(String token) throws SiteWhereException {
	IDevice result = getDeviceManagement().getDeviceByToken(token);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
	}
	return result;
    }

    /**
     * Gets a device assignment by token and throws an exception if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignment(UUID id) throws SiteWhereException {
	IDeviceAssignment result = getDeviceManagement().getDeviceAssignment(id);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
	}
	return result;
    }

    /**
     * Gets a device group by token and throws an exception if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceGroup assertDeviceGroup(String token) throws SiteWhereException {
	IDeviceGroup result = getDeviceManagement().getDeviceGroupByToken(token);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceGroupToken, ErrorLevel.ERROR);
	}
	return result;
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(getMicroservice().getDeviceEventManagementApiChannel());
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}