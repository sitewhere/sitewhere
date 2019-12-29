/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.ChartBuilder;
import com.sitewhere.microservice.api.device.DeviceAssignmentMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceCommandInvocationMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.microservice.api.schedule.ScheduledJobHelper;
import com.sitewhere.microservice.util.DataUtils;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceStateChangeCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentBulkRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.charting.IChartSeries;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/*
 * Controller for assignment operations.
 */
@Path("/assignments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "assignments")
public class Assignments {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Assignments.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a device assignment.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create a new device assignment")
    public Response createDeviceAssignment(@RequestBody DeviceAssignmentCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment created = getDeviceManagement().createDeviceAssignment(request);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	return Response.ok(helper.convert(created, getAssetManagement())).build();
    }

    /**
     * Get device assignment by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}")
    @ApiOperation(value = "Get device assignment by token")
    public Response getDeviceAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(token);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	helper.setIncludeDeviceType(true);
	return Response.ok(helper.convert(assignment, getAssetManagement())).build();
    }

    /**
     * Delete an existing device assignment.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{token}")
    @ApiOperation(value = "Delete an existing device assignment")
    public Response deleteDeviceAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment existing = assertDeviceAssignment(token);
	IDeviceAssignment assignment = getDeviceManagement().deleteDeviceAssignment(existing.getId());
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	return Response.ok(helper.convert(assignment, getAssetManagement())).build();
    }

    /**
     * Update an existing device assignment.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{token}")
    @ApiOperation(value = "Update an existing device assignment")
    public Response updateDeviceAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @RequestBody DeviceAssignmentCreateRequest request) throws SiteWhereException {
	IDeviceAssignment existing = assertDeviceAssignment(token);
	IDeviceAssignment result = getDeviceManagement().updateDeviceAssignment(existing.getId(), request);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	return Response.ok(helper.convert(result, getAssetManagement())).build();
    }

    /**
     * Get label for assignment based on a specific generator.
     * 
     * @param token
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/label/{generatorId}")
    @Produces("image/png")
    @ApiOperation(value = "Get label for area")
    public Response getAssignmentLabel(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	IDeviceAssignment existing = assertDeviceAssignment(token);
	ILabel label = getLabelGeneration().getDeviceAssignmentLabel(generatorId, existing.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * List assignments matching criteria.
     * 
     * @param deviceToken
     * @param customerToken
     * @param areaToken
     * @param assetToken
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
    @ApiOperation(value = "List assignments matching criteria")
    public Response listAssignments(
	    @ApiParam(value = "Limit by device token", required = false) @QueryParam("deviceToken") String deviceToken,
	    @ApiParam(value = "Limit by customer token", required = false) @QueryParam("customerToken") String customerToken,
	    @ApiParam(value = "Limit by area token", required = false) @QueryParam("areaToken") String areaToken,
	    @ApiParam(value = "Limit by asset token", required = false) @QueryParam("assetToken") String assetToken,
	    @ApiParam(value = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @ApiParam(value = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @ApiParam(value = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @ApiParam(value = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	if (deviceToken != null) {
	    criteria.setDeviceTokens(Collections.singletonList(deviceToken));
	}

	// If limiting by customer, look up customer and contained customers.
	if (customerToken != null) {
	    List<String> customers = Customers.resolveCustomerTokensRecursive(customerToken, true,
		    getDeviceManagement());
	    criteria.setCustomerTokens(customers);
	}

	// If limiting by area, look up area and contained areas.
	if (areaToken != null) {
	    List<String> areas = Areas.resolveAreaTokensRecursive(areaToken, true, getDeviceManagement());
	    criteria.setAreaTokens(areas);
	}

	// If limiting by asset, look up asset.
	if (assetToken != null) {
	    criteria.setAssetTokens(Collections.singletonList(assetToken));
	}

	// Perform search.
	ISearchResults<? extends IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);

	List<IDeviceAssignment> results = new ArrayList<>();
	for (IDeviceAssignment assn : matches.getResults()) {
	    results.add(helper.convert(assn, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceAssignment>(results, matches.getNumResults())).build();
    }

    /**
     * Perform and advanced search of device assignments.
     * 
     * @param includeDevice
     * @param includeCustomer
     * @param includeArea
     * @param includeAsset
     * @param page
     * @param pageSize
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/search")
    @ApiOperation(value = "Search device assignments with advanced criteria")
    public Response searchDeviceAssignments(
	    @ApiParam(value = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @ApiParam(value = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @ApiParam(value = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @ApiParam(value = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") Integer page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") Integer pageSize,
	    @RequestBody DeviceAssignmentSearchCriteria criteria) throws SiteWhereException {
	// Allow request parameters to override paging criteria.
	if (page != null) {
	    criteria.setPageNumber(page);
	}
	if (pageSize != null) {
	    criteria.setPageSize(pageSize);
	}

	// Perform search.
	ISearchResults<? extends IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);

	List<IDeviceAssignment> results = new ArrayList<>();
	for (IDeviceAssignment assn : matches.getResults()) {
	    results.add(helper.convert(assn, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceAssignment>(results, matches.getNumResults())).build();
    }

    /**
     * List device measurement events for multiple assignments.
     * 
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param bulk
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/bulk/measurements")
    @ApiOperation(value = "List measurement events for multiple assignments")
    public Response listMeasurementsForAssignments(
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement())
		.listDeviceMeasurementsForIndex(DeviceEventIndex.Assignment, ids, criteria)).build();
    }

    /**
     * List device measurement events for a given assignment.
     * 
     * @param token
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/measurements")
    @ApiOperation(value = "List measurement events for device assignment")
    public Response listMeasurementsForAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement()).listDeviceMeasurementsForIndex(
		DeviceEventIndex.Assignment, Collections.singletonList(assignment.getId()), criteria)).build();
    }

    /**
     * List measurement events for multiple assignments as chart series data.
     * 
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param measurementIds
     * @param bulk
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/bulk/measurements/series")
    @ApiOperation(value = "List measurements for multiple assignments as chart series")
    public Response listMeasurementsForAssignmentsAsChartSeries(
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate,
	    @ApiParam(value = "Measurement Ids", required = false) @QueryParam("measurementIds") String[] measurementIds,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	Map<String, List<IChartSeries<Double>>> results = new HashMap<String, List<IChartSeries<Double>>>();
	for (String token : bulk.getDeviceAssignmentTokens()) {
	    IDeviceAssignment assignment = assertDeviceAssignment(token);
	    ISearchResults<IDeviceMeasurement> measurements = new BlockingDeviceEventManagement(
		    getDeviceEventManagement()).listDeviceMeasurementsForIndex(DeviceEventIndex.Assignment,
			    Collections.singletonList(assignment.getId()), criteria);
	    ChartBuilder builder = new ChartBuilder();
	    results.put(token, builder.process(measurements.getResults(), measurementIds));
	}
	return Response.ok(results).build();
    }

    /**
     * List device measurement events for a given assignment in chart series format.
     * 
     * @param token
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param measurementIds
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/measurements/series")
    @ApiOperation(value = "List assignment measurements as chart series")
    public Response listMeasurementsForAssignmentAsChartSeries(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate,
	    @ApiParam(value = "Measurement Ids", required = false) @QueryParam("measurementIds") String[] measurementIds)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	ISearchResults<IDeviceMeasurement> measurements = new BlockingDeviceEventManagement(getDeviceEventManagement())
		.listDeviceMeasurementsForIndex(DeviceEventIndex.Assignment,
			Collections.singletonList(assignment.getId()), criteria);
	ChartBuilder builder = new ChartBuilder();
	return Response.ok(builder.process(measurements.getResults(), measurementIds)).build();
    }

    /**
     * Create measurements to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/measurements")
    @ApiOperation(value = "Create measurements event for device assignment")
    public Response createMeasurements(@RequestBody DeviceMeasurementCreateRequest input,
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement())
		.addDeviceMeasurements(assignment.getId(), input).get(0)).build();
    }

    /**
     * List location events for multiple assignments.
     * 
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param bulk
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/bulk/locations")
    @ApiOperation(value = "List location events for device assignment")
    public Response listLocationsForAssignments(
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement())
		.listDeviceLocationsForIndex(DeviceEventIndex.Assignment, ids, criteria)).build();
    }

    /**
     * List device locations for a given assignment.
     * 
     * @param token
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/locations")
    @ApiOperation(value = "List location events for device assignment")
    public Response listLocationsForAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement()).listDeviceLocationsForIndex(
		DeviceEventIndex.Assignment, Collections.singletonList(assignment.getId()), criteria)).build();
    }

    /**
     * Create location to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/locations")
    @ApiOperation(value = "Create location event for device assignment")
    public Response createLocation(@RequestBody DeviceLocationCreateRequest input,
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement())
		.addDeviceLocations(assignment.getId(), input).get(0)).build();
    }

    /**
     * List alert events for multiple assignments.
     * 
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param bulk
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/bulk/alerts")
    @ApiOperation(value = "List alert events for device assignment")
    public Response listAlertsForAssignments(
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement())
		.listDeviceAlertsForIndex(DeviceEventIndex.Assignment, ids, criteria)).build();
    }

    /**
     * List device alerts for a given assignment.
     * 
     * @param token
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/alerts")
    @ApiOperation(value = "List alert events for device assignment")
    public Response listAlertsForAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement()).listDeviceAlertsForIndex(
		DeviceEventIndex.Assignment, Collections.singletonList(assignment.getId()), criteria)).build();
    }

    /**
     * Create alert to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/alerts")
    @ApiOperation(value = "Create alert event for device assignment")
    public Response createAlert(@RequestBody DeviceAlertCreateRequest input,
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement())
		.addDeviceAlerts(assignment.getId(), input).get(0)).build();
    }

    /**
     * Create a stream to be associated with a device assignment.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    // @PostMapping(value = "/{token}/streams")
    // @ApiOperation(value = "Create data stream for a device assignment")
    // public DeviceStream createDeviceStream(@RequestBody DeviceStreamCreateRequest
    // request,
    // @ApiParam(value = "Assignment token", required = true) @PathVariable String
    // token)
    // throws SiteWhereException {
    // IDeviceAssignment existing = assertDeviceAssignment(token);
    // IDeviceStream result =
    // getDeviceManagement().createDeviceStream(existing.getId(), request);
    // return DeviceStream.copy(result);
    // }

    /**
     * List device streams associated with an assignment.
     * 
     * @param token
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    // @GetMapping(value = "/{token}/streams")
    // @ApiOperation(value = "List data streams for device assignment")
    // public ISearchResults<IDeviceStream> listDeviceStreamsForAssignment(
    // @ApiParam(value = "Assignment token", required = true) @PathVariable String
    // token,
    // @ApiParam(value = "Page number", required = false) @RequestParam(required =
    // false, defaultValue = "1") int page,
    // @ApiParam(value = "Page size", required = false) @RequestParam(required =
    // false, defaultValue = "100") int pageSize,
    // @ApiParam(value = "Start date", required = false) @RequestParam(required =
    // false) String startDate,
    // @ApiParam(value = "End date", required = false) @RequestParam(required =
    // false) String endDate,
    // HttpServletResponse response) throws SiteWhereException {
    // IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page,
    // pageSize, startDate, endDate, response);
    // IDeviceAssignment existing = assertDeviceAssignment(token);
    // ISearchResults<IDeviceStream> matches =
    // getDeviceManagement().listDeviceStreams(existing.getId(), criteria);
    // List<IDeviceStream> converted = new ArrayList<IDeviceStream>();
    // for (IDeviceStream stream : matches.getResults()) {
    // converted.add(DeviceStream.copy(stream));
    // }
    // return new SearchResults<IDeviceStream>(converted);
    // }

    /**
     * Get an existing device stream associated with an assignment.
     * 
     * @param token
     * @param streamId
     * @return
     * @throws SiteWhereException
     */
    // @GetMapping(value = "/{token}/streams/{streamId:.+}", produces =
    // "application/json")
    // @ApiOperation(value = "Get device assignment data stream by id")
    // public DeviceStream getDeviceStream(
    // @ApiParam(value = "Assignment token", required = true) @PathVariable String
    // token,
    // @ApiParam(value = "Stream Id", required = true) @PathVariable String
    // streamId) throws SiteWhereException {
    // IDeviceAssignment existing = assertDeviceAssignment(token);
    // IDeviceStream result =
    // getDeviceManagement().getDeviceStream(existing.getId(), streamId);
    // if (result == null) {
    // throw new SiteWhereSystemException(ErrorCode.InvalidStreamId,
    // ErrorLevel.ERROR,
    // HttpServletResponse.SC_NOT_FOUND);
    // }
    // return DeviceStream.copy(result);
    // }

    /**
     * Create command invocation to be associated with a device assignment.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/invocations")
    @ApiOperation(value = "Create command invocation event for assignment")
    public Response createCommandInvocation(@RequestBody DeviceCommandInvocationCreateRequest request,
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceCommandInvocation result = new BlockingDeviceEventManagement(getDeviceEventManagement())
		.addDeviceCommandInvocations(assignment.getId(), request).get(0);
	DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper(getDeviceManagement());
	return Response.ok(helper.convert(result)).build();
    }

    /**
     * Schedule a command invocation.
     * 
     * @param request
     * @param token
     * @param scheduleToken
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/invocations/schedules/{scheduleToken}")
    @ApiOperation(value = "Schedule command invocation")
    public Response scheduleCommandInvocation(@RequestBody DeviceCommandInvocationCreateRequest request,
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Schedule token", required = true) @PathParam("scheduleToken") String scheduleToken)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	assureDeviceCommand(assignment.getDeviceTypeId(), request.getCommandToken());
	IScheduledJobCreateRequest job = ScheduledJobHelper.createCommandInvocationJob(token, request.getCommandToken(),
		request.getParameterValues(), scheduleToken);
	return Response.ok(getScheduleManagement().createScheduledJob(job)).build();
    }

    /**
     * List command invocation events for multiple assignments.
     * 
     * @param includeCommand
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param bulk
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/bulk/invocations")
    @ApiOperation(value = "List command invocation events for assignment")
    public Response listCommandInvocationsForAssignments(
	    @ApiParam(value = "Include command information", required = false) @QueryParam("includeCommand") @DefaultValue("true") boolean includeCommand,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	ISearchResults<IDeviceCommandInvocation> matches = new BlockingDeviceEventManagement(getDeviceEventManagement())
		.listDeviceCommandInvocationsForIndex(DeviceEventIndex.Assignment, ids, criteria);
	DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper(getDeviceManagement());
	helper.setIncludeCommand(includeCommand);
	List<IDeviceCommandInvocation> converted = new ArrayList<IDeviceCommandInvocation>();
	for (IDeviceCommandInvocation invocation : matches.getResults()) {
	    converted.add(helper.convert(invocation));
	}
	return Response.ok(new SearchResults<IDeviceCommandInvocation>(converted)).build();
    }

    /**
     * List device command invocations for a given assignment.
     * 
     * @param token
     * @param includeCommand
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/invocations")
    @ApiOperation(value = "List command invocation events for assignment")
    public Response listCommandInvocationsForAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Include command information", required = false) @QueryParam("includeCommand") @DefaultValue("true") boolean includeCommand,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	ISearchResults<IDeviceCommandInvocation> matches = new BlockingDeviceEventManagement(getDeviceEventManagement())
		.listDeviceCommandInvocationsForIndex(DeviceEventIndex.Assignment,
			Collections.singletonList(assignment.getId()), criteria);
	DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper(getDeviceManagement());
	helper.setIncludeCommand(includeCommand);
	List<IDeviceCommandInvocation> converted = new ArrayList<IDeviceCommandInvocation>();
	for (IDeviceCommandInvocation invocation : matches.getResults()) {
	    converted.add(helper.convert(invocation));
	}
	return Response.ok(new SearchResults<IDeviceCommandInvocation>(converted)).build();
    }

    /**
     * Create state change to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/statechanges")
    @ApiOperation(value = "Create an state change event for a device assignment")
    public Response createStateChange(@RequestBody DeviceStateChangeCreateRequest input,
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement())
		.addDeviceStateChanges(assignment.getId(), input).get(0)).build();
    }

    /**
     * List state change events for multiple assignments.
     * 
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param bulk
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/bulk/statechanges")
    @ApiOperation(value = "List state change events for a device assignment")
    public Response listStateChangesForAssignments(
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement())
		.listDeviceStateChangesForIndex(DeviceEventIndex.Assignment, ids, criteria)).build();
    }

    /**
     * List device state changes for a given assignment.
     * 
     * @param token
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/statechanges")
    @ApiOperation(value = "List state change events for a device assignment")
    public Response listStateChangesForAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement()).listDeviceStateChangesForIndex(
		DeviceEventIndex.Assignment, Collections.singletonList(assignment.getId()), criteria)).build();
    }

    /**
     * Create command response to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/responses")
    @ApiOperation(value = "Create command response event for assignment")
    public Response createCommandResponse(@RequestBody DeviceCommandResponseCreateRequest input,
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceCommandResponse result = new BlockingDeviceEventManagement(getDeviceEventManagement())
		.addDeviceCommandResponses(assignment.getId(), input).get(0);
	return Response.ok(DeviceCommandResponse.copy(result)).build();
    }

    /**
     * List device command responses for mulitple assignments.
     * 
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param bulk
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/bulk/responses")
    @ApiOperation(value = "List command response events for assignment")
    public Response listCommandResponsesForAssignments(
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return Response.ok(new BlockingDeviceEventManagement(getDeviceEventManagement())
		.listDeviceCommandResponsesForIndex(DeviceEventIndex.Assignment, ids, criteria)).build();
    }

    /**
     * List device command responses for a given assignment.
     * 
     * @param token
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{token}/responses")
    @ApiOperation(value = "List command response events for assignment")
    public Response listCommandResponsesForAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @ApiParam(value = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response
		.ok(new BlockingDeviceEventManagement(getDeviceEventManagement()).listDeviceCommandResponsesForIndex(
			DeviceEventIndex.Assignment, Collections.singletonList(assignment.getId()), criteria))
		.build();
    }

    /**
     * End an existing device assignment.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/end")
    @ApiOperation(value = "Release an active device assignment")
    public Response endDeviceAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceManagement management = getDeviceManagement();
	IDeviceAssignment existing = assertDeviceAssignment(token);
	IDeviceAssignment updated = management.endDeviceAssignment(existing.getId());
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	return Response.ok(helper.convert(updated, getAssetManagement())).build();
    }

    /**
     * Mark a device assignment as missing.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/{token}/missing")
    @ApiOperation(value = "Mark device assignment as missing")
    public Response missingDeviceAssignment(
	    @ApiParam(value = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceManagement management = getDeviceManagement();
	IDeviceAssignment existing = assertDeviceAssignment(token);

	// Update status field.
	DeviceAssignmentCreateRequest request = new DeviceAssignmentCreateRequest();
	request.setStatus(DeviceAssignmentStatus.Missing);

	IDeviceAssignment updated = management.updateDeviceAssignment(existing.getId(), request);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	return Response.ok(helper.convert(updated, getAssetManagement())).build();
    }

    /**
     * Get a device command by token. Throw an exception if not found.
     * 
     * @param deviceTypeId
     * @param commandToken
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceCommand assureDeviceCommand(UUID deviceTypeId, String commandToken) throws SiteWhereException {
	IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(deviceTypeId, commandToken);
	if (command == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandId, ErrorLevel.ERROR);
	}
	return command;
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignment(String token) throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(token);
	if (assignment == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}
	return assignment;
    }

    /**
     * Get list of assignment ids based on assignment tokens.
     * 
     * @param bulk
     * @return
     * @throws SiteWhereException
     */
    protected List<UUID> getDeviceAssignmentIds(DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	List<UUID> results = new ArrayList<UUID>();
	for (String token : bulk.getDeviceAssignmentTokens()) {
	    IDeviceAssignment assignment = assertDeviceAssignment(token);
	    results.add(assignment.getId());
	}
	return results;
    }

    /**
     * Create date range search criteria.
     * 
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    protected static IDateRangeSearchCriteria createDateRangeSearchCriteria(int page, int pageSize, String startDate,
	    String endDate) throws SiteWhereException {
	Date parsedStartDate = parseDateOrFail(startDate);
	Date parsedEndDate = parseDateOrFail(endDate);
	return new DateRangeSearchCriteria(page, pageSize, parsedStartDate, parsedEndDate);
    }

    /**
     * Parse date or throw exception if invalid.
     * 
     * @param dateString
     * @return
     * @throws SiteWhereException
     */
    public static Date parseDateOrFail(String dateString) throws SiteWhereException {
	try {
	    if (StringUtils.isBlank(dateString)) {
		return null;
	    }
	    ZonedDateTime zdt = DataUtils.parseDateInMutipleFormats(dateString);
	    return Date.from(zdt.toInstant());
	} catch (DateTimeParseException e) {
	    throw new SiteWhereException(e);
	}
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }

    protected IDeviceEventManagementApiChannel<?> getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagementApiChannel();
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiChannel();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}