/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for device management operations.
 * 
 * @author Derek
 */
public interface IDeviceManagement extends ITenantEngineLifecycleComponent {

    /**
     * Create a new device specification.
     * 
     * @param request
     *            information about new specification
     * @return device specification that was created
     * @throws SiteWhereException
     */
    public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get device specification by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceSpecification getDeviceSpecification(UUID id) throws SiteWhereException;

    /**
     * Get a device specification by unique token.
     * 
     * @param token
     *            unique device specification token
     * @return corresponding specification or null if not found
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException;

    /**
     * Update an existing device specification.
     * 
     * @param token
     *            unique specification id
     * @param request
     *            updated information
     * @return updated device specification
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public IDeviceSpecification updateDeviceSpecification(UUID id, IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException;

    /**
     * List device specifications that match the search criteria.
     * 
     * @param includeDeleted
     *            include specifications marked as deleted
     * @param criteria
     *            search criteria
     * @return results corresponding to search criteria
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an existing device specification.
     * 
     * @param token
     *            unique id
     * @param force
     *            if true, deletes specification. if false, marks as deleted.
     * @return the deleted specification
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public IDeviceSpecification deleteDeviceSpecification(UUID id, boolean force) throws SiteWhereException;

    /**
     * Creates a device command associated with an existing device specification.
     * 
     * @param specificationId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand createDeviceCommand(UUID specificationId, IDeviceCommandCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get device command by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand getDeviceCommand(UUID id) throws SiteWhereException;

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
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand updateDeviceCommand(UUID id, IDeviceCommandCreateRequest request) throws SiteWhereException;

    /**
     * List device command objects associated with a device specification.
     * 
     * @param specificationId
     * @param includeDeleted
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceCommand> listDeviceCommands(UUID specificationId, boolean includeDeleted)
	    throws SiteWhereException;

    /**
     * Delete an existing device command.
     * 
     * @param id
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand deleteDeviceCommand(UUID id, boolean force) throws SiteWhereException;

    /**
     * Creates a device status associated with an existing device specification.
     * 
     * @param specificationId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus createDeviceStatus(UUID specificationId, IDeviceStatusCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get a device status by unique code.
     * 
     * @param specificationId
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus getDeviceStatusByCode(UUID specificationId, String code) throws SiteWhereException;

    /**
     * Update an existing device status.
     * 
     * @param specificationId
     * @param code
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus updateDeviceStatus(UUID specificationId, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException;

    /**
     * List device statuses associated with a device specification.
     * 
     * @param specificationId
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceStatus> listDeviceStatuses(UUID specificationId) throws SiteWhereException;

    /**
     * Delete an existing device status.
     * 
     * @param specificationId
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus deleteDeviceStatus(UUID specificationId, String code) throws SiteWhereException;

    /**
     * Create a new device.
     * 
     * @param device
     * @return
     * @throws SiteWhereException
     */
    public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException;

    /**
     * Get device by unique id.
     * 
     * @param deviceId
     * @return
     * @throws SiteWhereException
     */
    public IDevice getDevice(UUID deviceId) throws SiteWhereException;

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
     * @param deviceId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDevice updateDevice(UUID deviceId, IDeviceCreateRequest request) throws SiteWhereException;

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
     * @param deviceId
     * @param mapping
     * @return
     * @throws SiteWhereException
     */
    public IDevice createDeviceElementMapping(UUID deviceId, IDeviceElementMapping mapping) throws SiteWhereException;

