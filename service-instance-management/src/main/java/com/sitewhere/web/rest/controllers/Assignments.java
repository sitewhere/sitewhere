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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.ChartBuilder;
import com.sitewhere.microservice.api.device.DeviceAssignmentMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceAssignmentSummaryMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceCommandInvocationMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
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
import com.sitewhere.spi.device.IDeviceAssignmentSummary;
import com.sitewhere.spi.device.charting.IChartSeries;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;

/*
 * Controller for assignment operations.
 */
@Path("/api/assignments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "assignments")
@Tag(name = "Device Assignments", description = "Device assignments provide context (e.g. associated customer/area/asset) that is tagged on inbound events for a device.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Assignments {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Assignments.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a device assignment.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create new device assignment", description = "Create a new device assignment")
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
    @Operation(summary = "Get device assignment by token", description = "Get device assignment by token")
    public Response getDeviceAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment existing = assertDeviceAssignment(token);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	helper.setIncludeDeviceType(true);
	return Response.ok(helper.convert(existing, getAssetManagement())).build();
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
    @Operation(summary = "Delete an existing device assignment", description = "Delete an existing device assignment")
    public Response deleteDeviceAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
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
    @Operation(summary = "Update existing device assignment", description = "Update an existing device assignment")
    public Response updateDeviceAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
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
    @Operation(summary = "Get label for device assignment", description = "Get label for device assignment")
    public Response getAssignmentLabel(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Generator id", required = true) @PathParam("generatorId") String generatorId)
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
    @Operation(summary = "List assignments matching criteria", description = "List assignments matching criteria")
    public Response listAssignments(
	    @Parameter(description = "Limit by device token", required = false) @QueryParam("deviceToken") String deviceToken,
	    @Parameter(description = "Limit by customer token", required = false) @QueryParam("customerToken") String customerToken,
	    @Parameter(description = "Limit by area token", required = false) @QueryParam("areaToken") String areaToken,
	    @Parameter(description = "Limit by asset token", required = false) @QueryParam("assetToken") String assetToken,
	    @Parameter(description = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @Parameter(description = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @Parameter(description = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @Parameter(description = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
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
     * Perform an advanced search of device assignments.
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
    @Operation(summary = "Device assignment advanced search", description = "Search device assignments with advanced criteria")
    public Response searchDeviceAssignments(
	    @Parameter(description = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @Parameter(description = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @Parameter(description = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @Parameter(description = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") Integer page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") Integer pageSize,
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
     * Search device assignments for summary information.
     * 
     * @param includeAsset
     * @param page
     * @param pageSize
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Path("/search/summaries")
    @Operation(summary = "Device assignment summary advanced search", description = "Search device assignment summaries with advanced criteria")
    public Response searchDeviceAssignmentSummaries(
	    @Parameter(description = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") Integer page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") Integer pageSize,
	    @RequestBody DeviceAssignmentSearchCriteria criteria) throws SiteWhereException {
	// Allow request parameters to override paging criteria.
	if (page != null) {
	    criteria.setPageNumber(page);
	}
	if (pageSize != null) {
	    criteria.setPageSize(pageSize);
	}

	// Perform search.
	ISearchResults<? extends IDeviceAssignmentSummary> matches = getDeviceManagement()
		.listDeviceAssignmentSummaries(criteria);
	DeviceAssignmentSummaryMarshalHelper helper = new DeviceAssignmentSummaryMarshalHelper();
	helper.setIncludeAsset(includeAsset);

	List<IDeviceAssignmentSummary> results = new ArrayList<>();
	for (IDeviceAssignmentSummary assn : matches.getResults()) {
	    results.add(helper.convert(assn, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceAssignmentSummary>(results, matches.getNumResults())).build();
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
    @Operation(summary = "List measurement events for multiple assignments", description = "List measurement events for multiple assignments")
    public Response listMeasurementsForAssignments(
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	return Response.ok(
		getDeviceEventManagement().listDeviceMeasurementsForIndex(DeviceEventIndex.Assignment, ids, criteria))
		.build();
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
    @Operation(summary = "List measurement events for device assignment", description = "List measurement events for device assignment")
    public Response listMeasurementsForAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	return Response.ok(getDeviceEventManagement().listDeviceMeasurementsForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria)).build();
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
    @Operation(summary = "List measurements for multiple assignments as chart series", description = "List measurements for multiple assignments as chart series")
    public Response listMeasurementsForAssignmentsAsChartSeries(
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate,
	    @Parameter(description = "Measurement Ids", required = false) @QueryParam("measurementIds") String[] measurementIds,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	Map<String, List<IChartSeries<Double>>> results = new HashMap<String, List<IChartSeries<Double>>>();
	for (String token : bulk.getDeviceAssignmentTokens()) {
	    IDeviceAssignment assignment = assertDeviceAssignment(token);
	    ISearchResults<IDeviceMeasurement> measurements = getDeviceEventManagement().listDeviceMeasurementsForIndex(
		    DeviceEventIndex.Assignment, Collections.singletonList(assignment.getId()), criteria);
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
    @Operation(summary = "List assignment measurements as chart series", description = "List assignment measurements as chart series")
    public Response listMeasurementsForAssignmentAsChartSeries(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate,
	    @Parameter(description = "Measurement Ids", required = false) @QueryParam("measurementIds") String[] measurementIds)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	ISearchResults<IDeviceMeasurement> measurements = getDeviceEventManagement().listDeviceMeasurementsForIndex(
		DeviceEventIndex.Assignment, Collections.singletonList(assignment.getId()), criteria);
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
    @Operation(summary = "Create measurement event for device assignment", description = "Create measurement event for device assignment")
    public Response createMeasurements(@RequestBody DeviceMeasurementCreateRequest input,
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(getDeviceManagement(),
		assignment);
	return Response.ok(getDeviceEventManagement().addDeviceMeasurements(context, input).get(0)).build();
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
    @Operation(summary = "List location events for multiple assignments", description = "List location events for multiple assignments")
    public Response listLocationsForAssignments(
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return Response
		.ok(getDeviceEventManagement().listDeviceLocationsForIndex(DeviceEventIndex.Assignment, ids, criteria))
		.build();
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
    @Operation(summary = "List location events for device assignment", description = "List location events for device assignment")
    public Response listLocationsForAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(getDeviceEventManagement().listDeviceLocationsForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria)).build();
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
    @Operation(summary = "Create location event for device assignment", description = "Create location event for device assignment")
    public Response createLocation(@RequestBody DeviceLocationCreateRequest input,
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(getDeviceManagement(),
		assignment);
	return Response.ok(getDeviceEventManagement().addDeviceLocations(context, input).get(0)).build();
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
    @Operation(summary = "List alert events for multiple assignments", description = "List alert events for multiple assignments")
    public Response listAlertsForAssignments(
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return Response
		.ok(getDeviceEventManagement().listDeviceAlertsForIndex(DeviceEventIndex.Assignment, ids, criteria))
		.build();
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
    @Operation(summary = "List alert events for device assignment", description = "List alert events for device assignment")
    public Response listAlertsForAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(getDeviceEventManagement().listDeviceAlertsForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria)).build();
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
    @Operation(summary = "Create alert event for device assignment", description = "Create alert event for device assignment")
    public Response createAlert(@RequestBody DeviceAlertCreateRequest input,
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(getDeviceManagement(),
		assignment);
	return Response.ok(getDeviceEventManagement().addDeviceAlerts(context, input).get(0)).build();
    }

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
    @Operation(summary = "Create command invocation event for assignment", description = "Create command invocation event for assignment")
    public Response createCommandInvocation(@RequestBody DeviceCommandInvocationCreateRequest request,
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(getDeviceManagement(),
		assignment);
	IDeviceCommandInvocation result = getDeviceEventManagement().addDeviceCommandInvocations(context, request)
		.get(0);
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
    @Operation(summary = "Schedule command invocation", description = "Schedule command invocation")
    public Response scheduleCommandInvocation(@RequestBody DeviceCommandInvocationCreateRequest request,
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Schedule token", required = true) @PathParam("scheduleToken") String scheduleToken)
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
    @Operation(summary = "List command invocation events for multiple assignments", description = "List command invocation events for multiple assignments")
    public Response listCommandInvocationsForAssignments(
	    @Parameter(description = "Include command information", required = false) @QueryParam("includeCommand") @DefaultValue("true") boolean includeCommand,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	ISearchResults<IDeviceCommandInvocation> matches = getDeviceEventManagement()
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
    @Operation(summary = "List command invocation events for assignment", description = "List command invocation events for assignment")
    public Response listCommandInvocationsForAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Include command information", required = false) @QueryParam("includeCommand") @DefaultValue("true") boolean includeCommand,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	ISearchResults<IDeviceCommandInvocation> matches = getDeviceEventManagement()
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
    @Operation(summary = "Create a state change event for a device assignment", description = "Create a state change event for a device assignment")
    public Response createStateChange(@RequestBody DeviceStateChangeCreateRequest input,
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(getDeviceManagement(),
		assignment);
	return Response.ok(getDeviceEventManagement().addDeviceStateChanges(context, input).get(0)).build();
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
    @Operation(summary = "List state change events for multiple assignments", description = "List state change events for multiple assignments")
    public Response listStateChangesForAssignments(
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return Response.ok(
		getDeviceEventManagement().listDeviceStateChangesForIndex(DeviceEventIndex.Assignment, ids, criteria))
		.build();
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
    @Operation(summary = "List state change events for a device assignment", description = "List state change events for a device assignment")
    public Response listStateChangesForAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(getDeviceEventManagement().listDeviceStateChangesForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria)).build();
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
    @Operation(summary = "Create command response event for assignment", description = "Create command response event for assignment")
    public Response createCommandResponse(@RequestBody DeviceCommandResponseCreateRequest input,
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(getDeviceManagement(),
		assignment);
	IDeviceCommandResponse result = getDeviceEventManagement().addDeviceCommandResponses(context, input).get(0);
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
    @Operation(summary = "List command response events for multiple assignments", description = "List command response events for multiple assignments")
    public Response listCommandResponsesForAssignments(
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return Response.ok(getDeviceEventManagement().listDeviceCommandResponsesForIndex(DeviceEventIndex.Assignment,
		ids, criteria)).build();
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
    @Operation(summary = "List command response events for assignment", description = "List command response events for assignment")
    public Response listCommandResponsesForAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return Response.ok(getDeviceEventManagement().listDeviceCommandResponsesForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria)).build();
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
    @Consumes(MediaType.WILDCARD)
    @Operation(summary = "Release an active device assignment", description = "Release an active device assignment")
    public Response endDeviceAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
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
    @Consumes(MediaType.WILDCARD)
    @Operation(summary = "Mark device assignment as missing", description = "Mark device assignment as missing")
    public Response missingDeviceAssignment(
	    @Parameter(description = "Assignment token", required = true) @PathParam("token") String token)
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
	return getMicroservice().getDeviceManagement();
    }

    protected IDeviceEventManagementApiChannel<?> getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected IScheduleManagement getScheduleManagement() {
	return getMicroservice().getScheduleManagementApiChannel();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}