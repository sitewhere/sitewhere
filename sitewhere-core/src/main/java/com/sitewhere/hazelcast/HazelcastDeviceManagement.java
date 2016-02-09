/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hazelcast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.hazelcast.core.IMap;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
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
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
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
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IDeviceManagement} that stores data in a Hazelcast in-memory
 * data grid. This implementation works on ephemeral data and does not offer long-term
 * peristence.
 * 
 * @author Derek
 */
public class HazelcastDeviceManagement extends TenantLifecycleComponent implements IDeviceManagement {

	/** Map name for site data */
	public static final String MAP_SITES = "com.sitewhere.Site";

	/** Map name for device specification data */
	public static final String MAP_SPECIFICATIONS = "com.sitewhere.DeviceSpecification";

	/** Map name for device data */
	public static final String MAP_DEVICES = "com.sitewhere.Device";

	/** Map name for device assignment data */
	public static final String MAP_ASSIGNMENTS = "com.sitewhere.DeviceAssignment";

	/** Map name for device group data */
	public static final String MAP_GROUPS = "com.sitewhere.DeviceGroup";

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HazelcastDeviceManagement.class);

	/** Common Hazelcast configuration */
	private SiteWhereHazelcastConfiguration configuration;

	/** Map that stores site information */
	private IMap<String, Site> sites;

	/** Map that stores device specification information */
	private IMap<String, DeviceSpecification> specifications;

	/** Map that stores device information */
	private IMap<String, Device> devices;

	/** Map that stores device information */
	private IMap<String, DeviceAssignment> assignments;

	/** Map that stores device groups */
	private IMap<String, DeviceGroup> groups;

	public HazelcastDeviceManagement() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		if (getConfiguration() == null) {
			throw new SiteWhereException("No Hazelcast configuration provided.");
		}

		this.sites = getConfiguration().getHazelcastInstance().getMap(MAP_SITES);
		this.specifications = getConfiguration().getHazelcastInstance().getMap(MAP_SPECIFICATIONS);
		this.devices = getConfiguration().getHazelcastInstance().getMap(MAP_DEVICES);
		this.assignments = getConfiguration().getHazelcastInstance().getMap(MAP_ASSIGNMENTS);
		this.groups = getConfiguration().getHazelcastInstance().getMap(MAP_GROUPS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceSpecification(com.sitewhere.
	 * spi.device.request.IDeviceSpecificationCreateRequest)
	 */
	@Override
	public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
			throws SiteWhereException {
		String uuid = (request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString();
		DeviceSpecification specification =
				SiteWherePersistence.deviceSpecificationCreateLogic(request, uuid);
		return getSpecifications().put(specification.getToken(), specification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceSpecificationByToken(java.lang.
	 * String)
	 */
	@Override
	public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
		return getSpecifications().get(token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateDeviceSpecification(java.lang.
	 * String, com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
	 */
	@Override
	public IDeviceSpecification updateDeviceSpecification(String token,
			IDeviceSpecificationCreateRequest request) throws SiteWhereException {
		DeviceSpecification specification = getSpecifications().get(token);
		SiteWherePersistence.deviceSpecificationUpdateLogic(request, specification);
		return getSpecifications().put(specification.getToken(), specification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceSpecifications(boolean,
	 * com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		List<DeviceSpecification> list = new ArrayList<DeviceSpecification>();
		list.addAll(getSpecifications().values());
		Collections.sort(list, new Comparator<DeviceSpecification>() {

			@Override
			public int compare(DeviceSpecification a, DeviceSpecification b) {
				return 1 - (a.getCreatedDate().compareTo(b.getCreatedDate()));
			}
		});
		return getSearchResults(list, criteria, IDeviceSpecification.class);
	}

	protected <T, I> ISearchResults<I> getSearchResults(List<T> all, ISearchCriteria criteria,
			Class<I> type) {
		return null;
	}

	@Override
	public IDeviceSpecification deleteDeviceSpecification(String token, boolean force)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceCommand createDeviceCommand(IDeviceSpecification spec, IDeviceCommandCreateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IDeviceCommand> listDeviceCommands(String specToken, boolean includeDeleted)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceAssignment getCurrentDeviceAssignment(IDevice device) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IDevice> listDevices(boolean includeDeleted, IDeviceSearchCriteria criteria)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDevice createDeviceElementMapping(String hardwareId, IDeviceElementMapping mapping)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDevice deleteDeviceElementMapping(String hardwareId, String path) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceAssignment updateDeviceAssignmentMetadata(String token, IMetadataProvider metadata)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceAssignment updateDeviceAssignmentState(String token, IDeviceAssignmentState state)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId,
			ISearchCriteria criteria) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
			ISearchCriteria criteria) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentsWithLastInteraction(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(String siteToken,
			String assetModuleId, String assetId, DeviceAssignmentStatus status, ISearchCriteria criteria)
					throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISite getSiteByToken(String token) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IZone createZone(ISite site, IZoneCreateRequest request) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IZone getZone(String zoneToken) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken,
			ISearchCriteria criteria) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBatchOperation getBatchOperation(String token) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IBatchOperation> listBatchOperations(boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISearchResults<IBatchElement> listBatchElements(String batchToken,
			IBatchElementSearchCriteria criteria) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBatchElement updateBatchElement(String operationToken, long index,
			IBatchElementUpdateRequest request) throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
			throws SiteWhereException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	public SiteWhereHazelcastConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SiteWhereHazelcastConfiguration configuration) {
		this.configuration = configuration;
	}

	public IMap<String, Site> getSites() {
		return sites;
	}

	public void setSites(IMap<String, Site> sites) {
		this.sites = sites;
	}

	public IMap<String, DeviceSpecification> getSpecifications() {
		return specifications;
	}

	public void setSpecifications(IMap<String, DeviceSpecification> specifications) {
		this.specifications = specifications;
	}

	public IMap<String, Device> getDevices() {
		return devices;
	}

	public void setDevices(IMap<String, Device> devices) {
		this.devices = devices;
	}

	public IMap<String, DeviceAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(IMap<String, DeviceAssignment> assignments) {
		this.assignments = assignments;
	}

	public IMap<String, DeviceGroup> getGroups() {
		return groups;
	}

	public void setGroups(IMap<String, DeviceGroup> groups) {
		this.groups = groups;
	}
}