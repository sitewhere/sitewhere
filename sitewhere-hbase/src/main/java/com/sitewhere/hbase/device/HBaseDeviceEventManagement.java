/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

import java.util.List;

import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.log4j.Logger;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.device.AssignmentStateManager;
import com.sitewhere.hbase.HBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.hbase.encoder.ProtobufPayloadMarshaler;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
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
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * HBase implementation of SiteWhere device event management.
 * 
 * @author Derek
 */
public class HBaseDeviceEventManagement extends TenantLifecycleComponent implements IDeviceEventManagement {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(HBaseDeviceEventManagement.class);

	/** Device management implementation */
	private IDeviceManagement deviceManagement;

	/** Used to communicate with HBase */
	private ISiteWhereHBaseClient client;

	/** Injected payload encoder */
	private IPayloadMarshaler payloadMarshaler = new ProtobufPayloadMarshaler();

	/** Supplies context to implementation methods */
	private HBaseContext context;

	/** Allows puts to be buffered for device events */
	private DeviceEventBuffer buffer;

	/** Assignment state manager */
	private AssignmentStateManager assignmentStateManager;

	/** Device id manager */
	private DeviceIdManager deviceIdManager;

	public HBaseDeviceEventManagement() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		// Create context from configured options.
		this.context = new HBaseContext();
		context.setTenant(getTenant());
		context.setClient(getClient());
		context.setPayloadMarshaler(getPayloadMarshaler());

		ensureTablesExist();

		// Create device id manager instance.
		deviceIdManager = new DeviceIdManager();
		deviceIdManager.load(context);
		context.setDeviceIdManager(deviceIdManager);

		// Start buffer for saving device events.
		buffer = new DeviceEventBuffer(context);
		buffer.start();
		context.setDeviceEventBuffer(buffer);

		// Create assignment state manager and start it.
		assignmentStateManager = new AssignmentStateManager(getDeviceManagement());
		startNestedComponent(assignmentStateManager, true);
		context.setAssignmentStateManager(assignmentStateManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (buffer != null) {
			buffer.stop();
		}

		// Stop the assignment state manager.
		if (assignmentStateManager != null) {
			assignmentStateManager.stop();
		}
	}

	/**
	 * Make sure that all SiteWhere tables exist, creating them if necessary.
	 * 
	 * @throws SiteWhereException
	 */
	protected void ensureTablesExist() throws SiteWhereException {
		SiteWhereTables.assureTenantTable(context, ISiteWhereHBase.UID_TABLE_NAME, BloomType.ROW);
		SiteWhereTables.assureTenantTable(context, ISiteWhereHBase.EVENTS_TABLE_NAME, BloomType.ROW);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceEventBatch(java.
	 * lang.String, com.sitewhere.spi.device.event.IDeviceEventBatch)
	 */
	@Override
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		return SiteWherePersistence.deviceEventBatchLogic(assignmentToken, batch, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceEventById(java.lang
	 * .String)
	 */
	@Override
	public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException {
		return HBaseDeviceEvent.getDeviceEvent(context, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceEvents(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceEvent> listDeviceEvents(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceEvents(context, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceMeasurements(java
	 * .lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceMeasurements(context, assignment, measurements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceMeasurements(java
	 * .lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceMeasurements> listDeviceMeasurements(String token,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceMeasurements(context, token, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceMeasurementsForSite
	 * (java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceMeasurementsForSite(context, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceLocation(java.lang
	 * .String, com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
			throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceLocation(context, assignment, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocations(java.
	 * lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceLocations(context, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocationsForSite
	 * (java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceLocationsForSite(context, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocations(java.
	 * util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		throw new SiteWhereException("Not implemented yet for HBase device management.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceAlert(java.lang.
	 * String, com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceAlert(context, assignment, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceAlerts(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceAlerts(context, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceAlertsForSite(java
	 * .lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public SearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceAlertsForSite(context, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStreamData(java.
	 * lang.String, com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
	 */
	@Override
	public IDeviceStreamData addDeviceStreamData(String assignmentToken,
			IDeviceStreamDataCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceStreamData.createDeviceStreamData(context, assignment, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceStreamData(java.
	 * lang.String, java.lang.String, long)
	 */
	@Override
	public IDeviceStreamData getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
			throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceStreamData.getDeviceStreamData(context, assignment, streamId, sequenceNumber);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceStreamData(java
	 * .lang.String, java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStreamData> listDeviceStreamData(String assignmentToken, String streamId,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.listDeviceStreamData(context, assignment, streamId, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceCommandInvocation
	 * (java.lang.String, com.sitewhere.spi.device.command.IDeviceCommand,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
	 */
	@Override
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken, IDeviceCommand command,
			IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceCommandInvocation(context, assignment, command, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceCommandInvocations
	 * (java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandInvocations(context, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
	 * listDeviceCommandInvocationsForSite(java.lang.String,
	 * com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandInvocationsForSite(context, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
	 * listDeviceCommandInvocationResponses(java.lang.String)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(String invocationId)
			throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandInvocationResponses(context, invocationId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceCommandResponse(
	 * java.lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
	 */
	@Override
	public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceCommandResponse(context, assignment, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceCommandResponses
	 * (java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandResponses(context, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
	 * listDeviceCommandResponsesForSite (java.lang.String,
	 * com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceCommandResponsesForSite(context, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStateChange(java
	 * .lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
	 */
	@Override
	public IDeviceStateChange addDeviceStateChange(String assignmentToken,
			IDeviceStateChangeCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = assertDeviceAssignment(assignmentToken);
		return HBaseDeviceEvent.createDeviceStateChange(context, assignment, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceStateChanges(java
	 * .lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceStateChanges(context, assignmentToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceStateChangesForSite
	 * (java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return HBaseDeviceEvent.listDeviceStateChangesForSite(context, siteToken, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceManagement()
	 */
	public IDeviceManagement getDeviceManagement() {
		return deviceManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEventManagement#setDeviceManagement(com.
	 * sitewhere .spi.device.IDeviceManagement)
	 */
	public void setDeviceManagement(IDeviceManagement deviceManagement) {
		this.deviceManagement = deviceManagement;
	}

	/**
	 * Verify that the given assignment exists.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceAssignment assertDeviceAssignment(String token) throws SiteWhereException {
		IDeviceAssignment result = getDeviceManagement().getDeviceAssignmentByToken(token);
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

	public IPayloadMarshaler getPayloadMarshaler() {
		return payloadMarshaler;
	}

	public void setPayloadMarshaler(IPayloadMarshaler payloadMarshaler) {
		this.payloadMarshaler = payloadMarshaler;
	}
}