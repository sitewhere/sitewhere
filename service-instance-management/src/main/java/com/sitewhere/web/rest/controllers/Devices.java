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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import com.sitewhere.microservice.api.device.DeviceAssignmentMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceGroupUtils;
import com.sitewhere.microservice.api.device.DeviceMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceSummaryMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.DeviceSummary;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.marshaling.MarshaledDevice;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceAssignment;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSummary;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Controller for device operations.
 */
@RestController
@RequestMapping("/api/devices")
public class Devices {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Devices.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a device.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    public MarshaledDevice createDevice(@RequestBody DeviceCreateRequest request) throws SiteWhereException {
	IDevice result = getDeviceManagement().createDevice(request);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignments(false);
	return helper.convert(result, getAssetManagement());
    }

    /**
     * Get device by unique token.
     * 
     * @param deviceToken
     * @param includeDeviceType
     * @param includeAssignment
     * @param includeNested
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{deviceToken}")
    public MarshaledDevice getDeviceByToken(@PathVariable String deviceToken,
	    @RequestParam(defaultValue = "true", required = false) boolean includeDeviceType,
	    @RequestParam(defaultValue = "true", required = false) boolean includeAssignment,
	    @RequestParam(defaultValue = "true", required = false) boolean includeNested) throws SiteWhereException {
	IDevice result = assertDeviceByToken(deviceToken);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignments(includeAssignment);
	helper.setIncludeNested(includeNested);
	return helper.convert(result, getAssetManagement());
    }

    /**
     * Update existing device.
     * 
     * @param deviceToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping("/{deviceToken}")
    public MarshaledDevice updateDevice(@PathVariable String deviceToken, @RequestBody DeviceCreateRequest request)
	    throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	IDevice result = getDeviceManagement().updateDevice(existing.getId(), request);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignments(true);
	return helper.convert(result, getAssetManagement());
    }

    /**
     * Get label for device based on a specific generator.
     * 
     * @param deviceToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{deviceToken}/label/{generatorId}")
    public ResponseEntity<?> getDeviceLabel(@PathVariable String deviceToken, @PathVariable String generatorId)
	    throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	ILabel label = getLabelGeneration().getDeviceLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(label.getContent());
    }

    /**
     * Delete device identified by token.
     * 
     * @param deviceToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{deviceToken}")
    public MarshaledDevice deleteDevice(@PathVariable String deviceToken) throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	IDevice result = getDeviceManagement().deleteDevice(existing.getId());
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignments(true);
	return helper.convert(result, getAssetManagement());
    }

    /**
     * List active assignments for a given device.
     * 
     * @param deviceToken
     * @param includeDevice
     * @param includeCustomer
     * @param includeArea
     * @param includeAsset
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{deviceToken}/assignments/active")
    public List<MarshaledDeviceAssignment> getActiveDeviceAssignments(@PathVariable String deviceToken,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDevice,
	    @RequestParam(defaultValue = "false", required = false) boolean includeCustomer,
	    @RequestParam(defaultValue = "false", required = false) boolean includeArea,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset) throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	List<? extends IDeviceAssignment> assignments = getDeviceManagement()
		.getActiveDeviceAssignments(existing.getId());
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);

	List<MarshaledDeviceAssignment> converted = new ArrayList<>();
	for (IDeviceAssignment assignment : assignments) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}

	return converted;
    }

    /**
     * List device assignment history for a given device.
     * 
     * @param deviceToken
     * @param includeDevice
     * @param includeCustomer
     * @param includeArea
     * @param includeAsset
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{deviceToken}/assignments")
    public SearchResults<IDeviceAssignment> listDeviceAssignmentHistory(@PathVariable String deviceToken,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDevice,
	    @RequestParam(defaultValue = "false", required = false) boolean includeCustomer,
	    @RequestParam(defaultValue = "false", required = false) boolean includeArea,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAsset,
	    @RequestParam(defaultValue = "1") int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize) throws SiteWhereException {
	// Create search criteria.
	DeviceAssignmentSearchCriteria criteria = new DeviceAssignmentSearchCriteria(page, pageSize);
	criteria.setDeviceTokens(Collections.singletonList(deviceToken));

	ISearchResults<? extends IDeviceAssignment> history = getDeviceManagement().listDeviceAssignments(criteria);
	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeDevice(includeDevice);
	helper.setIncludeCustomer(includeCustomer);
	helper.setIncludeArea(includeArea);
	helper.setIncludeAsset(includeAsset);

	List<IDeviceAssignment> converted = new ArrayList<IDeviceAssignment>();
	for (IDeviceAssignment assignment : history.getResults()) {
	    converted.add(helper.convert(assignment, getAssetManagement()));
	}
	return new SearchResults<IDeviceAssignment>(converted, history.getNumResults());
    }

    /**
     * Create a device element mapping.
     * 
     * @param deviceToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{deviceToken}/mappings")
    public MarshaledDevice addDeviceElementMapping(@PathVariable String deviceToken,
	    @RequestBody DeviceElementMapping request) throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	IDevice updated = getDeviceManagement().createDeviceElementMapping(existing.getId(), request);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignments(false);
	return helper.convert(updated, getAssetManagement());
    }

    /**
     * Delete a device element mapping.
     * 
     * @param deviceToken
     * @param path
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping("/{deviceToken}/mappings/{path}")
    public MarshaledDevice deleteDeviceElementMapping(@PathVariable String deviceToken, @PathVariable String path)
	    throws SiteWhereException {
	IDevice existing = assertDeviceByToken(deviceToken);
	IDevice updated = getDeviceManagement().deleteDeviceElementMapping(existing.getId(), path);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeAssignments(false);
	return helper.convert(updated, getAssetManagement());
    }

    /**
     * List devices that match given criteria.
     * 
     * @param deviceType
     * @param excludeAssigned
     * @param includeDeviceType
     * @param includeAssignment
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public SearchResults<IDevice> listDevices(@RequestParam(required = false) String deviceType,
	    @RequestParam(defaultValue = "false", required = false) boolean excludeAssigned,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDeviceType,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAssignment,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
	    throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(deviceType, excludeAssigned, page, pageSize,
		Assignments.parseDateOrFail(startDate), Assignments.parseDateOrFail(endDate));
	ISearchResults<? extends IDevice> results = getDeviceManagement().listDevices(criteria);
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignments(includeAssignment);
	List<IDevice> devicesConv = new ArrayList<IDevice>();
	for (IDevice device : results.getResults()) {
	    devicesConv.add(helper.convert(device, getAssetManagement()));
	}
	return new SearchResults<IDevice>(devicesConv, results.getNumResults());
    }

    /**
     * List summary information for devices that meet criteria.
     * 
     * @param deviceType
     * @param excludeAssigned
     * @param includeDeviceType
     * @param includeAssignment
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/summaries")
    public SearchResults<DeviceSummary> listDeviceSummaries(@RequestParam(required = false) String deviceType,
	    @RequestParam(defaultValue = "false", required = false) boolean excludeAssigned,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDeviceType,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAssignment,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
	    throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(deviceType, excludeAssigned, page, pageSize,
		Assignments.parseDateOrFail(startDate), Assignments.parseDateOrFail(endDate));
	ISearchResults<? extends IDeviceSummary> results = getDeviceManagement().listDeviceSummaries(criteria);
	DeviceSummaryMarshalHelper helper = new DeviceSummaryMarshalHelper();
	helper.setIncludeAsset(true);
	List<DeviceSummary> converted = new ArrayList<>();
	for (IDeviceSummary summary : results.getResults()) {
	    converted.add(helper.convert(summary, getAssetManagement()));
	}
	return new SearchResults<DeviceSummary>(converted, results.getNumResults());
    }

    /**
     * List devices assigned to a group.
     * 
     * @param groupToken
     * @param deviceType
     * @param includeDeleted
     * @param excludeAssigned
     * @param includeDeviceType
     * @param includeAssignment
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/group/{groupToken}")
    public SearchResults<IDevice> listDevicesForGroup(@PathVariable String groupToken,
	    @RequestParam(required = false) String deviceType,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDeleted,
	    @RequestParam(defaultValue = "false", required = false) boolean excludeAssigned,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDeviceType,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAssignment,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
	    throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(deviceType, excludeAssigned, page, pageSize,
		Assignments.parseDateOrFail(startDate), Assignments.parseDateOrFail(endDate));
	IDeviceGroup group = assertDeviceGroup(groupToken);
	List<IDevice> matches = DeviceGroupUtils.getDevicesInGroup(group, criteria, getDeviceManagement(),
		getAssetManagement());
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignments(includeAssignment);
	List<IDevice> devicesConv = new ArrayList<IDevice>();
	for (IDevice device : matches) {
	    devicesConv.add(helper.convert(device, getAssetManagement()));
	}
	return new SearchResults<IDevice>(devicesConv, matches.size());
    }

    /**
     * List devices in groups with role.
     * 
     * @param role
     * @param deviceType
     * @param includeDeleted
     * @param excludeAssigned
     * @param includeDeviceType
     * @param includeAssignment
     * @param page
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/grouprole/{role}")
    public SearchResults<IDevice> listDevicesForGroupsWithRole(@PathVariable String role,
	    @RequestParam String deviceType,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDeleted,
	    @RequestParam(defaultValue = "false", required = false) boolean excludeAssigned,
	    @RequestParam(defaultValue = "false", required = false) boolean includeDeviceType,
	    @RequestParam(defaultValue = "false", required = false) boolean includeAssignment,
	    @RequestParam(defaultValue = "1", required = false) int page,
	    @RequestParam(defaultValue = "100", required = false) int pageSize,
	    @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
	    throws SiteWhereException {
	IDeviceSearchCriteria criteria = new DeviceSearchCriteria(deviceType, excludeAssigned, page, pageSize,
		Assignments.parseDateOrFail(startDate), Assignments.parseDateOrFail(endDate));
	Collection<IDevice> matches = DeviceGroupUtils.getDevicesInGroupsWithRole(role, criteria, getDeviceManagement(),
		getAssetManagement());
	DeviceMarshalHelper helper = new DeviceMarshalHelper(getDeviceManagement());
	helper.setIncludeDeviceType(includeDeviceType);
	helper.setIncludeAssignments(includeAssignment);
	List<IDevice> devicesConv = new ArrayList<IDevice>();
	for (IDevice device : matches) {
	    devicesConv.add(helper.convert(device, getAssetManagement()));
	}
	return new SearchResults<IDevice>(devicesConv, matches.size());
    }

    /**
     * Add a batch of events for the current assignment of the given device. Note
     * that the token in the URL overrides the one specified in the
     * {@link DeviceEventBatch} object.
     * 
     * @param deviceToken
     * @param batch
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{deviceToken}/batch")
    public IDeviceEventBatchResponse addDeviceEventBatch(@PathVariable String deviceToken,
	    @RequestBody DeviceEventBatch batch) throws SiteWhereException {
	IDevice device = assertDeviceByToken(deviceToken);
	List<? extends IDeviceAssignment> active = getDeviceManagement().getActiveDeviceAssignments(device.getId());
	if (active.size() == 0) {
	    throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.ERROR);
	}
	List<? extends IDeviceAssignment> assignments = getDeviceManagement()
		.getActiveDeviceAssignments(device.getId());

	IDeviceEventBatchResponse response = null;
	for (IDeviceAssignment assignment : assignments) {
	    // Set event dates if not set by client.
	    for (IDeviceLocationCreateRequest locReq : batch.getLocations()) {
		if (locReq.getEventDate() == null) {
		    ((DeviceLocationCreateRequest) locReq).setEventDate(new Date());
		}
	    }
	    for (IDeviceMeasurementCreateRequest measReq : batch.getMeasurements()) {
		if (measReq.getEventDate() == null) {
		    ((DeviceMeasurementCreateRequest) measReq).setEventDate(new Date());
		}
	    }
	    for (IDeviceAlertCreateRequest alertReq : batch.getAlerts()) {
		if (alertReq.getEventDate() == null) {
		    ((DeviceAlertCreateRequest) alertReq).setEventDate(new Date());
		}
	    }

	    IDeviceEventContext context = DeviceEventRequestBuilder.getContextForAssignment(createEventBatchSourceId(),
		    getDeviceManagement(), assignment);
	    response = getDeviceEventManagement().addDeviceEventBatch(context, batch);
	}

	// TODO: Only returns the last response. Should this be refactored?
	return response;
    }

    /**
     * Source id passed in events generated as side-effect of event batch
     * operations.
     * 
     * @return
     */
    protected String createEventBatchSourceId() {
	return "EVENTBATCH:" + UUID.randomUUID().toString();
    }

    /**
     * Gets a device by unique token and throws an exception if not found.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    protected IDevice assertDeviceByToken(String token) throws SiteWhereException {
	IDevice result = getDeviceManagement().getDeviceByToken(token);
	if (result == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceToken, ErrorLevel.ERROR);
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