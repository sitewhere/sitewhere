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
     * lang.String,
     * com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification updateDeviceSpecification(String token, IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().updateDeviceSpecification(token, request);
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
     * lang.String, boolean)
     */
    @Override
    public IDeviceSpecification deleteDeviceSpecification(String token, boolean force) throws SiteWhereException {
	return getDelegate().deleteDeviceSpecification(token, force);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand createDeviceCommand(String specificationToken, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().createDeviceCommand(specificationToken, request);
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
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceCommand(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().updateDeviceCommand(token, request);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.lang.
     * String, boolean)
     */
    @Override
    public List<IDeviceCommand> listDeviceCommands(String token, boolean includeDeleted) throws SiteWhereException {
	return getDelegate().listDeviceCommands(token, includeDeleted);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.lang.
     * String, boolean)
     */
    @Override
    public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
	return getDelegate().deleteDeviceCommand(token, force);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus createDeviceStatus(String specToken, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().createDeviceStatus(specToken, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByCode(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IDeviceStatus getDeviceStatusByCode(String specToken, String code) throws SiteWhereException {
	return getDelegate().getDeviceStatusByCode(specToken, code);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus updateDeviceStatus(String specToken, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().updateDeviceStatus(specToken, code, request);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(java.lang.
     * String)
     */
    @Override
    public List<IDeviceStatus> listDeviceStatuses(String specToken) throws SiteWhereException {
	return getDelegate().listDeviceStatuses(specToken);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IDeviceStatus deleteDeviceStatus(String specToken, String code) throws SiteWhereException {
	return getDelegate().deleteDeviceStatus(specToken, code);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java.lang.
     * String)
     */
    @Override
    public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	return getDelegate().getDeviceByHardwareId(hardwareId);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
	return getDelegate().updateDevice(hardwareId, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment(java.
     * lang.String)
     */
    @Override
    public IDeviceAssignment getCurrentDeviceAssignment(String hardwareId) throws SiteWhereException {
	return getDelegate().getCurrentDeviceAssignment(hardwareId);
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
     * lang.String, com.sitewhere.spi.device.IDeviceElementMapping)
     */
    @Override
    public IDevice createDeviceElementMapping(String hardwareId, IDeviceElementMapping mapping)
	    throws SiteWhereException {
	return getDelegate().createDeviceElementMapping(hardwareId, mapping);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceElementMapping(java.
     * lang.String, java.lang.String)
     */
    @Override
    public IDevice deleteDeviceElementMapping(String hardwareId, String path) throws SiteWhereException {
	return getDelegate().deleteDeviceElementMapping(hardwareId, path);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.lang.String,
     * boolean)
     */
    @Override
    public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
	return getDelegate().deleteDevice(hardwareId, force);
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
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken(java.
     * lang.String)
     */
    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	return getDelegate().getDeviceAssignmentByToken(token);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.lang.
     * String, boolean)
     */
    @Override
    public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
	return getDelegate().deleteDeviceAssignment(token, force);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentMetadata(
     * java.lang.String, java.util.Map)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentMetadata(String token, Map<String, String> metadata)
	    throws SiteWhereException {
	return getDelegate().updateDeviceAssignmentMetadata(token, metadata);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus(java.
     * lang.String, com.sitewhere.spi.device.DeviceAssignmentStatus)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
	    throws SiteWhereException {
	return getDelegate().updateDeviceAssignmentStatus(token, status);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.lang.
     * String)
     */
    @Override
    public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
	return getDelegate().endDeviceAssignment(token);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentHistory(java.
     * lang.String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId, ISearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().getDeviceAssignmentHistory(hardwareId, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(java.
     * lang.String, com.sitewhere.spi.search.device.IAssignmentSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException {
	return getDelegate().getDeviceAssignmentsForSite(siteToken, criteria);
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
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceStream(java.lang.
     * String, com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	return getDelegate().createDeviceStream(assignmentToken, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStream(java.lang.String,
     * java.lang.String)
     */
    @Override
    public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
	return getDelegate().getDeviceStream(assignmentToken, streamId);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceStreams(java.lang.
     * String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().listDeviceStreams(assignmentToken, criteria);
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
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteSite(java.lang.String,
     * boolean)
     */
    @Override
    public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
	return getDelegate().deleteSite(siteToken, force);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateSite(java.lang.String,
     * com.sitewhere.spi.device.request.ISiteCreateRequest)
     */
    @Override
    public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException {
	return getDelegate().updateSite(siteToken, request);
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
     * @see com.sitewhere.spi.device.IDeviceManagement#createZone(java.lang.String,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone createZone(String siteToken, IZoneCreateRequest request) throws SiteWhereException {
	return getDelegate().createZone(siteToken, request);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateZone(java.lang.String,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
	return getDelegate().updateZone(token, request);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.lang.String)
     */
    @Override
    public IZone getZone(String zoneToken) throws SiteWhereException {
	return getDelegate().getZone(zoneToken);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listZones(java.lang.String,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria) throws SiteWhereException {
	return getDelegate().listZones(siteToken, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.lang.String,
     * boolean)
     */
    @Override
    public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
	return getDelegate().deleteZone(zoneToken, force);
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
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceGroup(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request) throws SiteWhereException {
	return getDelegate().updateDeviceGroup(token, request);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.lang.String)
     */
    @Override
    public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
	return getDelegate().getDeviceGroup(token);
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
     * com.sitewhere.spi.device.IDeviceManagement#addDeviceGroupElements(java.lang.
     * String, java.util.List, boolean)
     */
    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	return getDelegate().addDeviceGroupElements(groupToken, elements, ignoreDuplicates);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java.
     * lang.String, java.util.List)
     */
    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	return getDelegate().removeDeviceGroupElements(groupToken, elements);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupElements(java.lang.
     * String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	return getDelegate().listDeviceGroupElements(groupToken, criteria);
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.lang.
     * String, boolean)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
	return getDelegate().deleteDeviceGroup(token, force);
    }
}