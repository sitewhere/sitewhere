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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.device.group.DeviceGroupUtils;
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
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for device operations.
 * 
 * @author Derek Adams
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/devices")
@Api(value = "devices")
public class Devices extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Create a device.
     * 
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new device")
    @Secured({ SiteWhereRoles.REST })
    public IDevice createDevice(@RequestBody DeviceCreateRequest request, HttpServletRequest servletRequest)
	    throws SiteWhereException {
	IDevice result = getDeviceManagement().createDevice(request);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(false);
	helper.setIncludeAssignment(false);
	return helper.convert(result, getAssetResolver());
    }

    /**
     * Used by AJAX calls to find a device by hardware id.
     * 
     * @param hardwareId
     * @return
     */
    @RequestMapping(value = "/{hardwareId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get device by unique hardware id")
    @Secured({ SiteWhereRoles.REST })
    public IDevice getDeviceByHardwareId(
	    @ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    @ApiParam(value = "Include device type information", required = false) @RequestParam(defaultValue = "true") boolean includeDeviceType,
	    @ApiParam(value = "Include assignment if associated", required = false) @RequestParam(defaultValue = "true") boolean includeAssignment,
	    @ApiParam(value = "Include site information", required = false) @RequestParam(defaultValue = "true") boolean includeSite,
	    @ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset,
	    @ApiParam(value = "Include detailed nested device information", required = false) @RequestParam(defaultValue = "false") boolean includeNested)
	    throws SiteWhereException {
	IDevice result = assertDeviceByHardwareId(hardwareId);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAsset(includeAsset);
	helper.setIncludeAssignment(includeAssignment);
	helper.setIncludeSite(includeSite);
	helper.setIncludeNested(includeNested);
	return helper.convert(result, getAssetResolver());
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
    @ApiOperation(value = "Update an existing device")
    @Secured({ SiteWhereRoles.REST })
    public IDevice updateDevice(@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    @RequestBody DeviceCreateRequest request, HttpServletRequest servletRequest) throws SiteWhereException {
	IDevice existing = assertDeviceByHardwareId(hardwareId);
	IDevice result = getDeviceManagement().updateDevice(existing.getId(), request);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeAssignment(true);
	return helper.convert(result, getAssetResolver());
    }

    /**
     * Delete device identified by hardware id.
     * 
     * @param hardwareId
     * @return
     */
    @RequestMapping(value = "/{hardwareId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete device based on unique hardware id")
    @Secured({ SiteWhereRoles.REST })
    public IDevice deleteDevice(@ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDevice existing = assertDeviceByHardwareId(hardwareId);
	IDevice result = getDeviceManagement().deleteDevice(existing.getId(), force);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeAssignment(true);
	return helper.convert(result, getAssetResolver());
    }

    /**
     * List device assignment history for a given device hardware id.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{hardwareId}/assignment", method = RequestMethod.GET)
    @ApiOperation(value = "Get current assignment for device")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceAssignment getDeviceCurrentAssignment(
	    @ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    @ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "true") boolean includeAsset,
	    @ApiParam(value = "Include detailed device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
	    @ApiParam(value = "Include detailed site information", required = false) @RequestParam(defaultValue = "true") boolean includeSite,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDevice existing = assertDeviceByHardwareId(hardwareId);
	IDeviceAssignment assignment = assertDeviceAssignment(existing.getDeviceAssignmentId());
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(includeAsset);
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeSite(includeSite);
	return helper.convert(assignment, getAssetResolver());
    }

    /**
     * List device assignment history for a given device hardware id.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{hardwareId}/assignments", method = RequestMethod.GET)
    @ApiOperation(value = "List assignment history for device")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDeviceAssignment> listDeviceAssignmentHistory(
	    @ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    @ApiParam(value = "Include detailed asset information", required = false) @RequestParam(defaultValue = "false") boolean includeAsset,
	    @ApiParam(value = "Include detailed device information", required = false) @RequestParam(defaultValue = "false") boolean includeDevice,
	    @ApiParam(value = "Include detailed site information", required = false) @RequestParam(defaultValue = "false") boolean includeSite,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	IDevice existing = assertDeviceByHardwareId(hardwareId);
	ISearchResults<IDeviceAssignment> history = getDeviceManagement().getDeviceAssignmentHistory(existing.getId(),
		criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(includeAsset);
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeSite(includeSite);
	List<IDeviceAssignment> converted = new ArrayList<IDeviceAssignment>();
	for (IDeviceAssignment assignment : history.getResults()) {
	    converted.add(helper.convert(assignment, getAssetResolver()));
	}
	return new SearchResults<IDeviceAssignment>(converted, history.getNumResults());
    }

    /**
     * Create a new device element mapping.
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/{hardwareId}/mappings", method = RequestMethod.POST)
    @ApiOperation(value = "Create new device element mapping")
    @Secured({ SiteWhereRoles.REST })
    public IDevice addDeviceElementMapping(
	    @ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    @RequestBody DeviceElementMapping request, HttpServletRequest servletRequest) throws SiteWhereException {
	IDevice existing = assertDeviceByHardwareId(hardwareId);
	IDevice updated = getDeviceManagement().createDeviceElementMapping(existing.getId(), request);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(false);
	helper.setIncludeAssignment(false);
	return helper.convert(updated, getAssetResolver());
    }

    @RequestMapping(value = "/{hardwareId}/mappings", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete existing device element mapping")
    @Secured({ SiteWhereRoles.REST })
    public IDevice deleteDeviceElementMapping(
	    @ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    @ApiParam(value = "Device element path", required = true) @RequestParam(required = true) String path,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDevice existing = assertDeviceByHardwareId(hardwareId);
	IDevice updated = getDeviceManagement().deleteDeviceElementMapping(existing.getId(), path);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(false);
	helper.setIncludeAssignment(false);
	return helper.convert(updated, getAssetResolver());
    }

    /**
     * Get default symbol for device.
     * 
     * @param hardwareId
     * @param servletRequest
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{hardwareId}/symbol", method = RequestMethod.GET)
    @ApiOperation(value = "Get default symbol for device")
    public ResponseEntity<byte[]> getDeviceDefaultSymbol(
	    @ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
	// IDevice device = assertDeviceWithoutUserValidation(hardwareId,
	// servletRequest);
	// IEntityUriProvider provider = DefaultEntityUriProvider.getInstance();
	// ISymbolGeneratorManager symbols =
	// getDeviceCommunication().getSymbolGeneratorManager();
	// ISymbolGenerator generator = symbols.getDefaultSymbolGenerator();
	// if (generator != null) {
	// byte[] image = generator.getDeviceSymbol(device, provider);
	//
	// final HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.IMAGE_PNG);
	// return new ResponseEntity<byte[]>(image, headers, HttpStatus.CREATED);
	// } else {
	// return null;
	// }
	return null;
    }

    /**
     * List devices that match given criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List devices that match criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDevice> listDevices(
	    @ApiParam(value = "Device type filter", required = false) @RequestParam(required = false) String deviceType,
	    @ApiParam(value = "Site filter", required = false) @RequestParam(required = false) String site,
	    @ApiParam(value = "Include deleted devices", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeDeleted,
	    @ApiParam(value = "Exclude assigned devices", required = false) @RequestParam(required = false, defaultValue = "false") boolean excludeAssigned,
	    @ApiParam(value = "Include device type information", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeDeviceType,
	    @ApiParam(value = "Include assignment information if associated", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeAssignment,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(deviceType, site, excludeAssigned, page, pageSize,
		startDate, endDate);
	ISearchResults<IDevice> results = getDeviceManagement().listDevices(includeDeleted, criteria);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignment(includeAssignment);
	List<IDevice> devicesConv = new ArrayList<IDevice>();
	for (IDevice device : results.getResults()) {
	    devicesConv.add(helper.convert(device, getAssetResolver()));
	}
	return new SearchResults<IDevice>(devicesConv, results.getNumResults());
    }

    @RequestMapping(value = "/group/{groupToken}", method = RequestMethod.GET)
    @ApiOperation(value = "List devices in device group")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDevice> listDevicesForGroup(
	    @ApiParam(value = "Group token", required = true) @PathVariable String groupToken,
	    @ApiParam(value = "Specification filter", required = false) @RequestParam(required = false) String specification,
	    @ApiParam(value = "Site filter", required = false) @RequestParam(required = false) String site,
	    @ApiParam(value = "Include deleted devices", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeDeleted,
	    @ApiParam(value = "Exclude assigned devices", required = false) @RequestParam(required = false, defaultValue = "false") boolean excludeAssigned,
	    @ApiParam(value = "Include device type information", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeDeviceType,
	    @ApiParam(value = "Include assignment information if associated", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeAssignment,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(specification, site, excludeAssigned, page, pageSize,
		startDate, endDate);
	IDeviceGroup group = assertDeviceGroup(groupToken);
	List<IDevice> matches = DeviceGroupUtils.getDevicesInGroup(group, criteria, getDeviceManagement());
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignment(includeAssignment);
	List<IDevice> devicesConv = new ArrayList<IDevice>();
	for (IDevice device : matches) {
	    devicesConv.add(helper.convert(device, getAssetResolver()));
	}
	return new SearchResults<IDevice>(devicesConv, matches.size());
    }

    @RequestMapping(value = "/grouprole/{role}", method = RequestMethod.GET)
    @ApiOperation(value = "List devices in device groups with role")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IDevice> listDevicesForGroupsWithRole(
	    @ApiParam(value = "Group role", required = true) @PathVariable String role,
	    @ApiParam(value = "Specification filter", required = false) @RequestParam(required = false) String specification,
	    @ApiParam(value = "Site filter", required = false) @RequestParam(required = false) String site,
	    @ApiParam(value = "Include deleted devices", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeDeleted,
	    @ApiParam(value = "Exclude assigned devices", required = false) @RequestParam(required = false, defaultValue = "false") boolean excludeAssigned,
	    @ApiParam(value = "Include device type information", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeDeviceType,
	    @ApiParam(value = "Include assignment information if associated", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeAssignment,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    @ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
	    @ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(specification, site, excludeAssigned, page, pageSize,
		startDate, endDate);
	Collection<IDevice> matches = DeviceGroupUtils.getDevicesInGroupsWithRole(role, criteria,
		getDeviceManagement());
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignment(includeAssignment);
	List<IDevice> devicesConv = new ArrayList<IDevice>();
	for (IDevice device : matches) {
	    devicesConv.add(helper.convert(device, getAssetResolver()));
	}
	return new SearchResults<IDevice>(devicesConv, matches.size());
    }

    /**
     * Add a batch of events for the current assignment of the given device. Note
     * that the hardware id in the URL overrides the one specified in the
     * {@link DeviceEventBatch} object.
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/{hardwareId}/batch", method = RequestMethod.POST)
    @ApiOperation(value = "Add multiple events for device")
    @Secured({ SiteWhereRoles.REST })
    public IDeviceEventBatchResponse addDeviceEventBatch(
	    @ApiParam(value = "Hardware id", required = true) @PathVariable String hardwareId,
	    @RequestBody DeviceEventBatch batch) throws SiteWhereException {
	IDevice device = assertDeviceByHardwareId(hardwareId);
	if (device.getDeviceAssignmentId() == null) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.ERROR);
	}
	IDeviceAssignment assignment = assertDeviceAssignment(device.getDeviceAssignmentId());

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

	return getDeviceEventManagement().addDeviceEventBatch(assignment, batch);
    }

    /**
     * Gets a device by unique hardware id and throws an exception if not found.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected IDevice assertDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	IDevice result = getDeviceManagement().getDeviceByHardwareId(hardwareId);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
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

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }

    private IDeviceEventManagement getDeviceEventManagement() {
	return getMicroservice().getDeviceEventManagementApiDemux().getApiChannel();
    }

    private IAssetResolver getAssetResolver() {
	return getMicroservice().getAssetResolver();
    }
}