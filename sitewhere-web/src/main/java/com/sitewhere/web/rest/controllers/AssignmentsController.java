/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.core.device.charting.ChartBuilder;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.DeviceAlert;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceEventBatch;
import com.sitewhere.rest.model.device.DeviceLocation;
import com.sitewhere.rest.model.device.DeviceMeasurements;
import com.sitewhere.rest.model.device.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceMeasurements;
import com.sitewhere.spi.device.charting.IChartSeries;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.model.DeviceAssignmentMarshalHelper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for assignment operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/assignments")
@Api(value = "", description = "Operations related to SiteWhere device assignments.")
public class AssignmentsController extends SiteWhereController {

	/**
	 * Used by AJAX calls to create a device assignment.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new device assignment")
	public DeviceAssignment createDeviceAssignment(@RequestBody DeviceAssignmentCreateRequest request)
			throws SiteWhereException {
		if (StringUtils.isEmpty(request.getDeviceHardwareId())) {
			throw new SiteWhereException("Hardware id required.");
		}
		if (StringUtils.isEmpty(request.getSiteToken())) {
			throw new SiteWhereException("Site token required.");
		}
		if (request.getAssignmentType() == null) {
			throw new SiteWhereException("Assignment type required.");
		}
		if ((request.getAssignmentType() != DeviceAssignmentType.Unassociated)
				&& (request.getAssetId() == null)) {
			throw new SiteWhereException("Asset id required.");
		}
		IDeviceManagement management = SiteWhereServer.getInstance().getDeviceManagement();
		IDeviceAssignment created = management.createDeviceAssignment(request);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeDevice(true);
		helper.setIncludeSite(true);
		return helper.convert(created, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Get an assignment by its unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get a device assignment by unique token")
	public DeviceAssignment getDeviceAssignment(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		IDeviceAssignment assignment =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceAssignmentByToken(token);
		if (assignment == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeDevice(true);
		helper.setIncludeSite(true);
		return helper.convert(assignment, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Get an assignment by its unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete a device assignment")
	public DeviceAssignment deleteDeviceAssignment(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
			throws SiteWhereException {
		IDeviceAssignment assignment =
				SiteWhereServer.getInstance().getDeviceManagement().deleteDeviceAssignment(token, force);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeDevice(true);
		helper.setIncludeSite(true);
		return helper.convert(assignment, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Update metadata associated with an assignment.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{token}/metadata", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update metadata for a device assignment")
	public DeviceAssignment updateDeviceAssignmentMetadata(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@RequestBody MetadataProvider metadata) throws SiteWhereException {
		IDeviceAssignment result =
				SiteWhereServer.getInstance().getDeviceManagement().updateDeviceAssignmentMetadata(token,
						metadata);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeDevice(true);
		helper.setIncludeSite(true);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Update latest state information associated with an assignment.
	 * 
	 * @param token
	 * @param batch
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/state", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update current state for a device assignment")
	public DeviceAssignment updateDeviceAssignmentState(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@RequestBody DeviceEventBatch batch) throws SiteWhereException {

		IDeviceAssignment result =
				SiteWhereServer.getInstance().getDeviceManagement().updateDeviceAssignmentState(token, batch);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeDevice(true);
		helper.setIncludeSite(true);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * List all device measurements for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/measurements", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List measurements associated with a device assignment")
	public ISearchResults<IDeviceMeasurements> listAssignmentMeasurements(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
		return SiteWhereServer.getInstance().getDeviceManagement().listDeviceMeasurements(token, criteria);
	}

	/**
	 * List all device measurements for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/measurements/series", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List measurements associated with a device assignment")
	public List<IChartSeries<Double>> listAssignmentMeasurementsAsChartSeries(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
		ISearchResults<IDeviceMeasurements> measurements =
				SiteWhereServer.getInstance().getDeviceManagement().listDeviceMeasurements(token, criteria);
		ChartBuilder builder = new ChartBuilder();
		return builder.process(measurements.getResults());
	}

	/**
	 * Create measurements to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/measurements", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create measurements to be associated with a device assignment")
	public DeviceMeasurements createAssignmentMeasurements(
			@RequestBody DeviceMeasurementsCreateRequest input,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		IDeviceAssignment assignment =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceAssignmentByToken(token);
		IDeviceMeasurements result =
				SiteWhereServer.getInstance().getDeviceManagement().addDeviceMeasurements(assignment, input);
		return DeviceMeasurements.copy(result);
	}

	/**
	 * List all device locations for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/locations", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List recent locations associated with a device assignment")
	public ISearchResults<IDeviceLocation> listAssignmentLocations(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
		return SiteWhereServer.getInstance().getDeviceManagement().listDeviceLocations(token, criteria);
	}

	/**
	 * Create location to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/locations", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a location to be associated with a device assignment")
	public DeviceLocation createAssignmentLocation(@RequestBody DeviceLocationCreateRequest input,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		IDeviceAssignment assignment =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceAssignmentByToken(token);
		IDeviceLocation result =
				SiteWhereServer.getInstance().getDeviceManagement().addDeviceLocation(assignment, input);
		return DeviceLocation.copy(result);
	}

	/**
	 * List all device alerts for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/alerts", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List alerts associated with a device assignment")
	public ISearchResults<IDeviceAlert> listDeviceAlerts(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
		return SiteWhereServer.getInstance().getDeviceManagement().listDeviceAlerts(token, criteria);
	}

	/**
	 * Create alert to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/alerts", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create an alert that will be associated with a device assignment")
	public DeviceAlert createAssignmentAlert(@RequestBody DeviceAlertCreateRequest input,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		IDeviceAssignment assignment =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceAssignmentByToken(token);
		IDeviceAlert result =
				SiteWhereServer.getInstance().getDeviceManagement().addDeviceAlert(assignment, input);
		return DeviceAlert.copy(result);
	}

	/**
	 * End an existing device assignment.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/end", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "End an active device assignment")
	public DeviceAssignment endDeviceAssignment(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		IDeviceManagement management = SiteWhereServer.getInstance().getDeviceManagement();
		IDeviceAssignment updated = management.endDeviceAssignment(token);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeDevice(true);
		helper.setIncludeSite(true);
		return helper.convert(updated, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Mark a device assignment as missing.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/missing", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Mark a device assignment as missing")
	public DeviceAssignment missingDeviceAssignment(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		IDeviceManagement management = SiteWhereServer.getInstance().getDeviceManagement();
		IDeviceAssignment updated =
				management.updateDeviceAssignmentStatus(token, DeviceAssignmentStatus.Missing);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeDevice(true);
		helper.setIncludeSite(true);
		return helper.convert(updated, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Find all assignments near the given point.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/near/{latitude}/{longitude}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find assignments near a given location")
	public ISearchResults<DeviceAssignment> findDeviceAssignmentsNear(
			@PathVariable double latitude,
			@PathVariable double longitude,
			@RequestParam(defaultValue = "10") double maxDistance,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		ISearchResults<IDeviceAssignment> results =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceAssignmentsNear(latitude,
						longitude, maxDistance, criteria);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(false);
		helper.setIncludeDevice(false);
		helper.setIncludeSite(false);
		List<DeviceAssignment> converted = new ArrayList<DeviceAssignment>();
		for (IDeviceAssignment assignment : results.getResults()) {
			converted.add(helper.convert(assignment, SiteWhereServer.getInstance().getAssetModuleManager()));
		}
		return new SearchResults<DeviceAssignment>(converted, results.getNumResults());
	}
}