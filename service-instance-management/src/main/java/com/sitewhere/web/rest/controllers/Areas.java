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
import java.util.Collections;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.AreaMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceAssignmentMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceAssignmentSummaryMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.device.asset.DeviceAlertWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceCommandInvocationWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceCommandResponseWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceLocationWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceMeasurementsWithAsset;
import com.sitewhere.microservice.api.device.asset.DeviceStateChangeWithAsset;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.area.request.AreaCreateRequest;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentSummary;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.area.AreaSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentSummary;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Controller for area operations.
 */
@Path("/api/areas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Areas", description = "Areas are used to provide location context for device assignments.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Areas {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(Areas.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new area.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create new area", description = "Create new area")
    public Response createArea(@RequestBody AreaCreateRequest input) throws SiteWhereException {
	return Response.ok(getDeviceManagement().createArea(input)).build();
    }

    /**
     * Get information for a given area based on token.
     * 
     * @param areaToken
     * @param includeAreaType
     * @param includeParentArea
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaToken}")
    @Operation(summary = "Get area by token", description = "Get area by unique token")
    public Response getAreaByToken(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Include area type", required = false) @QueryParam("includeAreaType") @DefaultValue("false") boolean includeAreaType,
	    @Parameter(description = "Include parent area information", required = false) @QueryParam("includeParentArea") @DefaultValue("true") boolean includeParentArea)
	    throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	AreaMarshalHelper helper = new AreaMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeAreaType(includeAreaType);
	helper.setIncludeParentArea(includeParentArea);
	return Response.ok(helper.convert(existing)).build();
    }

    /**
     * Update information for an area.
     * 
     * @param areaToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{areaToken}")
    @Operation(summary = "Update existing area", description = "Update details for an existing area")
    public Response updateArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @RequestBody AreaCreateRequest request) throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	return Response.ok(getDeviceManagement().updateArea(existing.getId(), request)).build();
    }

    /**
     * Get label for area based on a specific generator.
     * 
     * @param areaToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaToken}/label/{generatorId}")
    @Produces("image/png")
    @Operation(summary = "Get label for area", description = "Get label for area")
    public Response getAreaLabel(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	ILabel label = getLabelGeneration().getAreaLabel(generatorId, existing.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * List areas matching criteria.
     * 
     * @param rootOnly
     * @param parentAreaToken
     * @param areaTypeToken
     * @param includeAreaType
     * @param includeAssignments
     * @param includeZones
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List areas matching criteria", description = "List areas matching criteria")
    public Response listAreas(
	    @Parameter(description = "Limit to root elements", required = false) @QueryParam("rootOnly") @DefaultValue("true") Boolean rootOnly,
	    @Parameter(description = "Limit by parent area token", required = false) @QueryParam("parentAreaToken") String parentAreaToken,
	    @Parameter(description = "Limit by area type token", required = false) @QueryParam("areaTypeToken") String areaTypeToken,
	    @Parameter(description = "Include area type", required = false) @QueryParam("includeAreaType") @DefaultValue("false") boolean includeAreaType,
	    @Parameter(description = "Include assignments", required = false) @QueryParam("includeAssignments") @DefaultValue("false") boolean includeAssignments,
	    @Parameter(description = "Include zones", required = false) @QueryParam("includeZones") @DefaultValue("false") boolean includeZones,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	AreaSearchCriteria criteria = buildAreaSearchCriteria(page, pageSize, rootOnly, parentAreaToken, areaTypeToken);

	// Perform search.
	ISearchResults<? extends IArea> matches = getDeviceManagement().listAreas(criteria);
	AreaMarshalHelper helper = new AreaMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeAreaType(includeAreaType);
	helper.setIncludeZones(includeZones);
	helper.setIncludeAssignments(includeAssignments);

	List<IArea> results = new ArrayList<IArea>();
	for (IArea area : matches.getResults()) {
	    results.add(helper.convert(area));
	}
	return Response.ok(new SearchResults<IArea>(results, matches.getNumResults())).build();
    }

    /**
     * List all areas in a hierarchical tree format.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/tree")
    @Operation(summary = "List all areas in tree format", description = "List all areas in tree format")
    public Response getAreasTree() throws SiteWhereException {
	return Response.ok(getDeviceManagement().getAreasTree()).build();
    }

    /**
     * Build area search criteria from parameters.
     * 
     * @param page
     * @param pageSize
     * @param rootOnly
     * @param parentAreaToken
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    public static AreaSearchCriteria buildAreaSearchCriteria(int page, int pageSize, boolean rootOnly,
	    String parentAreaToken, String areaTypeToken) throws SiteWhereException {
	// Build criteria.
	AreaSearchCriteria criteria = new AreaSearchCriteria(page, pageSize);
	criteria.setRootOnly(rootOnly);
	criteria.setParentAreaToken(parentAreaToken);
	criteria.setAreaTypeToken(areaTypeToken);
	return criteria;
    }

    /**
     * Delete information for a given area based on token.
     * 
     * @param areaToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{areaToken}")
    @Operation(summary = "Delete area by token", description = "Delete area by token")
    public Response deleteArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken)
	    throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	LOGGER.info(String.format("REST call to delete area %s:\n%s\n\n", existing.getId().toString(),
		MarshalUtils.marshalJsonAsPrettyString(existing)));
	return Response.ok(getDeviceManagement().deleteArea(existing.getId())).build();
    }

    /**
     * Get device measurements for an area.
     * 
     * @param areaToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaToken}/measurements")
    @Operation(summary = "List measurements for an area", description = "List measurements for an area")
    public Response listDeviceMeasurementsForArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIdsRecursive(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceMeasurement> results = getDeviceEventManagement()
		.listDeviceMeasurementsForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceMeasurement> wrapped = new ArrayList<IDeviceMeasurement>();
	for (IDeviceMeasurement result : results.getResults()) {
	    wrapped.add(new DeviceMeasurementsWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceMeasurement>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device locations for an area.
     * 
     * @param areaToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaToken}/locations")
    @Operation(summary = "List locations for an area", description = "List locations for an area")
    public Response listDeviceLocationsForArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIdsRecursive(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceLocation> results = getDeviceEventManagement()
		.listDeviceLocationsForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceLocation> wrapped = new ArrayList<IDeviceLocation>();
	for (IDeviceLocation result : results.getResults()) {
	    wrapped.add(new DeviceLocationWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceLocation>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device alerts for an area.
     * 
     * @param areaToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaToken}/alerts")
    @Operation(summary = "List alerts for an area", description = "List alerts for an area")
    public Response listDeviceAlertsForArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	List<UUID> areas = resolveAreaIdsRecursive(areaToken, true, getDeviceManagement());
	ISearchResults<IDeviceAlert> results = getDeviceEventManagement()
		.listDeviceAlertsForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceAlert> wrapped = new ArrayList<IDeviceAlert>();
	for (IDeviceAlert result : results.getResults()) {
	    wrapped.add(new DeviceAlertWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceAlert>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device command invocations for an area.
     * 
     * @param areaToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaToken}/invocations")
    @Operation(summary = "List command invocations for an area", description = "List command invocations for an area")
    public Response listDeviceCommandInvocationsForArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIdsRecursive(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceCommandInvocation> results = getDeviceEventManagement()
		.listDeviceCommandInvocationsForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandInvocation> wrapped = new ArrayList<IDeviceCommandInvocation>();
	for (IDeviceCommandInvocation result : results.getResults()) {
	    wrapped.add(new DeviceCommandInvocationWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceCommandInvocation>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device command responses for an area.
     * 
     * @param areaToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaToken}/responses")
    @Operation(summary = "List command responses for an area", description = "List command responses for an area")
    public Response listDeviceCommandResponsesForArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIdsRecursive(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceCommandResponse> results = getDeviceEventManagement()
		.listDeviceCommandResponsesForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandResponse> wrapped = new ArrayList<IDeviceCommandResponse>();
	for (IDeviceCommandResponse result : results.getResults()) {
	    wrapped.add(new DeviceCommandResponseWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceCommandResponse>(wrapped, results.getNumResults())).build();
    }

    /**
     * Get device state changes for an area.
     * 
     * @param areaToken
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaToken}/statechanges")
    @Operation(summary = "List state changes associated with an area", description = "List state changes associated with an area")
    public Response listDeviceStateChangesForArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize,
	    @Parameter(description = "Start date", required = false) @QueryParam("startDate") String startDate,
	    @Parameter(description = "End date", required = false) @QueryParam("endDate") String endDate)
	    throws SiteWhereException {
	List<UUID> areas = resolveAreaIdsRecursive(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate);
	ISearchResults<IDeviceStateChange> results = getDeviceEventManagement()
		.listDeviceStateChangesForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceStateChange> wrapped = new ArrayList<IDeviceStateChange>();
	for (IDeviceStateChange result : results.getResults()) {
	    wrapped.add(new DeviceStateChangeWithAsset(result, getAssetManagement()));
	}
	return Response.ok(new SearchResults<IDeviceStateChange>(wrapped, results.getNumResults())).build();
    }

    /**
     * Find device assignments associated with an area.
     * 
     * @param areaToken
     * @param status
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
    @Path("/{areaToken}/assignments")
    @Operation(summary = "List device assignments for an area", description = "List device assignments for an area")
    public Response listAssignmentsForArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Limit results to the given status", required = false) @QueryParam("status") String status,
	    @Parameter(description = "Include device information", required = false) @QueryParam("includeDevice") @DefaultValue("false") boolean includeDevice,
	    @Parameter(description = "Include customer information", required = false) @QueryParam("includeCustomer") @DefaultValue("false") boolean includeCustomer,
	    @Parameter(description = "Include area information", required = false) @QueryParam("includeArea") @DefaultValue("false") boolean includeArea,
	    @Parameter(description = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	DeviceAssignmentStatus decodedStatus = (status != null) ? DeviceAssignmentStatus.valueOf(status) : null;
	if (decodedStatus != null) {
	    criteria.setAssignmentStatuses(Collections.singletonList(decodedStatus));
	}
	List<String> areas = resolveAreaTokensRecursive(areaToken, true, getDeviceManagement());
	criteria.setAreaTokens(areas);

	ISearchResults<? extends IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);

	List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
	for (IDeviceAssignment assignment : matches.getResults()) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}
	return Response.ok(new SearchResults<DeviceAssignment>(converted, matches.getNumResults())).build();
    }

    /**
     * List summary information for area device assignments.
     * 
     * @param areaToken
     * @param status
     * @param includeAsset
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaToken}/assignments/summaries")
    @Operation(summary = "List device assignment summaries for an area", description = "List device assignments for an area")
    public Response listAssignmentSummariesForArea(
	    @Parameter(description = "Token that identifies area", required = true) @PathParam("areaToken") String areaToken,
	    @Parameter(description = "Limit results to the given status", required = false) @QueryParam("status") String status,
	    @Parameter(description = "Include asset information", required = false) @QueryParam("includeAsset") @DefaultValue("false") boolean includeAsset,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	DeviceAssignmentStatus decodedStatus = (status != null) ? DeviceAssignmentStatus.valueOf(status) : null;
	if (decodedStatus != null) {
	    criteria.setAssignmentStatuses(Collections.singletonList(decodedStatus));
	}
	List<String> areas = resolveAreaTokensRecursive(areaToken, true, getDeviceManagement());
	criteria.setAreaTokens(areas);

	ISearchResults<? extends IDeviceAssignmentSummary> matches = getDeviceManagement()
		.listDeviceAssignmentSummaries(criteria);
	DeviceAssignmentSummaryMarshalHelper helper = new DeviceAssignmentSummaryMarshalHelper();
	helper.setIncludeAsset(includeAsset);

	List<DeviceAssignmentSummary> converted = new ArrayList<DeviceAssignmentSummary>();
	for (IDeviceAssignmentSummary assignment : matches.getResults()) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}
	return Response.ok(new SearchResults<DeviceAssignmentSummary>(converted, matches.getNumResults())).build();
    }

    /**
     * Get area associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IArea assertArea(String token) throws SiteWhereException {
	IArea area = getDeviceManagement().getAreaByToken(token);
	if (area == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	}
	return area;
    }

    /**
     * Resolve tokens recursively for subareas based on area token.
     * 
     * @param areaToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<String> resolveAreaTokensRecursive(String areaToken, boolean recursive,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	List<IArea> areas = resolveAreas(areaToken, recursive, deviceManagement);
	List<String> tokens = new ArrayList<>();
	for (IArea area : areas) {
	    tokens.add(area.getToken());
	}
	return tokens;
    }

    /**
     * Resolve ids recursively for subareas based on area token.
     * 
     * @param areaToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<UUID> resolveAreaIdsRecursive(String areaToken, boolean recursive,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	List<IArea> areas = resolveAreas(areaToken, recursive, deviceManagement);
	List<UUID> ids = new ArrayList<>();
	for (IArea area : areas) {
	    ids.add(area.getId());
	}
	return ids;
    }

    /**
     * Resolve areas including nested areas.
     * 
     * @param areaToken
     * @param recursive
     * @param deviceManagement
     * @return
     * @throws SiteWhereException
     */
    public static List<IArea> resolveAreas(String areaToken, boolean recursive, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
	IArea existing = deviceManagement.getAreaByToken(areaToken);
	if (existing == null) {
	    return new ArrayList<IArea>();
	}
	Map<String, IArea> resolved = new HashMap<>();
	resolveAreasRecursively(existing, recursive, resolved, deviceManagement);
	List<IArea> response = new ArrayList<>();
	response.addAll(resolved.values());
	return response;
    }

    /**
     * Resolve areas recursively.
     * 
     * @param current
     * @param recursive
     * @param matches
     * @param deviceManagement
     * @throws SiteWhereException
     */
    protected static void resolveAreasRecursively(IArea current, boolean recursive, Map<String, IArea> matches,
	    IDeviceManagement deviceManagement) throws SiteWhereException {
	matches.put(current.getToken(), current);
	List<? extends IArea> children = deviceManagement.getAreaChildren(current.getToken());
	for (IArea child : children) {
	    resolveAreasRecursively(child, recursive, matches, deviceManagement);
	}
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiChannel();
    }

    protected IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}