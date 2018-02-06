/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
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
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Allows classes to inject themselves as a facade around an existing device
 * management implementation. By default all methods just pass calls to the
 * underlying delegate.
 * 
 * @author Derek
 */
public class DeviceManagementDecorator extends LifecycleComponentDecorator<IDeviceManagement>
	implements IDeviceManagement {

    public DeviceManagementDecorator(IDeviceManagement delegate) {
	super(delegate);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * setTenantEngine(com.sitewhere.spi.microservice.multitenant.
     * IMicroserviceTenantEngine)
     */
    @Override
    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	getDelegate().setTenantEngine(tenantEngine);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngine() {
	return getDelegate().getTenantEngine();
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceSpecification(com.
     * sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().createDeviceSpecification(request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceSpecification(java.util.
     * UUID)
     */
    @Override
    public IDeviceSpecification getDeviceSpecification(UUID id) throws SiteWhereException {
	return getDelegate().getDeviceSpecification(id);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceSpecificationByToken(java
     * .lang.String)
     */
    @Override
    public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
	return getDelegate().getDeviceSpecificationByToken(token);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceSpecification(java.
     * util.UUID,
     * com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification updateDeviceSpecification(UUID id, IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().updateDeviceSpecification(id, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceSpecifications(boolean,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceSpecifications(includeDeleted, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceSpecification(java.
     * util.UUID, boolean)
     */
    @Override
    public IDeviceSpecification deleteDeviceSpecification(UUID id, boolean force) throws SiteWhereException {
	return getDelegate().deleteDeviceSpecification(id, force);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(java.util.
     * UUID, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand createDeviceCommand(UUID specificationdId, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().createDeviceCommand(specificationdId, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommand(java.util.UUID)
     */
    @Override
    public IDeviceCommand getDeviceCommand(UUID id) throws SiteWhereException {
	return getDelegate().getDeviceCommand(id);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.lang.
     * String)
     */
    @Override
    public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
	return getDelegate().getDeviceCommandByToken(token);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceCommand(java.util.
     * UUID, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand updateDeviceCommand(UUID id, IDeviceCommandCreateRequest request) throws SiteWhereException {
	return getDelegate().updateDeviceCommand(id, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.util.UUID,
     * boolean)
     */
    @Override
    public List<IDeviceCommand> listDeviceCommands(UUID specificationdId, boolean includeDeleted)
	    throws SiteWhereException {
	return getDelegate().listDeviceCommands(specificationdId, includeDeleted);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.util.
     * UUID, boolean)
     */
    @Override
    public IDeviceCommand deleteDeviceCommand(UUID id, boolean force) throws SiteWhereException {
	return getDelegate().deleteDeviceCommand(id, force);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus createDeviceStatus(UUID specificationdId, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().createDeviceStatus(specificationdId, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByCode(java.util.
     * UUID, java.lang.String)
     */
    @Override
    public IDeviceStatus getDeviceStatusByCode(UUID specificationdId, String code) throws SiteWhereException {
	return getDelegate().getDeviceStatusByCode(specificationdId, code);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.util.UUID,
     * java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus updateDeviceStatus(UUID specificationdId, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().updateDeviceStatus(specificationdId, code, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(java.util.UUID)
     */
    @Override
    public List<IDeviceStatus> listDeviceStatuses(UUID specificationdId) throws SiteWhereException {
	return getDelegate().listDeviceStatuses(specificationdId);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.util.UUID,
     * java.lang.String)
     */
    @Override
    public IDeviceStatus deleteDeviceStatus(UUID specificationdId, String code) throws SiteWhereException {
	return getDelegate().deleteDeviceStatus(specificationdId, code);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDevice(com.sitewhere.spi.
     * device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
	return getDelegate().createDevice(device);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getDevice(java.util.UUID)
     */
    @Override
    public IDevice getDevice(UUID deviceId) throws SiteWhereException {
	return getDelegate().getDevice(deviceId);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java.lang.
     * String)
     */
    @Override
    public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	return getDelegate().getDeviceByHardwareId(hardwareId);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(UUID id, IDeviceCreateRequest request) throws SiteWhereException {
	return getDelegate().updateDevice(id, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment(java.
     * util.UUID)
     */
    @Override
    public IDeviceAssignment getCurrentDeviceAssignment(UUID id) throws SiteWhereException {
	return getDelegate().getCurrentDeviceAssignment(id);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listDevices(boolean,
     * com.sitewhere.spi.search.device.IDeviceSearchCriteria)
     */
    @Override
    public ISearchResults<IDevice> listDevices(boolean includeDeleted, IDeviceSearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().listDevices(includeDeleted, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceElementMapping(java.
     * util.UUID, com.sitewhere.spi.device.IDeviceElementMapping)
     */
    @Override
    public IDevice createDeviceElementMapping(UUID id, IDeviceElementMapping mapping) throws SiteWhereException {
	return getDelegate().createDeviceElementMapping(id, mapping);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceElementMapping(java.
     * util.UUID, java.lang.String)
     */
    @Override
    public IDevice deleteDeviceElementMapping(UUID id, String path) throws SiteWhereException {
	return getDelegate().deleteDeviceElementMapping(id, path);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.util.UUID,
     * boolean)
     */
    @Override
    public IDevice deleteDevice(UUID id, boolean force) throws SiteWhereException {
	return getDelegate().deleteDevice(id, force);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceAssignment(com.
     * sitewhere.spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	return getDelegate().createDeviceAssignment(request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment getDeviceAssignment(UUID id) throws SiteWhereException {
	return getDelegate().getDeviceAssignment(id);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken(java.
     * lang.String)
     */
    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	return getDelegate().getDeviceAssignmentByToken(token);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.util.
     * UUID, boolean)
     */
    @Override
    public IDeviceAssignment deleteDeviceAssignment(UUID id, boolean force) throws SiteWhereException {
	return getDelegate().deleteDeviceAssignment(id, force);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentMetadata(
     * java.util.UUID, java.util.Map)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentMetadata(UUID id, Map<String, String> metadata)
	    throws SiteWhereException {
	return getDelegate().updateDeviceAssignmentMetadata(id, metadata);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus(java.
     * util.UUID, com.sitewhere.spi.device.DeviceAssignmentStatus)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentStatus(UUID id, DeviceAssignmentStatus status)
	    throws SiteWhereException {
	return getDelegate().updateDeviceAssignmentStatus(id, status);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment endDeviceAssignment(UUID id) throws SiteWhereException {
	return getDelegate().endDeviceAssignment(id);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentHistory(java.
     * util.UUID, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(UUID id, ISearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().getDeviceAssignmentHistory(id, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(java.
     * util.UUID, com.sitewhere.spi.search.device.IAssignmentSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(UUID siteId,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().getDeviceAssignmentsForSite(siteId, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForAsset(com.
     * sitewhere.spi.asset.IAssetReference,
     * com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(IAssetReference assetReference,
	    IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().getDeviceAssignmentsForAsset(assetReference, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStream(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public IDeviceStream createDeviceStream(UUID assignmentId, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().createDeviceStream(assignmentId, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStream(java.util.UUID,
     * java.lang.String)
     */
    @Override
    public IDeviceStream getDeviceStream(UUID assignmentId, String streamId) throws SiteWhereException {
	return getDelegate().getDeviceStream(assignmentId, streamId);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStreams(java.util.UUID,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(UUID assignmentId, ISearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().listDeviceStreams(assignmentId, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#createSite(com.sitewhere.spi.
     * device.request.ISiteCreateRequest)
     */
    @Override
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
	return getDelegate().createSite(request);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteSite(java.util.UUID,
     * boolean)
     */
    @Override
    public ISite deleteSite(UUID id, boolean force) throws SiteWhereException {
	return getDelegate().deleteSite(id, force);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateSite(java.util.UUID,
     * com.sitewhere.spi.device.request.ISiteCreateRequest)
     */
    @Override
    public ISite updateSite(UUID id, ISiteCreateRequest request) throws SiteWhereException {
	return getDelegate().updateSite(id, request);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getSite(java.util.UUID)
     */
    @Override
    public ISite getSite(UUID id) throws SiteWhereException {
	return getDelegate().getSite(id);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getSiteByToken(java.lang.String)
     */
    @Override
    public ISite getSiteByToken(String token) throws SiteWhereException {
	return getDelegate().getSiteByToken(token);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listSites(com.sitewhere.spi.search
     * .ISearchCriteria)
     */
    @Override
    public ISearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listSites(criteria);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#createZone(java.util.UUID,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone createZone(UUID siteId, IZoneCreateRequest request) throws SiteWhereException {
	return getDelegate().createZone(siteId, request);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateZone(java.util.UUID,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone updateZone(UUID id, IZoneCreateRequest request) throws SiteWhereException {
	return getDelegate().updateZone(id, request);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.util.UUID)
     */
    @Override
    public IZone getZone(UUID id) throws SiteWhereException {
	return getDelegate().getZone(id);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getZoneByToken(java.lang.String)
     */
    @Override
    public IZone getZoneByToken(String zoneToken) throws SiteWhereException {
	return getDelegate().getZoneByToken(zoneToken);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listZones(java.util.UUID,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IZone> listZones(UUID siteId, ISearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listZones(siteId, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.util.UUID,
     * boolean)
     */
    @Override
    public IZone deleteZone(UUID id, boolean force) throws SiteWhereException {
	return getDelegate().deleteZone(id, force);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceGroup(com.sitewhere.
     * spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
	return getDelegate().createDeviceGroup(request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceGroup(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup updateDeviceGroup(UUID id, IDeviceGroupCreateRequest request) throws SiteWhereException {
	return getDelegate().updateDeviceGroup(id, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.util.UUID)
     */
    @Override
    public IDeviceGroup getDeviceGroup(UUID id) throws SiteWhereException {
	return getDelegate().getDeviceGroup(id);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceGroupByToken(java.lang.
     * String)
     */
    @Override
    public IDeviceGroup getDeviceGroupByToken(String token) throws SiteWhereException {
	return getDelegate().getDeviceGroupByToken(token);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceGroups(boolean,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().listDeviceGroups(includeDeleted, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupsWithRole(java.lang
     * .String, boolean, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listDeviceGroupsWithRole(role, includeDeleted, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#addDeviceGroupElements(java.util.
     * UUID, java.util.List, boolean)
     */
    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(UUID groupId,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	return getDelegate().addDeviceGroupElements(groupId, elements, ignoreDuplicates);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java.
     * util.UUID, java.util.List)
     */
    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(UUID groupId,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	return getDelegate().removeDeviceGroupElements(groupId, elements);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupElements(java.util.
     * UUID, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(UUID groupId, ISearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().listDeviceGroupElements(groupId, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.util.UUID,
     * boolean)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(UUID id, boolean force) throws SiteWhereException {
	return getDelegate().deleteDeviceGroup(id, force);
    }
}