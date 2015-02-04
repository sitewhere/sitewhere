/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import java.util.List;

import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.batch.IBatchElement;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.device.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.device.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Interface for device operations.
 * 
 * @author Derek
 */
public interface IDeviceManagement extends ILifecycleComponent {

	/**
	 * Create a new device specification.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
			throws SiteWhereException;

	/**
	 * Get a device specification by unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException;

	/**
	 * Update an existing device specification.
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceSpecification updateDeviceSpecification(String token,
			IDeviceSpecificationCreateRequest request) throws SiteWhereException;

	/**
	 * List device specifications that match the search criteria.
	 * 
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException;

	/**
	 * Delete an existing device specification.
	 * 
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceSpecification deleteDeviceSpecification(String token, boolean force)
			throws SiteWhereException;

	/**
	 * Creates a device command associated with an existing device specification.
	 * 
	 * @param spec
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommand createDeviceCommand(IDeviceSpecification spec, IDeviceCommandCreateRequest request)
			throws SiteWhereException;

	/**
	 * Get a device command by unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException;

	/**
	 * Update an existing device command.
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
			throws SiteWhereException;

	/**
	 * List device command objects associated with a device specification.
	 * 
	 * @param specToken
	 * @param includeDeleted
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceCommand> listDeviceCommands(String specToken, boolean includeDeleted)
			throws SiteWhereException;

	/**
	 * Delete an existing device command.
	 * 
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException;

	/**
	 * Create a new device.
	 * 
	 * @param device
	 * @return
	 * @throws SiteWhereException
	 */
	public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException;

	/**
	 * Gets a device by unique hardware id.
	 * 
	 * @param hardwareId
	 * @return
	 * @throws SiteWhereException
	 */
	public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException;

	/**
	 * Update device information.
	 * 
	 * @param hardwareId
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException;

	/**
	 * Gets the current assignment for a device. Null if none.
	 * 
	 * @param device
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAssignment getCurrentDeviceAssignment(IDevice device) throws SiteWhereException;

	/**
	 * List devices that meet the given criteria.
	 * 
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDevice> listDevices(boolean includeDeleted, IDeviceSearchCriteria criteria)
			throws SiteWhereException;

	/**
	 * Create an {@link IDeviceElementMapping} for a nested device.
	 * 
	 * @param hardwareId
	 * @param mapping
	 * @return
	 * @throws SiteWhereException
	 */
	public IDevice createDeviceElementMapping(String hardwareId, IDeviceElementMapping mapping)
			throws SiteWhereException;

	/**
	 * Delete an exising {@link IDeviceElementMapping} from a device.
	 * 
	 * @param hardwareId
	 * @param path
	 * @return
	 * @throws SiteWhereException
	 */
	public IDevice deleteDeviceElementMapping(String hardwareId, String path) throws SiteWhereException;

	/**
	 * Delete an existing device.
	 * 
	 * @param hardwareId
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException;

	/**
	 * Create a new device assignment.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request)
			throws SiteWhereException;

	/**
	 * Get a device assignment by unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException;

	/**
	 * Delete a device assignment. Depending on 'force' flag the assignment will be marked
	 * for delete or actually be deleted.
	 * 
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException;

	/**
	 * Get the device associated with an assignment.
	 * 
	 * @param assignment
	 * @return
	 * @throws SiteWhereException
	 */
	public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException;

	/**
	 * Get the site associated with an assignment.
	 * 
	 * @param assignment
	 * @return
	 * @throws SiteWhereException
	 */
	public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException;

