/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.device.marshaling.AreaMarshalHelper;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.grpc.client.event.BlockingDeviceEventManagement;
import com.sitewhere.rest.model.area.request.AreaCreateRequest;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.asset.DeviceAlertWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceCommandInvocationWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceCommandResponseWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceLocationWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceMeasurementsWithAsset;
import com.sitewhere.rest.model.device.asset.DeviceStateChangeWithAsset;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.area.AreaSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.label.ILabelGeneration;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.ITreeNode;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for area operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/areas")
@Api(value = "areas")
public class Areas extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Areas.class);

    /**
     * Create a new area.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    @ApiOperation(value = "Create new area")
    public IArea createArea(@RequestBody AreaCreateRequest input) throws SiteWhereException {
	return getDeviceManagement().createArea(input);
    }

    /**
     * Get information for a given area based on token.
     * 
     * @param areaToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{areaToken:.+}")
    @ApiOperation(value = "Get area by token")
    public IArea getAreaByToken(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Include area type", required = false) @RequestParam(defaultValue = "true") boolean includeAreaType,
	    @ApiParam(value = "Include parent area information", required = false) @RequestParam(defaultValue = "true") boolean includeParentArea)
	    throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	AreaMarshalHelper helper = new AreaMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeAreaType(includeAreaType);
	helper.setIncludeParentArea(includeParentArea);
	return helper.convert(existing);
    }

    /**
     * Update information for an area.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PutMapping(value = "/{areaToken:.+}")
    @ApiOperation(value = "Update existing area")
    public IArea updateArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @RequestBody AreaCreateRequest request) throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	return getDeviceManagement().updateArea(existing.getId(), request);
    }

    /**
     * Get label for area based on a specific generator.
     * 
     * @param areaToken
     * @param generatorId
     * @param servletRequest
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{areaToken}/label/{generatorId}")
    @ApiOperation(value = "Get label for area")
    public ResponseEntity<byte[]> getAreaLabel(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Generator id", required = true) @PathVariable String generatorId,
	    HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	ILabel label = getLabelGeneration().getAreaLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.IMAGE_PNG);
	return new ResponseEntity<byte[]>(label.getContent(), headers, HttpStatus.OK);
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
    @GetMapping
    @ApiOperation(value = "List areas matching criteria")
    public ISearchResults<IArea> listAreas(
	    @ApiParam(value = "Limit to root elements", required = false) @RequestParam(required = false, defaultValue = "true") Boolean rootOnly,
	    @ApiParam(value = "Limit by parent area token", required = false) @RequestParam(required = false) String parentAreaToken,
	    @ApiParam(value = "Limit by area type token", required = false) @RequestParam(required = false) String areaTypeToken,
	    @ApiParam(value = "Include area type", required = false) @RequestParam(defaultValue = "false") boolean includeAreaType,
	    @ApiParam(value = "Include assignments", required = false) @RequestParam(defaultValue = "false") boolean includeAssignments,
	    @ApiParam(value = "Include zones", required = false) @RequestParam(defaultValue = "false") boolean includeZones,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	AreaSearchCriteria criteria = buildAreaSearchCriteria(page, pageSize, rootOnly, parentAreaToken, areaTypeToken);

	// Perform search.
	ISearchResults<IArea> matches = getDeviceManagement().listAreas(criteria);
	AreaMarshalHelper helper = new AreaMarshalHelper(getDeviceManagement(), getAssetManagement());
	helper.setIncludeAreaType(includeAreaType);
	helper.setIncludeZones(includeZones);
	helper.setIncludeAssignments(includeAssignments);

	List<IArea> results = new ArrayList<IArea>();
	for (IArea area : matches.getResults()) {
	    results.add(helper.convert(area));
	}
	return new SearchResults<IArea>(results, matches.getNumResults());
    }

    /**
     * List all areas in a hierarchical tree format.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/tree")
    @ApiOperation(value = "List all areas in tree format")
    public List<ITreeNode> getAreasTree() throws SiteWhereException {
	return getDeviceManagement().getAreasTree();
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
    protected AreaSearchCriteria buildAreaSearchCriteria(int page, int pageSize, boolean rootOnly,
	    String parentAreaToken, String areaTypeToken) throws SiteWhereException {
	// Build criteria.
	AreaSearchCriteria criteria = new AreaSearchCriteria(page, pageSize);
	criteria.setRootOnly(rootOnly);

	// Look up parent area if provided.
	if (parentAreaToken != null) {
	    IArea parent = getDeviceManagement().getAreaByToken(parentAreaToken);
	    if (parent != null) {
		criteria.setParentAreaId(parent.getId());
	    }
	}

	// Look up area type if provided.
	if (areaTypeToken != null) {
	    IAreaType areaType = getDeviceManagement().getAreaTypeByToken(areaTypeToken);
	    if (areaType != null) {
		criteria.setAreaTypeId(areaType.getId());
	    }
	}

	return criteria;
    }

    /**
     * Delete information for a given area based on token.
     * 
     * @param areaToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping(value = "/{areaToken:.+}")
    @ApiOperation(value = "Delete area by token")
    public IArea deleteArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken)
	    throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	return getDeviceManagement().deleteArea(existing.getId());
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
    @GetMapping(value = "/{areaToken}/measurements")
    @ApiOperation(value = "List measurements for an area")
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceMeasurement> results = getDeviceEventManagement()
		.listDeviceMeasurementsForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceMeasurement> wrapped = new ArrayList<IDeviceMeasurement>();
	for (IDeviceMeasurement result : results.getResults()) {
	    wrapped.add(new DeviceMeasurementsWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceMeasurement>(wrapped, results.getNumResults());
    }

    /**
     * Get device locations for an area.
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
    @GetMapping(value = "/{areaToken}/locations")
    @ApiOperation(value = "List locations for an area")
    public ISearchResults<IDeviceLocation> listDeviceLocationsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceLocation> results = getDeviceEventManagement()
		.listDeviceLocationsForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceLocation> wrapped = new ArrayList<IDeviceLocation>();
	for (IDeviceLocation result : results.getResults()) {
	    wrapped.add(new DeviceLocationWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceLocation>(wrapped, results.getNumResults());
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
    @GetMapping(value = "/{areaToken}/alerts")
    @ApiOperation(value = "List alerts for an area")
    public ISearchResults<IDeviceAlert> listDeviceAlertsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	ISearchResults<IDeviceAlert> results = getDeviceEventManagement()
		.listDeviceAlertsForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceAlert> wrapped = new ArrayList<IDeviceAlert>();
	for (IDeviceAlert result : results.getResults()) {
	    wrapped.add(new DeviceAlertWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceAlert>(wrapped, results.getNumResults());
    }

    /**
     * Get device command invocations for an area.
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
    @GetMapping(value = "/{areaToken}/invocations")
    @ApiOperation(value = "List command invocations for an area")
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceCommandInvocation> results = getDeviceEventManagement()
		.listDeviceCommandInvocationsForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandInvocation> wrapped = new ArrayList<IDeviceCommandInvocation>();
	for (IDeviceCommandInvocation result : results.getResults()) {
	    wrapped.add(new DeviceCommandInvocationWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceCommandInvocation>(wrapped, results.getNumResults());
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
    @GetMapping(value = "/{areaToken}/responses")
    @ApiOperation(value = "List command responses for an area")
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceCommandResponse> results = getDeviceEventManagement()
		.listDeviceCommandResponsesForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceCommandResponse> wrapped = new ArrayList<IDeviceCommandResponse>();
	for (IDeviceCommandResponse result : results.getResults()) {
	    wrapped.add(new DeviceCommandResponseWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceCommandResponse>(wrapped, results.getNumResults());
    }

    /**
     * Get device state changes for an area.
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
    @GetMapping(value = "/{areaToken}/statechanges")
    @ApiOperation(value = "List state changes associated with an area")
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) String startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) String endDate,
	    HttpServletResponse response) throws SiteWhereException {
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	IDateRangeSearchCriteria criteria = Assignments.createDateRangeSearchCriteria(page, pageSize, startDate,
		endDate, response);
	ISearchResults<IDeviceStateChange> results = getDeviceEventManagement()
		.listDeviceStateChangesForIndex(DeviceEventIndex.Area, areas, criteria);

	// Marshal with asset info since multiple assignments might match.
	List<IDeviceStateChange> wrapped = new ArrayList<IDeviceStateChange>();
	for (IDeviceStateChange result : results.getResults()) {
	    wrapped.add(new DeviceStateChangeWithAsset(result, getAssetManagement()));
	}
	return new SearchResults<IDeviceStateChange>(wrapped, results.getNumResults());
    }

    /**
     * Find device assignments associated with an area.
     * 
     * @param areaToken
     * @param status
     * @param includeDevice
     * @param includeAsset
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{areaToken}/assignments")
    @ApiOperation(value = "List device assignments for an area")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<DeviceAssignment> listAssignmentsForArea(
	    @ApiParam(value = "Token that identifies area", required = true) @PathVariable String areaToken,
	    @ApiParam(value = "Limit results to the given status", required = false) @RequestParam(required = false) String status,
	    @ApiParam(value = "Include device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
	    @ApiParam(value = "Include customer information", required = false) @RequestParam(defaultValue = "false") boolean includeCustomer,
	    @ApiParam(value = "Include area information", required = false) @RequestParam(defaultValue = "false") boolean includeArea,
	    @ApiParam(value = "Include asset information", required = false) @RequestParam(defaultValue = "false") boolean includeAsset,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	DeviceAssignmentStatus decodedStatus = (status != null) ? DeviceAssignmentStatus.valueOf(status) : null;
	if (decodedStatus != null) {
	    criteria.setStatus(decodedStatus);
	}
	List<UUID> areas = resolveAreaIds(areaToken, true, getDeviceManagement());
	criteria.setAreaIds(areas);

	ISearchResults<IDeviceAssignment> matches = getDeviceManagement().listDeviceAssignments(criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);

	List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
	for (IDeviceAssignment assignment : matches.getResults()) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}
	return new SearchResults<DeviceAssignment>(converted, matches.getNumResults());
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
    protected List<String> resolveAreaTokens(String areaToken, boolean recursive, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
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
    public static List<UUID> resolveAreaIds(String areaToken, boolean recursive, IDeviceManagement deviceManagement)
	    throws SiteWhereException {
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
	List<IArea> children = deviceManagement.getAreaChildren(current.getToken());
	for (IArea child : children) {
	    resolveAreasRecursively(child, recursive, matches, deviceManagement);
	}
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiChannel();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return new BlockingDeviceEventManagement(getMicroservice().getDeviceEventManagementApiChannel());
    }

    private IAssetManagement getAssetManagement() {
	return getMicroservice().getAssetManagementApiChannel();
    }

    private ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }
}