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
public class DeviceManagementDecorator extends LifecycleComponentDecorator implements IDeviceManagement {

    /** Delegate instance */
    private IDeviceManagement delegate;

    public DeviceManagementDecorator(IDeviceManagement delegate) {
	super(delegate);
	this.delegate = delegate;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * setTenantEngine(com.sitewhere.spi.microservice.multitenant.
     * IMicroserviceTenantEngine)
     */
    @Override
    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	delegate.setTenantEngine(tenantEngine);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngine() {
	return delegate.getTenantEngine();
    }

    @Override
    public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	return delegate.createDeviceSpecification(request);
    }

    @Override
    public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
	return delegate.getDeviceSpecificationByToken(token);
    }

    @Override
    public IDeviceSpecification updateDeviceSpecification(String token, IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	return delegate.updateDeviceSpecification(token, request);
    }

    @Override
    public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	return delegate.listDeviceSpecifications(includeDeleted, criteria);
    }

    @Override
    public IDeviceSpecification deleteDeviceSpecification(String token, boolean force) throws SiteWhereException {
	return delegate.deleteDeviceSpecification(token, force);
    }

    @Override
    public IDeviceCommand createDeviceCommand(String specificationToken, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	return delegate.createDeviceCommand(specificationToken, request);
    }

    @Override
    public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
	return delegate.getDeviceCommandByToken(token);
    }

    @Override
    public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	return delegate.updateDeviceCommand(token, request);
    }

    @Override
    public List<IDeviceCommand> listDeviceCommands(String token, boolean includeDeleted) throws SiteWhereException {
	return delegate.listDeviceCommands(token, includeDeleted);
    }

    @Override
    public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
	return delegate.deleteDeviceCommand(token, force);
    }

    @Override
    public IDeviceStatus createDeviceStatus(String specToken, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	return delegate.createDeviceStatus(specToken, request);
    }

    @Override
    public IDeviceStatus getDeviceStatusByCode(String specToken, String code) throws SiteWhereException {
	return delegate.getDeviceStatusByCode(specToken, code);
    }

    @Override
    public IDeviceStatus updateDeviceStatus(String specToken, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	return delegate.updateDeviceStatus(specToken, code, request);
    }

    @Override
    public List<IDeviceStatus> listDeviceStatuses(String specToken) throws SiteWhereException {
	return delegate.listDeviceStatuses(specToken);
    }

    @Override
    public IDeviceStatus deleteDeviceStatus(String specToken, String code) throws SiteWhereException {
	return delegate.deleteDeviceStatus(specToken, code);
    }

    @Override
    public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
	return delegate.createDevice(device);
    }

    @Override
    public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	return delegate.getDeviceByHardwareId(hardwareId);
    }

    @Override
    public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
	return delegate.updateDevice(hardwareId, request);
    }

    @Override
    public IDeviceAssignment getCurrentDeviceAssignment(String hardwareId) throws SiteWhereException {
	return delegate.getCurrentDeviceAssignment(hardwareId);
    }

    @Override
    public ISearchResults<IDevice> listDevices(boolean includeDeleted, IDeviceSearchCriteria criteria)
	    throws SiteWhereException {
	return delegate.listDevices(includeDeleted, criteria);
    }

    @Override
    public IDevice createDeviceElementMapping(String hardwareId, IDeviceElementMapping mapping)
	    throws SiteWhereException {
	return delegate.createDeviceElementMapping(hardwareId, mapping);
    }

    @Override
    public IDevice deleteDeviceElementMapping(String hardwareId, String path) throws SiteWhereException {
	return delegate.deleteDeviceElementMapping(hardwareId, path);
    }

    @Override
    public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
	return delegate.deleteDevice(hardwareId, force);
    }

    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	return delegate.createDeviceAssignment(request);
    }

    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	return delegate.getDeviceAssignmentByToken(token);
    }

    @Override
    public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
	return delegate.deleteDeviceAssignment(token, force);
    }

    @Override
    public IDeviceAssignment updateDeviceAssignmentMetadata(String token, Map<String, String> metadata)
	    throws SiteWhereException {
	return delegate.updateDeviceAssignmentMetadata(token, metadata);
    }

    @Override
    public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
	    throws SiteWhereException {
	return delegate.updateDeviceAssignmentStatus(token, status);
    }

    @Override
    public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
	return delegate.endDeviceAssignment(token);
    }

    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId, ISearchCriteria criteria)
	    throws SiteWhereException {
	return delegate.getDeviceAssignmentHistory(hardwareId, criteria);
    }

    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException {
	return delegate.getDeviceAssignmentsForSite(siteToken, criteria);
    }

    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(IAssetReference assetReference,
	    IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException {
	return delegate.getDeviceAssignmentsForAsset(assetReference, criteria);
    }

    @Override
    public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	return delegate.createDeviceStream(assignmentToken, request);
    }

    @Override
    public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
	return delegate.getDeviceStream(assignmentToken, streamId);
    }

    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	return delegate.listDeviceStreams(assignmentToken, criteria);
    }

    @Override
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
	return delegate.createSite(request);
    }

    @Override
    public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
	return delegate.deleteSite(siteToken, force);
    }

    @Override
    public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException {
	return delegate.updateSite(siteToken, request);
    }

    @Override
    public ISite getSiteByToken(String token) throws SiteWhereException {
	return delegate.getSiteByToken(token);
    }

    @Override
    public ISearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
	return delegate.listSites(criteria);
    }

    @Override
    public IZone createZone(String siteToken, IZoneCreateRequest request) throws SiteWhereException {
	return delegate.createZone(siteToken, request);
    }

    @Override
    public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
	return delegate.updateZone(token, request);
    }

    @Override
    public IZone getZone(String zoneToken) throws SiteWhereException {
	return delegate.getZone(zoneToken);
    }

    @Override
    public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria) throws SiteWhereException {
	return delegate.listZones(siteToken, criteria);
    }

    @Override
    public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
	return delegate.deleteZone(zoneToken, force);
    }

    @Override
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
	return delegate.createDeviceGroup(request);
    }

    @Override
    public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request) throws SiteWhereException {
	return delegate.updateDeviceGroup(token, request);
    }

    @Override
    public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
	return delegate.getDeviceGroup(token);
    }

    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	return delegate.listDeviceGroups(includeDeleted, criteria);
    }

    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	return delegate.listDeviceGroupsWithRole(role, includeDeleted, criteria);
    }

    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	return delegate.addDeviceGroupElements(groupToken, elements, ignoreDuplicates);
    }

    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	return delegate.removeDeviceGroupElements(groupToken, elements);
    }

    @Override
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	return delegate.listDeviceGroupElements(groupToken, criteria);
    }

    @Override
    public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
	return delegate.deleteDeviceGroup(token, force);
    }

    public IDeviceManagement getDelegate() {
	return delegate;
    }

    public void setDelegate(IDeviceManagement delegate) {
	this.delegate = delegate;
    }
}