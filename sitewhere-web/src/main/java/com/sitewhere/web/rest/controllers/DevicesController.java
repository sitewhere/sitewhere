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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.device.marshaling.DeviceMarshalHelper;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.wordnik.swagger.annotations.Api;
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

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DevicesController.class);

	/**
	 * Create a device.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new device")
	public IDevice createDevice(@RequestBody DeviceCreateRequest request) throws SiteWhereException {
		Tracer.start(TracerCategory.WebService, "createDevice", LOGGER);
		IDevice result = SiteWhere.getServer().getDeviceManagement().createDevice(request);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(false);
		helper.setIncludeAssignment(false);
		IDevice retval = helper.convert(result, SiteWhere.getServer().getAssetModuleManager());
		Tracer.stop(LOGGER);
		return retval;
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
			@ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset,
			@ApiParam(value = "Include detailed nested device information", required = false) @RequestParam(defaultValue = "false") boolean includeNested)
			throws SiteWhereException {
		Tracer.start(TracerCategory.WebService, "getDeviceByHardwareId", LOGGER);
		IDevice result = assertDeviceByHardwareId(hardwareId);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeSpecification(includeSpecification);
		helper.setIncludeAsset(includeAsset);
		helper.setIncludeAssignment(includeAssignment);
		helper.setIncludeNested(includeNested);
		IDevice retval = helper.convert(result, SiteWhere.getServer().getAssetModuleManager());
		Tracer.stop(LOGGER);
		return retval;
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
		Tracer.start(TracerCategory.WebService, "updateDevice", LOGGER);
		IDevice result = SiteWhere.getServer().getDeviceManagement().updateDevice(hardwareId, request);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeAssignment(true);
		IDevice retval = helper.convert(result, SiteWhere.getServer().getAssetModuleManager());
		Tracer.stop(LOGGER);
		return retval;
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
		Tracer.start(TracerCategory.WebService, "deleteDevice", LOGGER);
		IDevice result = SiteWhere.getServer().getDeviceManagement().deleteDevice(hardwareId, force);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeAssignment(true);
		IDevice retval = helper.convert(result, SiteWhere.getServer().getAssetModuleManager());
		Tracer.stop(LOGGER);
		return retval;
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
			@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
			@ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset,
			@ApiParam(value = "Include detailed device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
			@ApiParam(value = "Include detailed site information", required = false) @RequestParam(defaultValue = "true") boolean includeSite)
			throws SiteWhereException {
		Tracer.start(TracerCategory.WebService, "getDeviceCurrentAssignment", LOGGER);
		IDevice device = assertDeviceByHardwareId(hardwareId);
		IDeviceAssignment assignment =
				SiteWhere.getServer().getDeviceManagement().getCurrentDeviceAssignment(device);
		if (assignment == null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.INFO,
					HttpServletResponse.SC_NOT_FOUND);
		}
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(includeAsset);
		helper.setIncludeDevice(includeDevice);
		helper.setIncludeSite(includeSite);
		IDeviceAssignment retval = helper.convert(assignment, SiteWhere.getServer().getAssetModuleManager());
		Tracer.stop(LOGGER);
		return retval;
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
		Tracer.start(TracerCategory.WebService, "listDeviceAssignmentHistory", LOGGER);
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		ISearchResults<IDeviceAssignment> history =
				SiteWhere.getServer().getDeviceManagement().getDeviceAssignmentHistory(hardwareId, criteria);
		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
		helper.setIncludeAsset(includeAsset);
		helper.setIncludeDevice(includeDevice);
		helper.setIncludeSite(includeSite);
		List<IDeviceAssignment> converted = new ArrayList<IDeviceAssignment>();
		for (IDeviceAssignment assignment : history.getResults()) {
			converted.add(helper.convert(assignment, SiteWhere.getServer().getAssetModuleManager()));
		}
		ISearchResults<IDeviceAssignment> retval =
				new SearchResults<IDeviceAssignment>(converted, history.getNumResults());
		Tracer.stop(LOGGER);
		return retval;
	}

	/**
	 * Create a new device element mapping.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{hardwareId}/mappings", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new device element mapping")
	public IDevice addDeviceElementMapping(
			@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
			@RequestBody DeviceElementMapping request) throws SiteWhereException {
		Tracer.start(TracerCategory.WebService, "addDeviceElementMapping", LOGGER);
		IDevice updated =
				SiteWhere.getServer().getDeviceManagement().createDeviceElementMapping(hardwareId, request);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(false);
		helper.setIncludeAssignment(false);
		IDevice retval = helper.convert(updated, SiteWhere.getServer().getAssetModuleManager());
		Tracer.stop(LOGGER);
		return retval;
	}

	@RequestMapping(value = "/{hardwareId}/mappings", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete an existing device element mapping")
	public IDevice deleteDeviceElementMapping(
			@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
			@ApiParam(value = "Device element path", required = true) @RequestParam(required = true) String path)
			throws SiteWhereException {
		Tracer.start(TracerCategory.WebService, "deleteDeviceElementMapping", LOGGER);
		IDevice updated =
				SiteWhere.getServer().getDeviceManagement().deleteDeviceElementMapping(hardwareId, path);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(false);
		helper.setIncludeAssignment(false);
		IDevice retval = helper.convert(updated, SiteWhere.getServer().getAssetModuleManager());
		Tracer.stop(LOGGER);
		return retval;
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
		Tracer.start(TracerCategory.WebService, "listDevices", LOGGER);
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		ISearchResults<IDevice> results =
				SiteWhere.getServer().getDeviceManagement().listDevices(includeDeleted, criteria);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(true);
		helper.setIncludeSpecification(includeSpecification);
		helper.setIncludeAssignment(includeAssignment);
		List<IDevice> devicesConv = new ArrayList<IDevice>();
		for (IDevice device : results.getResults()) {
			devicesConv.add(helper.convert(device, SiteWhere.getServer().getAssetModuleManager()));
		}
		ISearchResults<IDevice> retval = new SearchResults<IDevice>(devicesConv, results.getNumResults());
		Tracer.stop(LOGGER);
		return retval;
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
		Tracer.start(TracerCategory.WebService, "addDeviceEventBatch", LOGGER);
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
				SiteWhere.getServer().getDeviceManagement().addDeviceEventBatch(device.getAssignmentToken(),
						batch);
		Tracer.stop(LOGGER);
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
		Tracer.start(TracerCategory.WebService, "listUnassignedDevices", LOGGER);
		SearchCriteria criteria = new SearchCriteria(page, pageSize);
		ISearchResults<IDevice> devices =
				SiteWhere.getServer().getDeviceManagement().listUnassignedDevices(criteria);
		DeviceMarshalHelper helper = new DeviceMarshalHelper();
		helper.setIncludeAsset(false);
		helper.setIncludeAssignment(false);

		List<IDevice> devicesConv = new ArrayList<IDevice>();
		for (IDevice device : devices.getResults()) {
			devicesConv.add(helper.convert(device, SiteWhere.getServer().getAssetModuleManager()));
		}
		ISearchResults<IDevice> retval = new SearchResults<IDevice>(devicesConv, devices.getNumResults());
		Tracer.stop(LOGGER);
		return retval;
	}

	/**
	 * Gets a device by unique hardware id and throws an exception if not found.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDevice assertDeviceByHardwareId(String hardwareId) throws SiteWhereException {
		IDevice result = SiteWhere.getServer().getDeviceManagement().getDeviceByHardwareId(hardwareId);
		if (result == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return result;
	}
}