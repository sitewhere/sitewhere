/*
 * HbaseDeviceManagement.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

import java.util.List;

import org.apache.hadoop.hbase.regionserver.StoreFile.BloomType;
import org.apache.log4j.Logger;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.ICachingDeviceManagement;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceManagementCacheProvider;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
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
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * HBase implementation of SiteWhere device management.
 * 
 * @author Derek
 */
public class HBaseDeviceManagement implements IDeviceManagement, ICachingDeviceManagement {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(HBaseDeviceManagement.class);

	/** Used to communicate with HBase */
	private ISiteWhereHBaseClient client;

	/** Injected cache provider */
	private IDeviceManagementCacheProvider cacheProvider;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	public void start() throws SiteWhereException {
		LOGGER.info("HBase device management starting...");

		LOGGER.info("Verifying tables...");
		ensureTablesExist();

		LOGGER.info("Loading id management...");
		IdManager.getInstance().load(client);

		LOGGER.info("HBase device management started.");
	}

	/**
	 * Make sure that all SiteWhere tables exist, creating them if necessary.
	 * 
	 * @throws SiteWhereException
	 */
	protected void ensureTablesExist() throws SiteWhereException {
		SiteWhereTables.assureTable(client, ISiteWhereHBase.UID_TABLE_NAME, BloomType.ROW);
		SiteWhereTables.assureTable(client, ISiteWhereHBase.SITES_TABLE_NAME, BloomType.ROW);
		SiteWhereTables.assureTable(client, ISiteWhereHBase.EVENTS_TABLE_NAME, BloomType.ROW);
		SiteWhereTables.assureTable(client, ISiteWhereHBase.DEVICES_TABLE_NAME, BloomType.ROW);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	public void stop() throws SiteWhereException {
		LOGGER.info("HBase device management stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.ICachingDeviceManagement#setCacheProvider(com.sitewhere
	 * .spi.device.IDeviceManagementCacheProvider)
	 */
	@Override
	public void setCacheProvider(IDeviceManagementCacheProvider cacheProvider) {
		this.cacheProvider = cacheProvider;
	}

	public IDeviceManagementCacheProvider getCacheProvider() {
		return cacheProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceSpecification(com.sitewhere
	 * .spi.device.request.IDeviceSpecificationCreateRequest)
	 */
	@Override
	public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
			throws SiteWhereException {
		return HBaseDeviceSpecification.createDeviceSpecification(client, request, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceSpecificationByToken(java.lang
	 * .String)
	 */
	@Override
	public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
		return HBaseDeviceSpecification.getDeviceSpecificationByToken(client, token, cacheProvider);
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
		return HBaseDeviceSpecification.updateDeviceSpecification(client, token, request, cacheProvider);
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
		return HBaseDeviceSpecification.listDeviceSpecifications(client, includeDeleted, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceSpecification(java.lang.
	 * String, boolean)
	 */
	@Override
	public IDeviceSpecification deleteDeviceSpecification(String token, boolean force)
			throws SiteWhereException {
		return HBaseDeviceSpecification.deleteDeviceSpecification(client, token, force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(com.sitewhere.spi
	 * .device.IDeviceSpecification,
	 * com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
	 */
	@Override
	public IDeviceCommand createDeviceCommand(IDeviceSpecification spec, IDeviceCommandCreateRequest request)
			throws SiteWhereException {
		return HBaseDeviceCommand.createDeviceCommand(client, spec, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.lang.String
	 * )
	 */
	@Override
	public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
		return HBaseDeviceCommand.getDeviceCommandByToken(client, token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateDeviceCommand(java.lang.String,
	 * com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
	 */
	@Override
	public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
			throws SiteWhereException {
		return HBaseDeviceCommand.updateDeviceCommand(client, token, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.lang.String,
	 * boolean)
	 */
	@Override
	public List<IDeviceCommand> listDeviceCommands(String specToken, boolean includeDeleted)
			throws SiteWhereException {
		return HBaseDeviceCommand.listDeviceCommands(client, specToken, includeDeleted);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.lang.String,
	 * boolean)
	 */
	@Override
	public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
		return HBaseDeviceCommand.deleteDeviceCommand(client, token, force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDevice(com.sitewhere.spi.device
	 * .request.IDeviceCreateRequest)
	 */
	public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
		return HBaseDevice.createDevice(client, device);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java.lang.String)
	 */
	public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
		return HBaseDevice.getDeviceByHardwareId(client, hardwareId, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.lang.String,
	 * com.sitewhere.spi.device.request.IDeviceCreateRequest)
	 */
	public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
		return HBaseDevice.updateDevice(client, hardwareId, request, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment(com.sitewhere
	 * .spi.device.IDevice)
	 */
	public IDeviceAssignment getCurrentDeviceAssignment(IDevice device) throws SiteWhereException {
		String token = HBaseDevice.getCurrentAssignmentId(client, device.getHardwareId());
		if (token == null) {
			return null;
		}
		return HBaseDeviceAssignment.getDeviceAssignment(client, token, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDevices(boolean,
	 * com.sitewhere.spi.common.ISearchCriteria)
	 */
	public SearchResults<IDevice> listDevices(boolean includeDeleted, ISearchCriteria criteria)
			throws SiteWhereException {
		return HBaseDevice.listDevices(client, includeDeleted, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listUnassignedDevices(com.sitewhere.
	 * spi.common.ISearchCriteria)
	 */
	public SearchResults<IDevice> listUnassignedDevices(ISearchCriteria criteria) throws SiteWhereException {
		return HBaseDevice.listUnassignedDevices(client, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.lang.String,
	 * boolean)
	 */
	public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
		return HBaseDevice.deleteDevice(client, hardwareId, force, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceAssignment(com.sitewhere
	 * .spi.device.request.IDeviceAssignmentCreateRequest)
	 */
	public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request)
			throws SiteWhereException {
		return HBaseDeviceAssignment.createDeviceAssignment(client, request, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken(java.lang
	 * .String)
	 */
	public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
		return HBaseDeviceAssignment.getDeviceAssignment(client, token, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.lang.String,
	 * boolean)
	 */
	public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
		return HBaseDeviceAssignment.deleteDeviceAssignment(client, token, force, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceForAssignment(com.sitewhere
	 * .spi.device.IDeviceAssignment)
	 */
	public IDevice getDeviceForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return HBaseDevice.getDeviceByHardwareId(client, assignment.getDeviceHardwareId(), cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getSiteForAssignment(com.sitewhere.spi
	 * .device.IDeviceAssignment)
	 */
	public ISite getSiteForAssignment(IDeviceAssignment assignment) throws SiteWhereException {
		return HBaseSite.getSiteByToken(client, assignment.getSiteToken());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentMetadata(java.
	 * lang.String, com.sitewhere.spi.common.IMetadataProvider)
	 */
	public IDeviceAssignment updateDeviceAssignmentMetadata(String token, IMetadataProvider metadata)
			throws SiteWhereException {
		return HBaseDeviceAssignment.updateDeviceAssignmentMetadata(client, token, metadata, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus(java.lang
	 * .String, com.sitewhere.spi.device.DeviceAssignmentStatus)
	 */
	public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
			throws SiteWhereException {
		return HBaseDeviceAssignment.updateDeviceAssignmentStatus(client, token, status, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentState(java.lang
	 * .String, com.sitewhere.spi.device.IDeviceAssignmentState)
	 */
	public IDeviceAssignment updateDeviceAssignmentState(String token, IDeviceAssignmentState state)
			throws SiteWhereException {
		return HBaseDeviceAssignment.updateDeviceAssignmentState(client, token, state, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceEventBatch(java.lang.String,
	 * com.sitewhere.spi.device.IDeviceEventBatch)
	 */
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		return SiteWherePersistence.deviceEventBatchLogic(assignmentToken, batch, this, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.lang.String)
	 */
	public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
		return HBaseDeviceAssignment.endDeviceAssignment(client, token, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentHistory(java.lang
	 * .String, com.sitewhere.spi.common.ISearchCriteria)
	 */
	public SearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId,
			ISearchCriteria criteria) throws SiteWhereException {
		return HBaseDevice.getDeviceAssignmentHistory(client, hardwareId, criteria, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(java.lang
	 * .String, com.sitewhere.spi.common.ISearchCriteria)
	 */
	public SearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
			ISearchCriteria criteria) throws SiteWhereException {
		return HBaseSite.listDeviceAssignmentsForSite(client, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#getDeviceEventById(java.lang.String)
	 */
	@Override
	public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException {
		return HBaseDeviceEvent.getDeviceEvent(client, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceMeasurements(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest, boolean)
	 */
	@Override
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest measurements, boolean updateState) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceMeasurements(client, assignment, measurements, updateState,
				cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceMeasurements(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceMeasurements> listDeviceMeasurements(String token,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceMeasurements(client, token, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceMeasurementsForSite(java.lang
	 * .String, com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceMeasurementsForSite(client, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#addDeviceLocation(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest, boolean)
	 */
	@Override
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request,
			boolean updateState) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceLocation(client, assignment, request, updateState, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceLocations(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceLocations(client, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceLocationsForSite(java.lang
	 * .String, com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceLocationsForSite(client, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceLocations(java.util.List,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		throw new SiteWhereException("Not implemented yet for HBase device management.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#addDeviceAlert(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest, boolean)
	 */
	@Override
	public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request,
			boolean updateState) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceAlert(client, assignment, request, updateState, cacheProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceAlerts(java.lang.String,
	 * com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceAlerts(client, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceAlertsForSite(java.lang.String
	 * , com.sitewhere.spi.common.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceAlertsForSite(client, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceCommandInvocation(java.lang
	 * .String, com.sitewhere.spi.device.command.IDeviceCommand,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
	 */
	@Override
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
			IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceCommandInvocation(client, assignment, command, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandInvocations(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandInvocations(client, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandInvocationsForSite(
	 * java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandInvocationsForSite(client, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandInvocationResponses
	 * (java.lang.String)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(String invocationId)
			throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandInvocationResponses(client, invocationId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceCommandResponse(java.lang.String
	 * , com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
	 */
	@Override
	public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceCommandResponse(client, assignment, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandResponses(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandResponses(client, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommandResponsesForSite(java
	 * .lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandResponsesForSite(client, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceStateChange(java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
	 */
	@Override
	public IDeviceStateChange addDeviceStateChange(String assignmentToken,
			IDeviceStateChangeCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceStateChange(client, assignment, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceStateChanges(java.lang.String,
	 * com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceStateChanges(client, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceStateChangesForSite(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceStateChangesForSite(client, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createSite(com.sitewhere.spi.device.
	 * request.ISiteCreateRequest)
	 */
	@Override
	public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
		return HBaseSite.createSite(client, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteSite(java.lang.String,
	 * boolean)
	 */
	@Override
	public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
		return HBaseSite.deleteSite(client, siteToken, force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateSite(java.lang.String,
	 * com.sitewhere.spi.device.request.ISiteCreateRequest)
	 */
	@Override
	public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException {
		return HBaseSite.updateSite(client, siteToken, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getSiteByToken(java.lang.String)
	 */
	@Override
	public ISite getSiteByToken(String token) throws SiteWhereException {
		return HBaseSite.getSiteByToken(client, token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listSites(com.sitewhere.spi.common.
	 * ISearchCriteria)
	 */
	@Override
	public SearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
		return HBaseSite.listSites(client, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createZone(com.sitewhere.spi.device.
	 * ISite, com.sitewhere.spi.device.request.IZoneCreateRequest)
	 */
	@Override
	public IZone createZone(ISite site, IZoneCreateRequest request) throws SiteWhereException {
		return HBaseZone.createZone(client, site, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateZone(java.lang.String,
	 * com.sitewhere.spi.device.request.IZoneCreateRequest)
	 */
	@Override
	public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
		return HBaseZone.updateZone(client, token, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.lang.String)
	 */
	@Override
	public IZone getZone(String zoneToken) throws SiteWhereException {
		return HBaseZone.getZone(client, zoneToken);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listZones(java.lang.String,
	 * com.sitewhere.spi.common.ISearchCriteria)
	 */
	@Override
	public SearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria)
			throws SiteWhereException {
		return HBaseSite.listZonesForSite(client, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.lang.String,
	 * boolean)
	 */
	@Override
	public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
		return HBaseZone.deleteZone(client, zoneToken, force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#createDeviceGroup(com.sitewhere.spi.
	 * device.request.IDeviceGroupCreateRequest)
	 */
	@Override
	public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
		return HBaseDeviceGroup.createDeviceGroup(client, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceGroup(java.lang.String,
	 * com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
	 */
	@Override
	public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request)
			throws SiteWhereException {
		return HBaseDeviceGroup.updateDeviceGroup(client, token, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.lang.String)
	 */
	@Override
	public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
		return HBaseDeviceGroup.getDeviceGroupByToken(client, token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceGroups(boolean,
	 * com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
			throws SiteWhereException {
		return HBaseDeviceGroup.listDeviceGroups(client, includeDeleted, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupsWithRole(java.lang.String
	 * , boolean, com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
			ISearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceGroup.listDeviceGroupsWithRole(client, role, includeDeleted, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.lang.String,
	 * boolean)
	 */
	@Override
	public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
		return HBaseDeviceGroup.deleteDeviceGroup(client, token, force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceGroupElements(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public List<IDeviceGroupElement> addDeviceGroupElements(String networkToken,
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		return HBaseDeviceGroupElement.createDeviceGroupElements(client, networkToken, elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java.lang.
	 * String, java.util.List)
	 */
	@Override
	public List<IDeviceGroupElement> removeDeviceGroupElements(String networkToken,
			List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		return HBaseDeviceGroupElement.removeDeviceGroupElements(client, networkToken, elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupElements(java.lang.String
	 * , com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceGroupElement> listDeviceGroupElements(String networkToken,
			ISearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceGroupElement.listDeviceGroupElements(client, networkToken, criteria);
	}

	/**
	 * Verify that the given assignment exists.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceAssignment assertDeviceAssignment(String token) throws SiteWhereException {
		IDeviceAssignment result = HBaseDeviceAssignment.getDeviceAssignment(client, token, cacheProvider);
		if (result == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		return result;
	}

	public ISiteWhereHBaseClient getClient() {
		return client;
	}

	public void setClient(ISiteWhereHBaseClient client) {
		this.client = client;
	}
}