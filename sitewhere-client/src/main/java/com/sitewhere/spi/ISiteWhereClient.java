/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi;

import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.DeviceEventBatchResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.AssetSearchResults;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.DeviceAlertSearchResults;
import com.sitewhere.rest.model.search.DeviceAssignmentSearchResults;
import com.sitewhere.rest.model.search.DeviceCommandInvocationSearchResults;
import com.sitewhere.rest.model.search.DeviceCommandSearchResults;
import com.sitewhere.rest.model.search.DeviceGroupElementSearchResults;
import com.sitewhere.rest.model.search.DeviceGroupSearchResults;
import com.sitewhere.rest.model.search.DeviceLocationSearchResults;
import com.sitewhere.rest.model.search.DeviceSearchResults;
import com.sitewhere.rest.model.search.DeviceSpecificationSearchResults;
import com.sitewhere.rest.model.search.DeviceStreamSearchResults;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.ZoneSearchResults;
import com.sitewhere.rest.model.system.Version;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;

/**
 * Interface for SiteWhere client calls.
 * 
 * @author Derek Adams
 */
public interface ISiteWhereClient {

    /**
     * Get SiteWhere version information.
     * 
     * @return
     * @throws SiteWhereException
     */
    public Version getSiteWhereVersion() throws SiteWhereException;

    /**
     * Create a new device specification.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public DeviceSpecification createDeviceSpecification(DeviceSpecificationCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get a device specification by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public DeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException;

    /**
     * Update an existing device specification.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public DeviceSpecification updateDeviceSpecification(String token, DeviceSpecificationCreateRequest request)
	    throws SiteWhereException;

    /**
     * List device specifications that meet the given criteria.
     * 
     * @param includeDeleted
     * @param includeDetailedAssetInfo
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public DeviceSpecificationSearchResults listDeviceSpecifications(boolean includeDeleted,
	    boolean includeDetailedAssetInfo, SearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an existing device specification.
     * 
     * @param token
     * @param deletePermanently
     * @return
     * @throws SiteWhereException
     */
    public DeviceSpecification deleteDeviceSpecification(String token, boolean deletePermanently)
	    throws SiteWhereException;

    /**
     * Create a new device command for a specification.
     * 
     * @param specificationToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public DeviceCommand createDeviceCommand(String specificationToken, DeviceCommandCreateRequest request)
	    throws SiteWhereException;

    /**
     * List all device commands for a device specification.
     * 
     * @param specificationToken
     * @param includeDeleted
     * @return
     * @throws SiteWhereException
     */
    public DeviceCommandSearchResults listDeviceCommands(String specificationToken, boolean includeDeleted)
	    throws SiteWhereException;

    /**
     * Create a new site.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public Site createSite(SiteCreateRequest request) throws SiteWhereException;

    /**
     * Get Site by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public Site getSiteByToken(String token) throws SiteWhereException;

    /**
     * Create a new zone associated with a site.
     * 
     * @param siteToken
     *            unique token for site
     * @param request
     *            information for new zone
     * @return zone that was created.
     * @throws SiteWhereException
     */
    public Zone createZone(String siteToken, ZoneCreateRequest request) throws SiteWhereException;

    /**
     * List zones associated with a given site.
     * 
     * @param siteToken
     * @return
     * @throws SiteWhereException
     */
    public ZoneSearchResults listZonesForSite(String siteToken) throws SiteWhereException;

    /**
     * Create a new device.
     * 
     * @param request
     *            information about device to be created
     * @return the created device
     * @throws SiteWhereException
     */
    public Device createDevice(DeviceCreateRequest request) throws SiteWhereException;

    /**
     * Get a device by its unique hardware id.
     * 
     * @param hardwareId
     *            hardware id of device to return
     * @return device if found or null if not
     * @throws SiteWhereException
     */
    public Device getDeviceByHardwareId(String hardwareId) throws SiteWhereException;

    /**
     * Update information for an existing device.
     * 
     * @param hardwareId
     *            hardware id of device to update
     * @param request
     *            updated information
     * @throws SiteWhereException
     */
    public Device updateDevice(String hardwareId, DeviceCreateRequest request) throws SiteWhereException;

