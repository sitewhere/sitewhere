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
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Interface for device management operations.
 * 
 * @author Derek
 */
public interface IDeviceManagement extends ITenantLifecycleComponent {

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
     *            unique specification token
     * @param request
     *            updated information
     * @return updated device specification
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public IDeviceSpecification updateDeviceSpecification(String token, IDeviceSpecificationCreateRequest request)
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
     *            unique specification token
     * @param force
     *            if true, deletes specification. if false, marks as deleted.
     * @return the deleted specification
     * @throws SiteWhereException
     *             if implementation encountered an error
     */
    public IDeviceSpecification deleteDeviceSpecification(String token, boolean force) throws SiteWhereException;

    /**
     * Creates a device command associated with an existing device specification.
     * 
     * @param specificationToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceCommand createDeviceCommand(String specificationToken, IDeviceCommandCreateRequest request)
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
    public List<IDeviceCommand> listDeviceCommands(String specToken, boolean includeDeleted) throws SiteWhereException;

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
     * Creates a device status associated with an existing device specification.
     * 
     * @param specToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus createDeviceStatus(String specToken, IDeviceStatusCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get a device status by unique code.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus getDeviceStatusByCode(String specToken, String code) throws SiteWhereException;

    /**
     * Update an existing device status.
     * 
     * @param code
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus updateDeviceStatus(String specToken, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException;

    /**
     * List device statuses associated with a device specification.
     * 
     * @param specToken
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceStatus> listDeviceStatuses(String specToken) throws SiteWhereException;

    /**
     * Delete an existing device status.
     * 
     * @param specToken
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStatus deleteDeviceStatus(String specToken, String code) throws SiteWhereException;

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
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException;

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
     * @param hardwareId
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment getCurrentDeviceAssignment(String hardwareId) throws SiteWhereException;

    /**
     * Delete a device assignment. Depending on 'force' flag the assignment will be
     * marked for delete or actually be deleted.
     * 
     * @param token
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException;

    /**
     * Update metadata associated with a device assignment.
     * 
     * @param token
     * @param metadata
     * @return
     * @throws SiteWhereException
     */
    public IDeviceAssignment updateDeviceAssignmentMetadata(String token, Map<String, String> metadata)
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
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId, ISearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Get a list of device assignments for a site.
     * 
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
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
     * @param assignmentToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
	    throws SiteWhereException;

    /**
     * Get an exsiting {@link IDeviceStream} for an assignment based on unique
     * stream id. Returns null if not found.
     * 
     * @param assignmentToken
     * @param streamId
     * @return
     * @throws SiteWhereException
     */
    public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException;

    /**
     * List device streams for the assignment that meet the given criteria.
     * 
     * @param assignmentToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
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
     * @param siteToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException;

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
     * @param siteToken
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException;

    /**
     * Create a new zone.
     * 
     * @param siteToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IZone createZone(String siteToken, IZoneCreateRequest request) throws SiteWhereException;

    /**
     * Get a zone by its unique token.
     * 
     * @param zoneToken
     * @return
     * @throws SiteWhereException
     */
    public IZone getZone(String zoneToken) throws SiteWhereException;

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
     * Get a list of all zones associated with a Site.
     * 
     * @param siteToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria) throws SiteWhereException;

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
     * Get a device network by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException;

    /**
     * Update an existing device group.
     * 
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request) throws SiteWhereException;

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
     * @param ignoreDuplicates
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException;

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
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken, ISearchCriteria criteria)
	    throws SiteWhereException;
}