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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.sitewhere.rest.model.area.request.AreaCreateRequest;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentSummary;
import com.sitewhere.rest.model.device.marshaling.MarshaledArea;
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
import com.sitewhere.spi.search.ITreeNode;

/**
 * Controller for area operations.
 */
@RestController
@RequestMapping("/api/areas")
public class Areas {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Areas.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new area.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public IArea createArea(@RequestBody AreaCreateRequest input) throws SiteWhereException {
	return getDeviceManagement().createArea(input);
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
    @GetMapping("/{areaToken}")
    public MarshaledArea getAreaByToken(@PathVariable String areaToken,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAreaType,
	    @RequestParam(defaultValue = "true", required = false) boolean includeParentArea)
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
     * @param areaToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{areaToken}")
    public IArea updateArea(@PathVariable String areaToken, @RequestBody AreaCreateRequest request)
	    throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	return getDeviceManagement().updateArea(existing.getId(), request);
    }

    /**
     * Get label for area based on a specific generator.
     * 
     * @param areaToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{areaToken}/label/{generatorId}")
    public ResponseEntity<?> getAreaLabel(@PathVariable String areaToken, @PathVariable String generatorId)
	    throws SiteWhereException {
	IArea existing = assertArea(areaToken);
	ILabel label = getLabelGeneration().getAreaLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label);
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
    public SearchResults<IArea> listAreas(@RequestParam(defaultValue = "true", required = false) Boolean rootOnly,
	    @RequestParam(required = false) String parentAreaToken,
	    @RequestParam(required = false) String areaTypeToken,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAreaType,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAssignments,
	    @RequestParam(defaultValue = "false", required = false) boolean includeZones,
	    @RequestParam(defaultValue = "1") int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
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
	return new SearchResults<IArea>(results, matches.getNumResults());
    }

    /**
     * List all areas in a hierarchical tree format.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/tree")
    public List<? extends ITreeNode> getAreasTree() throws SiteWhereException {
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
    @DeleteMapping("/{areaToken}")
    public IArea deleteArea(@PathVariable String areaToken) throws SiteWhereException {
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{areaToken}/measurements")
    public SearchResults<IDeviceMeasurement> listDeviceMeasurementsForArea(@PathVariable String areaToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{areaToken}/locations")
    public SearchResults<IDeviceLocation> listDeviceLocationsForArea(@PathVariable String areaToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{areaToken}/alerts")
    public SearchResults<IDeviceAlert> listDeviceAlertsForArea(@PathVariable String areaToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{areaToken}/invocations")
    public SearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForArea(@PathVariable String areaToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{areaToken}/responses")
    public SearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForArea(@PathVariable String areaToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{areaToken}/statechanges")
    public SearchResults<IDeviceStateChange> listDeviceStateChangesForArea(@PathVariable String areaToken,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
	return new SearchResults<IDeviceStateChange>(wrapped, results.getNumResults());
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
    @GetMapping("/{areaToken}/assignments")
    public SearchResults<DeviceAssignment> listAssignmentsForArea(@PathVariable String areaToken,
	    @RequestParam(required = false) String status,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDevice,
	    @RequestParam(defaultValue = "false", required = false) boolean includeCustomer,
	    @RequestParam(defaultValue = "false", required = false) boolean includeArea,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
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
	return new SearchResults<DeviceAssignment>(converted, matches.getNumResults());
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
    @GetMapping("/{areaToken}/assignments/summaries")
    public SearchResults<DeviceAssignmentSummary> listAssignmentSummariesForArea(@PathVariable String areaToken,
	    @RequestParam(required = false) String status,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
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
	return new SearchResults<DeviceAssignmentSummary>(converted, matches.getNumResults());
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