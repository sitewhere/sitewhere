/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hazelcast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.hazelcast.timeseries.EventBucket.Scope;
import com.sitewhere.hazelcast.timeseries.TimeSeriesManager;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.DeviceEventType;
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
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IDeviceEventManagement} that stores data in a Hazelcast
 * in-memory data grid. This implementation works on ephemeral data and does not offer
 * long-term peristence.
 * 
 * @author Derek
 */
public class HazelcastDeviceEventManagement extends TenantLifecycleComponent
		implements IDeviceEventManagement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HazelcastDeviceEventManagement.class);

	/** Default expiration period in minutes */
	private static final long DEFAULT_EXPIRATION_IN_MIN = 120;

	/** Common Hazelcast configuration */
	private SiteWhereHazelcastConfiguration configuration;

	/** Device management implementation */
	private IDeviceManagement deviceManagement;

	/** Manages time series data in Hazelcast */
	private TimeSeriesManager timeSeriesManager;

	/** Expiration period in seconds */
	private long expirationInMin = DEFAULT_EXPIRATION_IN_MIN;

	public HazelcastDeviceEventManagement() {
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

		LOGGER.info("Cache entries will expire in " + getExpirationInMin() + " minutes.");
		this.timeSeriesManager =
				new TimeSeriesManager(getTenant(), getConfiguration().getHazelcastInstance(),
						getExpirationInMin() * 60);
		timeSeriesManager.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (timeSeriesManager != null) {
			timeSeriesManager.stop();
		}
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
	 * @see com.sitewhere.spi.device.event.IDeviceEventManagement#setDeviceManagement(com.
	 * sitewhere.spi.device.IDeviceManagement)
	 */
	@Override
	public void setDeviceManagement(IDeviceManagement deviceManagement) throws SiteWhereException {
		this.deviceManagement = deviceManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceManagement()
	 */
	@Override
	public IDeviceManagement getDeviceManagement() throws SiteWhereException {
		return deviceManagement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceEventBatch(java.lang
	 * .String, com.sitewhere.spi.device.event.IDeviceEventBatch)
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
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceEventById(java.lang.
	 * String)
	 */
	@Override
	public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException {
		return getTimeSeriesManager().getEventById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceEvents(java.lang.
	 * String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceEvent> listDeviceEvents(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Assignment, assignmentToken, null, criteria,
				IDeviceEvent.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceMeasurements(java.
	 * lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public IDeviceMeasurements addDeviceMeasurements(String assignmentToken,
			IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assignmentToken);
		DeviceMeasurements mx = SiteWherePersistence.deviceMeasurementsCreateLogic(measurements, assignment);
		mx.setId(UUID.randomUUID().toString());
		getTimeSeriesManager().addEvent(mx);
		return mx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceMeasurements(java.
	 * lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Assignment, assignmentToken, DeviceEventType.Measurements,
				criteria, IDeviceMeasurements.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceMeasurementsForSite
	 * (java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Site, siteToken, DeviceEventType.Measurements, criteria,
				IDeviceMeasurements.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceLocation(java.lang.
	 * String, com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
			throws SiteWhereException {
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assignmentToken);
		DeviceLocation loc = SiteWherePersistence.deviceLocationCreateLogic(assignment, request);
		loc.setId(UUID.randomUUID().toString());
		getTimeSeriesManager().addEvent(loc);
		return loc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocations(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Assignment, assignmentToken, DeviceEventType.Location,
				criteria, IDeviceLocation.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocationsForSite(
	 * java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Site, siteToken, DeviceEventType.Location, criteria,
				IDeviceLocation.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocations(java.util
	 * .List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return null;
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
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assignmentToken);
		DeviceAlert alert = SiteWherePersistence.deviceAlertCreateLogic(assignment, request);
		alert.setId(UUID.randomUUID().toString());
		getTimeSeriesManager().addEvent(alert);
		return alert;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceAlerts(java.lang.
	 * String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Assignment, assignmentToken, DeviceEventType.Alert,
				criteria, IDeviceAlert.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceAlertsForSite(java.
	 * lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Site, siteToken, DeviceEventType.Alert, criteria,
				IDeviceAlert.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStreamData(java.lang
	 * .String, com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
	 */
	@Override
	public IDeviceStreamData addDeviceStreamData(String assignmentToken,
			IDeviceStreamDataCreateRequest request) throws SiteWhereException {
		throw new SiteWhereException("Streaming data not supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceStreamData(java.lang
	 * .String, java.lang.String, long)
	 */
	@Override
	public IDeviceStreamData getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
			throws SiteWhereException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceStreamData(java.
	 * lang.String, java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStreamData> listDeviceStreamData(String assignmentToken, String streamId,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return new SearchResults<>(new ArrayList<IDeviceStreamData>(), 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceCommandInvocation(
	 * java.lang.String, com.sitewhere.spi.device.command.IDeviceCommand,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
	 */
	@Override
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken, IDeviceCommand command,
			IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assignmentToken);
		DeviceCommandInvocation ci =
				SiteWherePersistence.deviceCommandInvocationCreateLogic(assignment, command, request);
		ci.setId(UUID.randomUUID().toString());
		getTimeSeriesManager().addEvent(ci);
		return ci;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceCommandInvocations(
	 * java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Assignment, assignmentToken,
				DeviceEventType.CommandInvocation, criteria, IDeviceCommandInvocation.class);
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
		return getTimeSeriesManager().search(Scope.Site, siteToken, DeviceEventType.CommandInvocation,
				criteria, IDeviceCommandInvocation.class);
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
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceCommandResponse(java
	 * .lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
	 */
	@Override
	public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
			IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assignmentToken);
		DeviceCommandResponse cr = SiteWherePersistence.deviceCommandResponseCreateLogic(assignment, request);
		cr.setId(UUID.randomUUID().toString());
		getTimeSeriesManager().addEvent(cr);
		return cr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceCommandResponses(
	 * java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Assignment, assignmentToken,
				DeviceEventType.CommandResponse, criteria, IDeviceCommandResponse.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
	 * listDeviceCommandResponsesForSite(java.lang.String,
	 * com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Site, siteToken, DeviceEventType.CommandResponse, criteria,
				IDeviceCommandResponse.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStateChange(java.
	 * lang.String,
	 * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
	 */
	@Override
	public IDeviceStateChange addDeviceStateChange(String assignmentToken,
			IDeviceStateChangeCreateRequest request) throws SiteWhereException {
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assignmentToken);
		DeviceStateChange state = SiteWherePersistence.deviceStateChangeCreateLogic(assignment, request);
		state.setId(UUID.randomUUID().toString());
		getTimeSeriesManager().addEvent(state);
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceStateChanges(java.
	 * lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return getTimeSeriesManager().search(Scope.Assignment, assignmentToken, DeviceEventType.StateChange,
				criteria, IDeviceStateChange.class);
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
		return getTimeSeriesManager().search(Scope.Site, siteToken, DeviceEventType.StateChange, criteria,
				IDeviceStateChange.class);
	}

	public SiteWhereHazelcastConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SiteWhereHazelcastConfiguration configuration) {
		this.configuration = configuration;
	}

	public long getExpirationInMin() {
		return expirationInMin;
	}

	public void setExpirationInMin(long expirationInMin) {
		this.expirationInMin = expirationInMin;
	}

	public TimeSeriesManager getTimeSeriesManager() {
		return timeSeriesManager;
	}

	public void setTimeSeriesManager(TimeSeriesManager timeSeriesManager) {
		this.timeSeriesManager = timeSeriesManager;
	}
}