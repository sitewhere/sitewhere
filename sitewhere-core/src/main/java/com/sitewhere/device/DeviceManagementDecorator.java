/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device;

import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
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
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
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
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Allows classes to inject themselves as a facade around an existing device management
 * implementation. By default all methods just pass calls to the underlying delegate.
 * 
 * @author Derek
 */
public class DeviceManagementDecorator implements IDeviceManagement {

	/** Delegate instance */
	private IDeviceManagement delegate;

	public DeviceManagementDecorator(IDeviceManagement delegate) {
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentId()
	 */
	@Override
	public String getComponentId() {
		return delegate.getComponentId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentType()
	 */
	@Override
	public LifecycleComponentType getComponentType() {
		return delegate.getComponentType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.lifecycle.LifecycleComponent#lifecycleStart()
	 */
	@Override
	public void lifecycleStart() {
		delegate.lifecycleStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		delegate.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#lifecyclePause()
	 */
	@Override
	public void lifecyclePause() {
		delegate.lifecyclePause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#canPause()
	 */
	@Override
	public boolean canPause() throws SiteWhereException {
		return delegate.canPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#pause()
	 */
	@Override
	public void pause() throws SiteWhereException {
		delegate.pause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return delegate.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.lifecycle.LifecycleComponent#lifecycleStop()
	 */
	@Override
	public void lifecycleStop() {
		delegate.lifecycleStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return delegate.getComponentName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleStatus()
	 */
	@Override
	public LifecycleStatus getLifecycleStatus() {
		return delegate.getLifecycleStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleError()
	 */
	@Override
	public SiteWhereException getLifecycleError() {
		return delegate.getLifecycleError();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLifecycleComponents()
	 */
	@Override
	public List<ILifecycleComponent> getLifecycleComponents() {
		return delegate.getLifecycleComponents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		delegate.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.server.lifecycle.ILifecycleComponent#findComponentsOfType(com
	 * .sitewhere.spi.server.lifecycle.LifecycleComponentType)
	 */
	@Override
	public List<ILifecycleComponent> findComponentsOfType(LifecycleComponentType type)
			throws SiteWhereException {
		return delegate.findComponentsOfType(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#logState()
	 */
	@Override
	public void logState() {
		delegate.logState();
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
	public IDeviceSpecification updateDeviceSpecification(String token,
			IDeviceSpecificationCreateRequest request) throws SiteWhereException {
		return delegate.updateDeviceSpecification(token, request);
	}

	@Override
	public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceSpecifications(includeDeleted, criteria);
	}

	@Override
	public IDeviceSpecification deleteDeviceSpecification(String token, boolean force)
			throws SiteWhereException {
		return delegate.deleteDeviceSpecification(token, force);
	}

	@Override
	public IDeviceCommand createDeviceCommand(IDeviceSpecification spec, IDeviceCommandCreateRequest request)
			throws SiteWhereException {
		return delegate.createDeviceCommand(spec, request);
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
	public List<IDeviceCommand> listDeviceCommands(String token, boolean includeDeleted)
			throws SiteWhereException {
		return delegate.listDeviceCommands(token, includeDeleted);
	}

	@Override
	public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
		return delegate.deleteDeviceCommand(token, force);
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
	public IDeviceAssignment getCurrentDeviceAssignment(IDevice device) throws SiteWhereException {
		return delegate.getCurrentDeviceAssignment(device);
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
	public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request)
			throws SiteWhereException {
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
	public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return delegate.getDeviceForAssignment(assignment);
	}

	@Override
	public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return delegate.getSiteForAssignment(assignment);
	}

	@Override
	public IDeviceAssignment updateDeviceAssignmentMetadata(String token, IMetadataProvider metadata)
			throws SiteWhereException {
		return delegate.updateDeviceAssignmentMetadata(token, metadata);
	}

	@Override
	public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
			throws SiteWhereException {
		return delegate.updateDeviceAssignmentStatus(token, status);
	}

	@Override
	public IDeviceAssignment updateDeviceAssignmentState(String token, IDeviceAssignmentState state)
			throws SiteWhereException {
		return delegate.updateDeviceAssignmentState(token, state);
	}

	@Override
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		return delegate.addDeviceEventBatch(assignmentToken, batch);
	}

	@Override
	public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
		return delegate.endDeviceAssignment(token);
	}

	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId,
			ISearchCriteria criteria) throws SiteWhereException {
		return delegate.getDeviceAssignmentHistory(hardwareId, criteria);
	}

	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
			ISearchCriteria criteria) throws SiteWhereException {
		return delegate.getDeviceAssignmentsForSite(siteToken, criteria);
	}

	@Override
	public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException {
		return delegate.getDeviceEventById(id);
	}

	@Override
	public ISearchResults<IDeviceEvent> listDeviceEvents(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceEvents(assignmentToken, criteria);
	}

	@Override
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
		return delegate.addDeviceMeasurements(assignmentToken, measurements);
	}

	@Override
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceMeasurements(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceMeasurementsForSite(siteToken, criteria);
	}

	@Override
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
			throws SiteWhereException {
		return delegate.addDeviceLocation(assignmentToken, request);
	}

	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceLocations(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceLocationsForSite(siteToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceLocations(assignmentTokens, criteria);
	}

	@Override
	public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		return delegate.addDeviceAlert(assignmentToken, request);
	}

	@Override
	public ISearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceAlerts(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceAlertsForSite(siteToken, criteria);
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
	public IDeviceStreamData addDeviceStreamData(String assignmentToken,
			IDeviceStreamDataCreateRequest request) throws SiteWhereException {
		return delegate.addDeviceStreamData(assignmentToken, request);
	}

	@Override
	public ISearchResults<IDeviceStreamData> listDeviceStreamData(String assignmentToken, String streamId,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceStreamData(assignmentToken, streamId, criteria);
	}

	@Override
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
			IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		return delegate.addDeviceCommandInvocation(assignmentToken, command, request);
	}

	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceCommandInvocations(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceCommandInvocationsForSite(siteToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(String invocationId)
			throws SiteWhereException {
		return delegate.listDeviceCommandInvocationResponses(invocationId);
	}

	@Override
	public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
		return delegate.addDeviceCommandResponse(assignmentToken, request);
	}

	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceCommandResponses(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceCommandResponsesForSite(siteToken, criteria);
	}

	@Override
	public IDeviceStateChange addDeviceStateChange(String assignmentToken,
			IDeviceStateChangeCreateRequest request) throws SiteWhereException {
		return delegate.addDeviceStateChange(assignmentToken, request);
	}

	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceStateChanges(assignmentToken, criteria);
	}

	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceStateChangesForSite(siteToken, criteria);
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
	public IZone createZone(ISite site, IZoneCreateRequest request) throws SiteWhereException {
		return delegate.createZone(site, request);
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
	public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria)
			throws SiteWhereException {
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
	public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request)
			throws SiteWhereException {
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
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		return delegate.addDeviceGroupElements(groupToken, elements);
	}

	@Override
	public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		return delegate.removeDeviceGroupElements(groupToken, elements);
	}

	@Override
	public SearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken,
			ISearchCriteria criteria) throws SiteWhereException {
		return delegate.listDeviceGroupElements(groupToken, criteria);
	}

	@Override
	public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
		return delegate.deleteDeviceGroup(token, force);
	}

	@Override
	public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request)
			throws SiteWhereException {
		return delegate.createBatchOperation(request);
	}

	@Override
	public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
			throws SiteWhereException {
		return delegate.updateBatchOperation(token, request);
	}

	@Override
	public IBatchOperation getBatchOperation(String token) throws SiteWhereException {
		return delegate.getBatchOperation(token);
	}

	@Override
	public ISearchResults<IBatchOperation> listBatchOperations(boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		return delegate.listBatchOperations(includeDeleted, criteria);
	}

	@Override
	public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException {
		return delegate.deleteBatchOperation(token, force);
	}

	@Override
	public SearchResults<IBatchElement> listBatchElements(String batchToken,
			IBatchElementSearchCriteria criteria) throws SiteWhereException {
		return delegate.listBatchElements(batchToken, criteria);
	}

	@Override
	public IBatchElement updateBatchElement(String operationToken, long index,
			IBatchElementUpdateRequest request) throws SiteWhereException {
		return delegate.updateBatchElement(operationToken, index, request);
	}

	@Override
	public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
			throws SiteWhereException {
		return delegate.createBatchCommandInvocation(request);
	}

	public IDeviceManagement getDelegate() {
		return delegate;
	}

	public void setDelegate(IDeviceManagement delegate) {
		this.delegate = delegate;
	}
}