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

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.rest.model.device.DeviceEventBatch;
import com.sitewhere.rest.model.device.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.rest.model.DeviceAssignmentMarshalHelper;
import com.sitewhere.web.rest.model.DeviceMarshalHelper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for device operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/devices")
@Api(value = "", description = "Operations related to SiteWhere devices.")
public class DevicesController extends SiteWhereController {

	/**
	 * Create a device.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new device")
	@ApiError(code = HttpServletResponse.SC_NOT_FOUND, reason = "Device references unknown hardware asset id")
	public IDevice createDevice(@RequestBody DeviceCreateRequest request) throws SiteWhereException {
		IDevice result = SiteWhereServer.getInstance().getDeviceManagement().createDevice(request);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(false);
		helper.setIncludeAssignment(false);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Used by AJAX calls to find a device by hardware id.
	 * 
	 * @param hardwareId
	 * @return
	 */
	@RequestMapping(value = "/{hardwareId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get a device by unique hardware id")
	public IDevice getDeviceByHardwareId(
			@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
			@ApiParam(value = "Include specification information", required = false) @RequestParam(defaultValue = "true") boolean includeSpecification,
			@ApiParam(value = "Include assignment if associated", required = false) @RequestParam(defaultValue = "true") boolean includeAssignment,
			@ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset)
			throws SiteWhereException {
		IDevice result = assertDeviceByHardwareId(hardwareId);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeSpecification(includeSpecification);
		helper.setIncludeAsset(includeAsset);
		helper.setIncludeAssignment(includeAssignment);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Update device information.
	 * 
	 * @param hardwareId
	 *            unique hardware id
	 * @param request
	 *            updated information
	 * @return the updated device
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{hardwareId}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update device information")
	public IDevice updateDevice(
			@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
			@RequestBody DeviceCreateRequest request) throws SiteWhereException {
		IDevice result =
				SiteWhereServer.getInstance().getDeviceManagement().updateDevice(hardwareId, request);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeAssignment(true);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * Delete device identified by hardware id.
	 * 
	 * @param hardwareId
	 * @return
	 */
	@RequestMapping(value = "/{hardwareId}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete a device based on unique hardware id")
	public IDevice deleteDevice(
			@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
			throws SiteWhereException {
		IDevice result = SiteWhereServer.getInstance().getDeviceManagement().deleteDevice(hardwareId, force);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeAssignment(true);
		return helper.convert(result, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * List device assignment history for a given device hardware id.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{hardwareId}/assignment", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get the current assignment for a device")
	public IDeviceAssignment getDeviceCurrentAssignment(
			@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId)
			throws SiteWhereException {
		IDevice device = assertDeviceByHardwareId(hardwareId);
		IDeviceAssignment assignment =
				SiteWhereServer.getInstance().getDeviceManagement().getCurrentDeviceAssignment(device);
		if (assignment == null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.INFO,
					HttpServletResponse.SC_NOT_FOUND);
		}
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeDevice(false);
		helper.setIncludeSite(true);
		return helper.convert(assignment, SiteWhereServer.getInstance().getAssetModuleManager());
	}

	/**
	 * List device assignment history for a given device hardware id.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{hardwareId}/assignments", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get assignment history for a device")
	public ISearchResults<IDeviceAssignment> listDeviceAssignmentHistory(
			@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
			@ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "false") boolean includeAsset,
			@ApiParam(value = "Include detailed device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
			@ApiParam(value = "Include detailed site information", required = false) @RequestParam(defaultValue = "false") boolean includeSite,
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		ISearchResults<IDeviceAssignment> history =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceAssignmentHistory(hardwareId,
						criteria);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(includeAsset);
		helper.setIncludeDevice(includeDevice);
		helper.setIncludeSite(includeSite);
		List<IDeviceAssignment> converted = new ArrayList<IDeviceAssignment>();
		for (IDeviceAssignment assignment : history.getResults()) {
			converted.add(helper.convert(assignment, SiteWhereServer.getInstance().getAssetModuleManager()));
		}
		return new SearchResults<IDeviceAssignment>(converted, history.getNumResults());
	}

	/**
	 * List devices that match given criteria.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List devices that match certain criteria")
	public ISearchResults<IDevice> listDevices(
			@ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
			@ApiParam(value = "Include specification information", required = false) @RequestParam(defaultValue = "false") boolean includeSpecification,
			@ApiParam(value = "Include assignment if associated", required = false) @RequestParam(defaultValue = "false") boolean includeAssignment,
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		ISearchResults<IDevice> results =
				SiteWhereServer.getInstance().getDeviceManagement().listDevices(includeDeleted, criteria);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeSpecification(includeSpecification);
		helper.setIncludeAssignment(includeAssignment);
		List<IDevice> devicesConv = new ArrayList<IDevice>();
		for (IDevice device : results.getResults()) {
			devicesConv.add(helper.convert(device, SiteWhereServer.getInstance().getAssetModuleManager()));
		}
		return new SearchResults<IDevice>(devicesConv, results.getNumResults());
	}

	/**
	 * Add a batch of events for the current assignment of the given device. Note that the
	 * hardware id in the URL overrides the one specified in the {@link DeviceEventBatch}
	 * object.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{hardwareId}/batch", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Send a batch of events for the current assignment of the given device.")
	public IDeviceEventBatchResponse addDeviceEventBatch(
			@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
			@RequestBody DeviceEventBatch batch) throws SiteWhereException {
		IDevice device = assertDeviceByHardwareId(hardwareId);
		if (device.getAssignmentToken() == null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.ERROR);
		}

		// Set event dates if not set by client.
		for (IDeviceLocationCreateRequest locReq : batch.getLocations()) {
			if (locReq.getEventDate() == null) {
				((DeviceLocationCreateRequest) locReq).setEventDate(new Date());
			}
		}
		for (IDeviceMeasurementsCreateRequest measReq : batch.getMeasurements()) {
			if (measReq.getEventDate() == null) {
				((DeviceMeasurementsCreateRequest) measReq).setEventDate(new Date());
			}
		}
		for (IDeviceAlertCreateRequest alertReq : batch.getAlerts()) {
			if (alertReq.getEventDate() == null) {
				((DeviceAlertCreateRequest) alertReq).setEventDate(new Date());
			}
		}

		IDeviceEventBatchResponse response =
				SiteWhereServer.getInstance().getDeviceManagement().addDeviceEventBatch(
						device.getAssignmentToken(), batch);
		return response;
	}

	/**
	 * List all unassigned devices.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/unassigned", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List devices that are not currently assigned")
	public ISearchResults<IDevice> listUnassignedDevices(
			@ApiParam(value = "Page Number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		ISearchResults<IDevice> devices =
				SiteWhereServer.getInstance().getDeviceManagement().listUnassignedDevices(criteria);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(false);
		helper.setIncludeAssignment(false);

		List<IDevice> devicesConv = new ArrayList<IDevice>();
		for (IDevice device : devices.getResults()) {
			devicesConv.add(helper.convert(device, SiteWhereServer.getInstance().getAssetModuleManager()));
		}
		return new SearchResults<IDevice>(devicesConv, devices.getNumResults());
	}

	/**
	 * Gets a device by unique hardware id and throws an exception if not found.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDevice assertDeviceByHardwareId(String hardwareId) throws SiteWhereException {
		IDevice result =
				SiteWhereServer.getInstance().getDeviceManagement().getDeviceByHardwareId(hardwareId);
		if (result == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return result;
	}
}