	/**
	 * Update metadata associated with a device assignment.
	 * 
	 * @param token
	 * @param metadata
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAssignment updateDeviceAssignmentMetadata(String token, IMetadataProvider metadata)
			throws SiteWhereException;

	/**
	 * Update the status of an existing device assignment.
	 * 
	 * @param token
	 * @param status
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
			throws SiteWhereException;

	/**
	 * Updates the current state of a device assignment.
	 * 
	 * @param token
	 * @param state
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAssignment updateDeviceAssignmentState(String token, IDeviceAssignmentState state)
			throws SiteWhereException;

	/**
	 * Add a batch of events for the given assignment.
	 * 
	 * @param assignmentToken
	 * @param batch
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException;

	/**
	 * Ends a device assignment.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException;

	/**
	 * Get the device assignment history for a given device.
	 * 
	 * @param hardwareId
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId,
			ISearchCriteria criteria) throws SiteWhereException;

	/**
	 * Get a list of device assignments for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
			ISearchCriteria criteria) throws SiteWhereException;

	/**
	 * Get a device event by unique id.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException;

	/**
	 * List all events for the given assignment that meet the search criteria.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceEvent> listDeviceEvents(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Add measurements for a given device assignment.
	 * 
	 * @param assignmentToken
	 * @param measurements
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException;

	/**
	 * Gets device measurement entries for an assignment based on criteria.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device measurements for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Add location for a given device assignment.
	 * 
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
			throws SiteWhereException;

	/**
	 * Gets device location entries for an assignment.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device locations for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device locations for the given tokens within the given time range.
	 * 
	 * @param assignmentTokens
	 * @param start
	 * @param end
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Add alert for a given device assignment.
	 * 
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
			throws SiteWhereException;

	/**
	 * Gets the most recent device alert entries for an assignment.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device alerts for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Add a device command invocation event for the given assignment.
	 * 
	 * @param assignmentToken
	 * @param command
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
			IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException;

	/**
	 * Gets device command invocations for an assignment based on criteria.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device command invocations for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List responses associated with a command invocation.
	 * 
	 * @param invocationId
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(String invocationId)
			throws SiteWhereException;

	/**
	 * Adds a new device command response event.
	 * 
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException;

	/**
	 * Gets the most recent device command response entries for an assignment.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device command responses for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Adds a new device state change event.
	 * 
	 * @param assignmentToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceStateChange addDeviceStateChange(String assignmentToken,
			IDeviceStateChangeCreateRequest request) throws SiteWhereException;

	/**
	 * Gets the most recent device state change entries for an assignment.
	 * 
	 * @param assignmentToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * List device state changes for a site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Create a site based on the given input.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public ISite createSite(ISiteCreateRequest request) throws SiteWhereException;

	/**
	 * Delete a site based on unique site token. If 'force' is specified, the database
	 * object will be deleted, otherwise the deleted flag will be set to true.
	 * 
	 * @param siteToken
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException;

	/**
	 * Update information for a site.
	 * 
	 * @param siteToken
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException;

	/**
	 * Get a site by unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public ISite getSiteByToken(String token) throws SiteWhereException;

	/**
	 * Get a list of all sites.
	 * 
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException;

	/**
	 * Create a new zone.
	 * 
	 * @param site
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IZone createZone(ISite site, IZoneCreateRequest request) throws SiteWhereException;

	/**
	 * Update an existing zone.
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException;

	/**
	 * Get a zone by its unique token.
	 * 
	 * @param zoneToken
	 * @return
	 * @throws SiteWhereException
	 */
	public IZone getZone(String zoneToken) throws SiteWhereException;

	/**
	 * Get a list of all zones associated with a Site.
	 * 
	 * @param siteToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria)
			throws SiteWhereException;

	/**
	 * Delete a zone given its unique token.
	 * 
	 * @param zoneToken
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException;

	/**
	 * Create a new device group.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException;

	/**
	 * Update an existing device group.
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request)
			throws SiteWhereException;

	/**
	 * Get a device network by unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException;

	/**
	 * List device groups.
	 * 
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
			throws SiteWhereException;

	/**
	 * Lists all device groups that have the given role.
	 * 
	 * @param role
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException;

	/**
	 * Delete a device group.
	 * 
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException;

	/**
	 * Add elements to a device group.
	 * 
	 * @param groupToken
	 * @param elements
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException;

	/**
	 * Remove selected elements from a device group.
	 * 
	 * @param groupToken
	 * @param elements
	 * @return
	 * @throws SiteWhereException
	 */
	public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException;

	/**
	 * List device group elements that meet the given criteria.
	 * 
	 * @param groupToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public SearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken,
			ISearchCriteria criteria) throws SiteWhereException;

	/**
	 * Creates an {@link IBatchOperation} to perform an operation on multiple devices.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request)
			throws SiteWhereException;

	/**
	 * Update an existing {@link IBatchOperation}.
	 * 
	 * @param token
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
			throws SiteWhereException;

	/**
	 * Get an {@link IBatchOperation} by unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public IBatchOperation getBatchOperation(String token) throws SiteWhereException;

	/**
	 * List batch operations based on the given criteria.
	 * 
	 * @param includeDeleted
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<IBatchOperation> listBatchOperations(boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException;

	/**
	 * Deletes a batch operation and its elements.
	 * 
	 * @param token
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException;

	/**
	 * Lists elements for an {@link IBatchOperation} that meet the given criteria.
	 * 
	 * @param batchToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public SearchResults<IBatchElement> listBatchElements(String batchToken,
			IBatchElementSearchCriteria criteria) throws SiteWhereException;

	/**
	 * Updates an existing batch operation element.
	 * 
	 * @param operationToken
	 * @param index
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IBatchElement updateBatchElement(String operationToken, long index,
			IBatchElementUpdateRequest request) throws SiteWhereException;

	/**
	 * Creates a {@link BatchOperation} that will invoke a command on multiple devices.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
			throws SiteWhereException;
}