    /**
     * List devices that meet the given criteria.
     * 
     * @param includeDeleted
     * @param excludeAssigned
     * @param populateSpecification
     * @param populateAssignment
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public DeviceSearchResults listDevices(boolean includeDeleted, boolean excludeAssigned,
	    boolean populateSpecification, boolean populateAssignment, DateRangeSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Delete a device.
     * 
     * @param hardwareId
     *            hardware id of device to delete
     * @param force
     *            if true, data is deleted. if false, delete flag is set to true
     * @return
     * @throws SiteWhereException
     */
    public Device deleteDevice(String hardwareId, boolean force) throws SiteWhereException;

    /**
     * Get current device assignment for a device based on hardware id.
     * 
     * @param hardwareId
     *            unique hardware id of device
     * @return device assignment information
     * @throws SiteWhereException
     */
    public DeviceAssignment getCurrentAssignmentForDevice(String hardwareId) throws SiteWhereException;

    /**
     * Get the history of device assignments for a given hardware id.
     * 
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    public DeviceAssignmentSearchResults listDeviceAssignmentHistory(String hardwareId) throws SiteWhereException;

    /**
     * Get all assignments at a site that are associated with a given asset.
     * 
     * @param siteToken
     * @param assetModuleId
     * @param assetId
     * @param status
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public DeviceAssignmentSearchResults getAssignmentsForAsset(String siteToken, String assetModuleId, String assetId,
	    DeviceAssignmentStatus status, SearchCriteria criteria) throws SiteWhereException;

    /**
     * Add a batch of events to the current assignment for the given hardware
     * id.
     * 
     * @param hardwareId
     *            hardware id whose assignment will have events added
     * @param batch
     *            batch of events that will be added
     * @return response of events that were created
     * @throws SiteWhereException
     */
    public DeviceEventBatchResponse addDeviceEventBatch(String hardwareId, DeviceEventBatch batch)
	    throws SiteWhereException;

    /**
     * Create a new device assignment based on the given inputs.
     * 
     * @param request
     *            information about the new assignment
     * @return the assignment that was created.
     * @throws SiteWhereException
     */
    public DeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException;

    /**
     * Get a device assignment by its unique token.
     * 
     * @param assignmentToken
     *            unique assignment token
     * @return the device assignment
     * @throws SiteWhereException
     */
    public DeviceAssignment getDeviceAssignmentByToken(String assignmentToken) throws SiteWhereException;

    /**
     * List all assignments for a site.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public DeviceAssignmentSearchResults listAssignmentsForSite(String token) throws SiteWhereException;

    /**
     * Delete a device assignment based on its unique token.
     * 
     * @param assignmentToken
     *            unique assignment token
     * @param force
     *            value of false sets deleted flag, true deletes data.
     * @return assignment that was deleted
     * @throws SiteWhereException
     */
    public DeviceAssignment deleteDeviceAssignment(String assignmentToken, boolean force) throws SiteWhereException;

    /**
     * Update the metadata for an existing device assignment.
     * 
     * @param token
     * @param metadata
     * @return
     * @throws SiteWhereException
     */
    public DeviceAssignment updateDeviceAssignmentMetadata(String token, MetadataProvider metadata)
	    throws SiteWhereException;

    /**
     * Create measurements for an assignment.
     * 
     * @param assignmentToken
     * @param measurements
     * @return
     * @throws SiteWhereException
     */
    public DeviceMeasurements createDeviceMeasurements(String assignmentToken,
	    DeviceMeasurementsCreateRequest measurements) throws SiteWhereException;

    /**
     * Get most recent device measurements for a given assignment.
     * 
     * @param assignmentToken
     * @param searchCriteria
     * @return
     * @throws SiteWhereException
     */
    public SearchResults<DeviceMeasurements> listDeviceMeasurements(String assignmentToken,
	    DateRangeSearchCriteria searchCriteria) throws SiteWhereException;

