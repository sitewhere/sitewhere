/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import java.util.List;
import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.area.request.IAreaCreateRequest;
import com.sitewhere.spi.area.request.IAreaTypeCreateRequest;
import com.sitewhere.spi.area.request.IZoneCreateRequest;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for device management operations.
 * 
 * @author Derek
 */
public interface IDeviceManagement extends ITenantEngineLifecycleComponent {

    /**
     * Create a new device type.
     * 
     * @param request
     *            information about new type
     * @return device type that was created
     * @throws SiteWhereException
     */
    public IDeviceType createDeviceType(IDeviceTypeCreateRequest request) throws SiteWhereException;

    /**
     * Get device type by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceType getDeviceType(UUID id) throws SiteWhereException;

    /**
     * Get a device type by unique token.
     * 
     * @param token
     *            unique device type token
     * @return corresponding device type or null if not found
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public IDeviceType getDeviceTypeByToken(String token) throws SiteWhereException;

    /**
     * Update an existing device type.
     * 
     * @param token
     *            unique device type id
     * @param request
     *            updated information
     * @return updated device type
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public IDeviceType updateDeviceType(UUID id, IDeviceTypeCreateRequest request) throws SiteWhereException;

    /**
     * List device types that match the search criteria.
     * 
     * @param includeDeleted
     *            include types marked as deleted
     * @param criteria
     *            search criteria
     * @return results corresponding to search criteria
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public ISearchResults<IDeviceType> listDeviceTypes(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Delete an existing device type.
     * 
     * @param token
     *            unique id
     * @param force
     *            if true, deletes type. if false, marks as deleted.
     * @return the deleted type
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public IDeviceType deleteDeviceType(UUID id, boolean force) throws SiteWhereException;

    /**
     * Creates a device command associated with an existing device type.
     * 
     * @param deviceTypeId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand createDeviceCommand(UUID deviceTypeId, IDeviceCommandCreateRequest request)
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
     * List device command objects associated with a device type.
     * 
     * @param deviceTypeId
     * @param includeDeleted
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceCommand> listDeviceCommands(UUID deviceTypeId, boolean includeDeleted) throws SiteWhereException;

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
     * Creates a device status associated with an existing device device type.
     * 
     * @param deviceTypeId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus createDeviceStatus(UUID deviceTypeId, IDeviceStatusCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get a device status by unique code.
     * 
     * @param deviceTypeId
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus getDeviceStatusByCode(UUID deviceTypeId, String code) throws SiteWhereException;

    /**
     * Update an existing device status.
     * 
     * @param deviceTypeId
     * @param code
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus updateDeviceStatus(UUID deviceTypeId, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException;

    /**
     * List device statuses associated with a device type.
     * 
     * @param deviceTypeId
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceStatus> listDeviceStatuses(UUID deviceTypeId) throws SiteWhereException;

    /**
     * Delete an existing device status.
     * 
     * @param deviceTypeId
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus deleteDeviceStatus(UUID deviceTypeId, String code) throws SiteWhereException;

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
     * Gets a device by reference token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IDevice getDeviceByToken(String token) throws SiteWhereException;

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
     * Update an existing device assignment.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment updateDeviceAssignment(UUID id, IDeviceAssignmentCreateRequest request)
	    throws SiteWhereException;

    /**
     * List device assignments that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAssignment> listDeviceAssignments(IDeviceAssignmentSearchCriteria criteria)
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
     * Create an area type based on the given input.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IAreaType createAreaType(IAreaTypeCreateRequest request) throws SiteWhereException;

    /**
     * Get area type by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IAreaType getAreaType(UUID id) throws SiteWhereException;

    /**
     * Get a area type by alias token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IAreaType getAreaTypeByToken(String token) throws SiteWhereException;

    /**
     * Update information for an area type.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IAreaType updateAreaType(UUID id, IAreaTypeCreateRequest request) throws SiteWhereException;

    /**
     * Get a list of area types matching search criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IAreaType> listAreaTypes(ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an area type based on unique id. If 'force' is specified, the database
     * object will be deleted, otherwise the deleted flag will be set to true.
     * 
     * @param id
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public IAreaType deleteAreaType(UUID id, boolean force) throws SiteWhereException;

    /**
     * Create an area based on the given input.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IArea createArea(IAreaCreateRequest request) throws SiteWhereException;

    /**
     * Get area by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IArea getArea(UUID id) throws SiteWhereException;

    /**
     * Get a area by alias token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IArea getAreaByToken(String token) throws SiteWhereException;

    /**
     * Get list of areas that are children of the given area.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public List<IArea> getAreaChildren(String token) throws SiteWhereException;

    /**
     * Update information for an area.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IArea updateArea(UUID id, IAreaCreateRequest request) throws SiteWhereException;

    /**
     * Get a list of areas matching search criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IArea> listAreas(IAreaSearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an area based on unique id. If 'force' is specified, the database
     * object will be deleted, otherwise the deleted flag will be set to true.
     * 
     * @param id
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public IArea deleteArea(UUID id, boolean force) throws SiteWhereException;

    /**
     * Create a new zone.
     * 
     * @param areaId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IZone createZone(UUID areaId, IZoneCreateRequest request) throws SiteWhereException;

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
     * Get a list of all zones associated with an area.
     * 
     * @param areaId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IZone> listZones(UUID areaId, ISearchCriteria criteria) throws SiteWhereException;

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