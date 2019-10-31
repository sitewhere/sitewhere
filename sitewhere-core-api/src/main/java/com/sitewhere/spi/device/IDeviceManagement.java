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
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.customer.request.ICustomerCreateRequest;
import com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.ITreeNode;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;
import com.sitewhere.spi.search.customer.ICustomerSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceCommandSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceStatusSearchCriteria;
import com.sitewhere.spi.search.device.IZoneSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for device management operations.
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
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceType> listDeviceTypes(ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an existing device type.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceType deleteDeviceType(UUID id) throws SiteWhereException;

    /**
     * Create a device command.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand createDeviceCommand(IDeviceCommandCreateRequest request) throws SiteWhereException;

    /**
     * Get device command by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand getDeviceCommand(UUID id) throws SiteWhereException;

    /**
     * Get a device command by token.
     * 
     * @param deviceTypeId
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand getDeviceCommandByToken(UUID deviceTypeId, String token) throws SiteWhereException;

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
     * List device commands that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceCommand> listDeviceCommands(IDeviceCommandSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Delete an existing device command.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand deleteDeviceCommand(UUID id) throws SiteWhereException;

    /**
     * Create a device status.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus createDeviceStatus(IDeviceStatusCreateRequest request) throws SiteWhereException;

    /**
     * Get device status by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus getDeviceStatus(UUID id) throws SiteWhereException;

    /**
     * Get a device status by unique token.
     * 
     * @param deviceTypeId
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus getDeviceStatusByToken(UUID deviceTypeId, String token) throws SiteWhereException;

    /**
     * Update an existing device status.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus updateDeviceStatus(UUID id, IDeviceStatusCreateRequest request) throws SiteWhereException;

    /**
     * List device statuses that match criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStatus> listDeviceStatuses(IDeviceStatusSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Delete an existing device status.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus deleteDeviceStatus(UUID id) throws SiteWhereException;

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
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDevice> listDevices(IDeviceSearchCriteria criteria) throws SiteWhereException;

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
     * @return
     * @throws SiteWhereException
     */
    public IDevice deleteDevice(UUID deviceId) throws SiteWhereException;

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
     * Gets list of active assignments for a device.
     * 
     * @param deviceId
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceAssignment> getActiveDeviceAssignments(UUID deviceId) throws SiteWhereException;

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
     * Delete a device assignment.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment deleteDeviceAssignment(UUID id) throws SiteWhereException;

    /**
     * Create a device alarm.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAlarm createDeviceAlarm(IDeviceAlarmCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing device alarm.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAlarm updateDeviceAlarm(UUID id, IDeviceAlarmCreateRequest request) throws SiteWhereException;

    /**
     * Get device alarm by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAlarm getDeviceAlarm(UUID id) throws SiteWhereException;

    /**
     * Search for device alarms that match criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAlarm> searchDeviceAlarms(IDeviceAlarmSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Delete a device alarm.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAlarm deleteDeviceAlarm(UUID id) throws SiteWhereException;

    /**
     * Create a customer type based on the given input.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ICustomerType createCustomerType(ICustomerTypeCreateRequest request) throws SiteWhereException;

    /**
     * Get customer type by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public ICustomerType getCustomerType(UUID id) throws SiteWhereException;

    /**
     * Get a customer type by alias token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public ICustomerType getCustomerTypeByToken(String token) throws SiteWhereException;

    /**
     * Update information for a customer type.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ICustomerType updateCustomerType(UUID id, ICustomerTypeCreateRequest request) throws SiteWhereException;

    /**
     * Get a list of customer types matching search criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<ICustomerType> listCustomerTypes(ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete a customer type.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public ICustomerType deleteCustomerType(UUID id) throws SiteWhereException;

    /**
     * Create a customer based on the given input.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ICustomer createCustomer(ICustomerCreateRequest request) throws SiteWhereException;

    /**
     * Get customer by unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public ICustomer getCustomer(UUID id) throws SiteWhereException;

    /**
     * Get a customer by alias token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public ICustomer getCustomerByToken(String token) throws SiteWhereException;

    /**
     * Get list of customers that are children of the given area.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public List<ICustomer> getCustomerChildren(String token) throws SiteWhereException;

    /**
     * Update information for a customer.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ICustomer updateCustomer(UUID id, ICustomerCreateRequest request) throws SiteWhereException;

    /**
     * Get a list of customers matching search criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<ICustomer> listCustomers(ICustomerSearchCriteria criteria) throws SiteWhereException;

    /**
     * Get tree structure for customers hierarchy.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<? extends ITreeNode> getCustomersTree() throws SiteWhereException;

    /**
     * Delete a customer.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public ICustomer deleteCustomer(UUID id) throws SiteWhereException;

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
     * Delete an area type.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IAreaType deleteAreaType(UUID id) throws SiteWhereException;

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
     * Get tree structure for areas hierarchy.
     * 
     * @return
     * @throws SiteWhereException
     */
    public List<? extends ITreeNode> getAreasTree() throws SiteWhereException;

    /**
     * Delete an area.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IArea deleteArea(UUID id) throws SiteWhereException;

    /**
     * Create a new zone.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IZone createZone(IZoneCreateRequest request) throws SiteWhereException;

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
     * Get list of all zones that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IZone> listZones(IZoneSearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete a zone.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IZone deleteZone(UUID id) throws SiteWhereException;

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
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceGroup> listDeviceGroups(ISearchCriteria criteria) throws SiteWhereException;

    /**
     * Lists all device groups that have the given role.
     * 
     * @param role
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, ISearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Delete a device group.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public IDeviceGroup deleteDeviceGroup(UUID id) throws SiteWhereException;

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
     * @param elements
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceGroupElement> removeDeviceGroupElements(List<UUID> elements) throws SiteWhereException;

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