    /**
     * Delete an exising {@link IDeviceElementMapping} from a device.
     * 
     * @param deviceId
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public IDevice deleteDeviceElementMapping(UUID deviceId, String path) throws SiteWhereException;

    /**
     * Delete an existing device.
     * 
     * @param deviceId
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public IDevice deleteDevice(UUID deviceId, boolean force) throws SiteWhereException;

    /**
     * Create a new device assignment.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException;

    /**
     * Get device assignment by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment getDeviceAssignment(UUID id) throws SiteWhereException;

    /**
     * Get a device assignment by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException;

    /**
     * Gets the current assignment for a device. Null if none.
     * 
     * @param deviceId
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment getCurrentDeviceAssignment(UUID deviceId) throws SiteWhereException;

    /**
     * Delete a device assignment. Depending on 'force' flag the assignment will be
     * marked for delete or actually be deleted.
     * 
     * @param id
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment deleteDeviceAssignment(UUID id, boolean force) throws SiteWhereException;

    /**
     * Update metadata associated with a device assignment.
     * 
     * @param id
     * @param metadata
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment updateDeviceAssignmentMetadata(UUID id, Map<String, String> metadata)
	    throws SiteWhereException;

    /**
     * Update the status of an existing device assignment.
     * 
     * @param id
     * @param status
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment updateDeviceAssignmentStatus(UUID id, DeviceAssignmentStatus status)
	    throws SiteWhereException;

    /**
     * Ends a device assignment.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment endDeviceAssignment(UUID id) throws SiteWhereException;

    /**
     * Get the device assignment history for a given device.
     * 
     * @param deviceId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(UUID deviceId, ISearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Get a list of device assignments for a site.
     * 
     * @param siteId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(UUID siteId,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException;

    /**
     * Get a list of device assignments associated with a given asset.
     * 
     * @param assetReference
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(IAssetReference assetReference,
	    IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException;

    /**
     * Create a new {@link IDeviceStream} associated with an assignment.
     * 
     * @param assignmentId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStream createDeviceStream(UUID assignmentId, IDeviceStreamCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get an exisiting {@link IDeviceStream} for an assignment based on unique
     * stream id. Returns null if not found.
     * 
     * @param assignmentId
     * @param streamId
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStream getDeviceStream(UUID assignmentId, String streamId) throws SiteWhereException;

    /**
     * List device streams for the assignment that meet the given criteria.
     * 
     * @param assignmentId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStream> listDeviceStreams(UUID assignmentId, ISearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Create a site based on the given input.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException;

    /**
     * Get site by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public ISite getSite(UUID id) throws SiteWhereException;

    /**
     * Get a site by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public ISite getSiteByToken(String token) throws SiteWhereException;

    /**
     * Update information for a site.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ISite updateSite(UUID id, ISiteCreateRequest request) throws SiteWhereException;

    /**
     * Get a list of all sites.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete a site based on unique site token. If 'force' is specified, the
     * database object will be deleted, otherwise the deleted flag will be set to
     * true.
     * 
     * @param id
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public ISite deleteSite(UUID id, boolean force) throws SiteWhereException;

    /**
     * Create a new zone.
     * 
     * @param siteId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IZone createZone(UUID siteId, IZoneCreateRequest request) throws SiteWhereException;

    /**
     * Get zone by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IZone getZone(UUID id) throws SiteWhereException;

    /**
     * Get a zone by its unique token.
     * 
     * @param zoneToken
     * @return
     * @throws SiteWhereException
     */
    public IZone getZoneByToken(String zoneToken) throws SiteWhereException;

    /**
     * Update an existing zone.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IZone updateZone(UUID id, IZoneCreateRequest request) throws SiteWhereException;

    /**
     * Get a list of all zones associated with a Site.
     * 
     * @param siteId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IZone> listZones(UUID siteId, ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete a zone given its unique id.
     * 
     * @param id
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public IZone deleteZone(UUID id, boolean force) throws SiteWhereException;

    /**
     * Create a new device group.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException;

    /**
     * Get device group by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceGroup getDeviceGroup(UUID id) throws SiteWhereException;

    /**
     * Get a device group by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IDeviceGroup getDeviceGroupByToken(String token) throws SiteWhereException;

    /**
     * Update an existing device group.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceGroup updateDeviceGroup(UUID id, IDeviceGroupCreateRequest request) throws SiteWhereException;

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
     * @param id
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public IDeviceGroup deleteDeviceGroup(UUID id, boolean force) throws SiteWhereException;

    /**
     * Add elements to a device group.
     * 
     * @param groupId
     * @param elements
     * @param ignoreDuplicates
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceGroupElement> addDeviceGroupElements(UUID groupId,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException;

    /**
     * Remove selected elements from a device group.
     * 
     * @param groupId
     * @param elements
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceGroupElement> removeDeviceGroupElements(UUID groupId,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException;

    /**
     * List device group elements that meet the given criteria.
     * 
     * @param groupId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(UUID groupId, ISearchCriteria criteria)
	    throws SiteWhereException;
}