    /**
     * Create a new device location for an assignment.
     * 
     * @param assignmentToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public DeviceLocation createDeviceLocation(String assignmentToken, DeviceLocationCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get most recent device locations for a given assignment.
     * 
     * @param assignmentToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public DeviceLocationSearchResults listDeviceLocations(String assignmentToken, DateRangeSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Create a new alert for a device assignment.
     * 
     * @param assignmentToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public DeviceAlert createDeviceAlert(String assignmentToken, DeviceAlertCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get most recent device alerts for a given assignment.
     * 
     * @param assignmentToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public DeviceAlertSearchResults listDeviceAlerts(String assignmentToken, DateRangeSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Create a new device command invocation for a device assignment.
     * 
     * @param assignmentToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public DeviceCommandInvocation createDeviceCommandInvocation(String assignmentToken,
	    DeviceCommandInvocationCreateRequest request) throws SiteWhereException;

    /**
     * List device command invocations that match the given criteria.
     * 
     * @param assignmentToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public DeviceCommandInvocationSearchResults listDeviceCommandInvocations(String assignmentToken,
	    DateRangeSearchCriteria criteria) throws SiteWhereException;

    /**
     * Create a stream that will be associated with a device assignment.
     * 
     * @param assignmentToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public DeviceStream createDeviceStream(String assignmentToken, DeviceStreamCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get a stream from an assignment based on unique id.
     * 
     * @param assignmentToken
     * @param streamId
     * @return
     * @throws SiteWhereException
     */
    public DeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException;

    /**
     * List device streams for an assignment.
     * 
     * @param assignmentToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public DeviceStreamSearchResults listDeviceStreams(String assignmentToken, DateRangeSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Add a chunk of data to a device stream.
     * 
     * @param assignmentToken
     * @param streamId
     * @param sequenceNumber
     * @param data
     * @throws SiteWhereException
     */
    public void addDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber, byte[] data)
	    throws SiteWhereException;

    /**
     * Get a chunk of data from a device stream.
     * 
     * @param assignmentToken
     * @param streamId
     * @param sequenceNumber
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
	    throws SiteWhereException;

    /**
     * List device stream data that meets the given criteria.
     * 
     * @param assignmentToken
     * @param streamId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public byte[] listDeviceStreamData(String assignmentToken, String streamId, DateRangeSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Invokes a command on a list of devices as a batch operation.
     * 
     * @param batchToken
     * @param commandToken
     * @param parameters
     * @param hardwareIds
     * @return
     * @throws SiteWhereException
     */
    public BatchOperation createBatchCommandInvocation(String batchToken, String commandToken,
	    Map<String, String> parameters, List<String> hardwareIds) throws SiteWhereException;

    /**
     * Create a new device group.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroup createDeviceGroup(DeviceGroupCreateRequest request) throws SiteWhereException;

    /**
     * Get a device group by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroup getDeviceGroupByToken(String token) throws SiteWhereException;

    /**
     * Delete a device group by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroup deleteDeviceGroup(String token) throws SiteWhereException;

    /**
     * List device groups that meet the given criteria.
     * 
     * @param role
     * @param criteria
     * @param includeDeleted
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroupSearchResults listDeviceGroups(String role, SearchCriteria criteria, boolean includeDeleted)
	    throws SiteWhereException;

    /**
     * Add elements to an existing device group.
     * 
     * @param groupToken
     * @param elements
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroupElementSearchResults addDeviceGroupElements(String groupToken,
	    List<DeviceGroupElementCreateRequest> elements) throws SiteWhereException;

    /**
     * List device group elements that meet the given criteria.
     * 
     * @param groupToken
     * @param includeDetails
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroupElementSearchResults listDeviceGroupElements(String groupToken, boolean includeDetails,
	    SearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete elements from an existing device group.
     * 
     * @param groupToken
     * @param elements
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroupElementSearchResults deleteDeviceGroupElements(String groupToken,
	    List<DeviceGroupElementCreateRequest> elements) throws SiteWhereException;

    /**
     * List all assets in a given asset module that meet the given criteria.
     * 
     * @param moduleId
     * @return
     * @throws SiteWhereException
     */
    public AssetSearchResults getAssetsByModuleId(String moduleId, String criteria) throws SiteWhereException;
}