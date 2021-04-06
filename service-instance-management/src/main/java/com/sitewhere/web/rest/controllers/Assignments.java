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

import org.apache.commons.lang3.StringUtils;
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
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceStateChangeCreateRequest;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceAssignment;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceCommandInvocation;
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
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/*
 * Controller for assignment operations.
 */
@RestController
@RequestMapping("/api/assignments")
public class Assignments {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Assignments.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a device assignment.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public MarshaledDeviceAssignment createDeviceAssignment(@RequestBody DeviceAssignmentCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment created = getDeviceManagement().createDeviceAssignment(request);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	return helper.convert(created, getAssetManagement());
    }

    /**
     * Get device assignment by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{token}")
    public MarshaledDeviceAssignment getDeviceAssignment(@PathVariable String token) throws SiteWhereException {
	IDeviceAssignment existing = assertDeviceAssignment(token);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	helper.setIncludeDeviceType(true);
	return helper.convert(existing, getAssetManagement());
    }

    /**
     * Delete an existing device assignment.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{token}")
    public MarshaledDeviceAssignment deleteDeviceAssignment(@PathVariable String token) throws SiteWhereException {
	IDeviceAssignment existing = assertDeviceAssignment(token);
	IDeviceAssignment assignment = getDeviceManagement().deleteDeviceAssignment(existing.getId());
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	return helper.convert(assignment, getAssetManagement());
    }

    /**
     * Update an existing device assignment.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{token}")
    public MarshaledDeviceAssignment updateDeviceAssignment(@PathVariable String token,
	    @RequestBody DeviceAssignmentCreateRequest request) throws SiteWhereException {
	IDeviceAssignment existing = assertDeviceAssignment(token);
	IDeviceAssignment result = getDeviceManagement().updateDeviceAssignment(existing.getId(), request);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	return helper.convert(result, getAssetManagement());
    }

    /**
     * Get label for assignment based on a specific generator.
     * 
     * @param token
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{token}/label/{generatorId}")
    public ResponseEntity<?> getAssignmentLabel(@PathVariable String token, @PathVariable String generatorId)
	    throws SiteWhereException {
	IDeviceAssignment existing = assertDeviceAssignment(token);
	ILabel label = getLabelGeneration().getDeviceAssignmentLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label.getContent());
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
    @GetMapping
    public SearchResults<IDeviceAssignment> listAssignments(@RequestParam(required = false) String deviceToken,
	    @RequestParam(required = false) String customerToken, @RequestParam(required = false) String areaToken,
	    @RequestParam(required = false) String assetToken,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDevice,
	    @RequestParam(defaultValue = "false", required = false) boolean includeCustomer,
	    @RequestParam(defaultValue = "false", required = false) boolean includeArea,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
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
	return new SearchResults<IDeviceAssignment>(results, matches.getNumResults());
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
    @PostMapping("/search")
    public SearchResults<IDeviceAssignment> searchDeviceAssignments(
	    @RequestParam(defaultValue = "false", required = false) boolean includeDevice,
	    @RequestParam(defaultValue = "false", required = false) boolean includeCustomer,
	    @RequestParam(defaultValue = "false", required = false) boolean includeArea,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset,
	    @RequestParam(defaultValue = "1", required = false) Integer page,
	    @RequestParam(defaultValue = "100", required = false) Integer pageSize,
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
	return new SearchResults<IDeviceAssignment>(results, matches.getNumResults());
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
    @PostMapping("/search/summaries")
    public SearchResults<IDeviceAssignmentSummary> searchDeviceAssignmentSummaries(
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset,
	    @RequestParam(defaultValue = "1", required = false) Integer page,
	    @RequestParam(defaultValue = "100", required = false) Integer pageSize,
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
	return new SearchResults<IDeviceAssignmentSummary>(results, matches.getNumResults());
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
    @PostMapping("/bulk/measurements")
    public ISearchResults<IDeviceMeasurement> listMeasurementsForAssignments(
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	return getDeviceEventManagement().listDeviceMeasurementsForIndex(DeviceEventIndex.Assignment, ids, criteria);
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
    @GetMapping("/{token}/measurements")
    public ISearchResults<IDeviceMeasurement> listMeasurementsForAssignment(@PathVariable String token,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	return getDeviceEventManagement().listDeviceMeasurementsForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria);
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
    @GetMapping("/bulk/measurements/series")
    public Map<String, List<IChartSeries<Double>>> listMeasurementsForAssignmentsAsChartSeries(
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
	    @RequestParam(required = false) String[] measurementIds, @RequestBody DeviceAssignmentBulkRequest bulk)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	Map<String, List<IChartSeries<Double>>> results = new HashMap<String, List<IChartSeries<Double>>>();
	for (String token : bulk.getDeviceAssignmentTokens()) {
	    IDeviceAssignment assignment = assertDeviceAssignment(token);
	    ISearchResults<IDeviceMeasurement> measurements = getDeviceEventManagement().listDeviceMeasurementsForIndex(
		    DeviceEventIndex.Assignment, Collections.singletonList(assignment.getId()), criteria);
	    ChartBuilder builder = new ChartBuilder();
	    results.put(token, builder.process(measurements.getResults(), measurementIds));
	}
	return results;
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
    @GetMapping("/{token}/measurements/series")
    public List<IChartSeries<Double>> listMeasurementsForAssignmentAsChartSeries(@PathVariable String token,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
	    @RequestParam(required = false) String[] measurementIds) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	ISearchResults<IDeviceMeasurement> measurements = getDeviceEventManagement().listDeviceMeasurementsForIndex(
		DeviceEventIndex.Assignment, Collections.singletonList(assignment.getId()), criteria);
	ChartBuilder builder = new ChartBuilder();
	return builder.process(measurements.getResults(), measurementIds);
    }

    /**
     * Create measurement to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/measurements")
    public IDeviceMeasurement createMeasurement(@RequestBody DeviceMeasurementCreateRequest input,
	    @PathVariable String token) throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(createRestSourceId(),
		getDeviceManagement(), assignment);
	return getDeviceEventManagement().addDeviceMeasurements(context, input).get(0);
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
    @PostMapping("/bulk/locations")
    public ISearchResults<IDeviceLocation> listLocationsForAssignments(
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return getDeviceEventManagement().listDeviceLocationsForIndex(DeviceEventIndex.Assignment, ids, criteria);
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
    @GetMapping("/{token}/locations")
    public ISearchResults<IDeviceLocation> listLocationsForAssignment(@PathVariable String token,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return getDeviceEventManagement().listDeviceLocationsForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria);
    }

    /**
     * Create location to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/locations")
    public IDeviceLocation createLocation(@RequestBody DeviceLocationCreateRequest input, @PathVariable String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(createRestSourceId(),
		getDeviceManagement(), assignment);
	return getDeviceEventManagement().addDeviceLocations(context, input).get(0);
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
    @PostMapping("/bulk/alerts")
    public ISearchResults<IDeviceAlert> listAlertsForAssignments(
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return getDeviceEventManagement().listDeviceAlertsForIndex(DeviceEventIndex.Assignment, ids, criteria);
    }

    /**
     * List device alerts for a given assignment.
     * 
     * @param token
     * @param page
     * @param pageSize
     * @param startDate
     * @return
     */
    @GetMapping("/{token}/alerts")
    public ISearchResults<IDeviceAlert> listAlertsForAssignment(@PathVariable String token,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return getDeviceEventManagement().listDeviceAlertsForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria);
    }

    /**
     * Create alert to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/alerts")
    public IDeviceAlert createAlert(@RequestBody DeviceAlertCreateRequest input, @PathVariable String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(createRestSourceId(),
		getDeviceManagement(), assignment);
	return getDeviceEventManagement().addDeviceAlerts(context, input).get(0);
    }

    /**
     * Create command invocation to be associated with a device assignment.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/invocations")
    public MarshaledDeviceCommandInvocation createCommandInvocation(
	    @RequestBody DeviceCommandInvocationCreateRequest request, @PathVariable String token)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(createRestSourceId(),
		getDeviceManagement(), assignment);
	IDeviceCommandInvocation result = getDeviceEventManagement().addDeviceCommandInvocations(context, request)
		.get(0);
	DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper(getDeviceManagement());
	return helper.convert(result);
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
    @PostMapping("/{token}/invocations/schedules/{scheduleToken}")
    public IScheduledJob scheduleCommandInvocation(@RequestBody DeviceCommandInvocationCreateRequest request,
	    @PathVariable String token, @PathVariable String scheduleToken) throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	assureDeviceCommand(assignment.getDeviceTypeId(), request.getCommandToken());
	IScheduledJobCreateRequest job = ScheduledJobHelper.createCommandInvocationJob(token, request.getCommandToken(),
		request.getParameterValues(), scheduleToken);
	return getScheduleManagement().createScheduledJob(job);
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
    @PostMapping("/bulk/invocations")
    public SearchResults<IDeviceCommandInvocation> listCommandInvocationsForAssignments(
	    @RequestParam(defaultValue = "true", required = false) boolean includeCommand,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
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
	return new SearchResults<IDeviceCommandInvocation>(converted);
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
    @GetMapping("/{token}/invocations")
    public SearchResults<IDeviceCommandInvocation> listCommandInvocationsForAssignment(@PathVariable String token,
	    @RequestParam(defaultValue = "true", required = false) boolean includeCommand,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
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
	return new SearchResults<IDeviceCommandInvocation>(converted);
    }

    /**
     * Create state change to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/statechanges")
    public IDeviceStateChange createStateChange(@RequestBody DeviceStateChangeCreateRequest input,
	    @PathVariable String token) throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(createRestSourceId(),
		getDeviceManagement(), assignment);
	return getDeviceEventManagement().addDeviceStateChanges(context, input).get(0);
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
    @PostMapping("/bulk/statechanges")
    public ISearchResults<IDeviceStateChange> listStateChangesForAssignments(
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return getDeviceEventManagement().listDeviceStateChangesForIndex(DeviceEventIndex.Assignment, ids, criteria);
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
    @GetMapping("/{token}/statechanges")
    public ISearchResults<IDeviceStateChange> listStateChangesForAssignment(@PathVariable String token,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return getDeviceEventManagement().listDeviceStateChangesForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria);
    }

    /**
     * Create command response to be associated with a device assignment.
     * 
     * @param input
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/responses")
    public IDeviceCommandResponse createCommandResponse(@RequestBody DeviceCommandResponseCreateRequest input,
	    @PathVariable String token) throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(createRestSourceId(),
		getDeviceManagement(), assignment);
	return getDeviceEventManagement().addDeviceCommandResponses(context, input).get(0);
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
    @PostMapping("/bulk/responses")
    public ISearchResults<IDeviceCommandResponse> listCommandResponsesForAssignments(
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
	    @RequestBody DeviceAssignmentBulkRequest bulk) throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	List<UUID> ids = getDeviceAssignmentIds(bulk);
	return getDeviceEventManagement().listDeviceCommandResponsesForIndex(DeviceEventIndex.Assignment, ids,
		criteria);
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
    @GetMapping("/{token}/responses")
    public ISearchResults<IDeviceCommandResponse> listCommandResponsesForAssignment(@PathVariable String token,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
	    throws SiteWhereException {
	IDateRangeSearchCriteria criteria = createDateRangeSearchCriteria(page, pageSize, startDate, endDate);
	IDeviceAssignment assignment = assertDeviceAssignment(token);
	return getDeviceEventManagement().listDeviceCommandResponsesForIndex(DeviceEventIndex.Assignment,
		Collections.singletonList(assignment.getId()), criteria);
    }

    /**
     * End an existing device assignment.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/end")
    public MarshaledDeviceAssignment endDeviceAssignment(@PathVariable String token) throws SiteWhereException {
	IDeviceManagement management = getDeviceManagement();
	IDeviceAssignment existing = assertDeviceAssignment(token);
	IDeviceAssignment updated = management.endDeviceAssignment(existing.getId());
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	helper.setIncludeArea(true);
	return helper.convert(updated, getAssetManagement());
    }

    /**
     * Mark a device assignment as missing.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{token}/missing")
    public MarshaledDeviceAssignment missingDeviceAssignment(@PathVariable String token) throws SiteWhereException {
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
	return helper.convert(updated, getAssetManagement());
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

    /**
     * Source id passed in events generated as side-effect of REST calls.
     * 
     * @return
     */
    protected String createRestSourceId() {
	return "REST:" + UUID.randomUUID().toString();
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