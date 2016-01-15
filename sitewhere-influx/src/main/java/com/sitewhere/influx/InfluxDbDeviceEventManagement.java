/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.influx;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.LogLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.influx.device.InfluxDbDeviceAlert;
import com.sitewhere.influx.device.InfluxDbDeviceEvent;
import com.sitewhere.influx.device.InfluxDbDeviceLocation;
import com.sitewhere.influx.device.InfluxDbDeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
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
 * Implementation of {@link IDeviceEventManagement} that stores events in InfluxDB.
 * 
 * @author Derek
 */
public class InfluxDbDeviceEventManagement extends TenantLifecycleComponent implements IDeviceEventManagement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(InfluxDbDeviceEventManagement.class);

	/** Device management implementation */
	private IDeviceManagement deviceManagement;

	/** InfluxDB handle */
	private InfluxDB influx;

	/** Connection URL */
	private String connectUrl = "http://localhost:8086";

	/** Username */
	private String username = "root";

	/** Password */
	private String password = "root";

	/** Database name */
	private String database = "sitewhere";

	/** Retention policy */
	private String retention = "default";

	/** Indicates if batch delivery is enabled */
	private boolean enableBatch = true;

	/** Max records in batch */
	private int batchChunkSize = 2000;

	/** Max time to wait for sending batch */
	private int batchIntervalMs = 100;

	/** Log level */
	private String logLevel;

	public InfluxDbDeviceEventManagement() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		this.influx = InfluxDBFactory.connect(getConnectUrl(), getUsername(), getPassword());
		influx.createDatabase(getDatabase());
		if (isEnableBatch()) {
			influx.enableBatch(getBatchChunkSize(), getBatchIntervalMs(), TimeUnit.MILLISECONDS);
		}
		influx.setLogLevel(convertLogLevel(getLogLevel()));
	}

	/**
	 * Convert log level setting to expected enum value.
	 * 
	 * @param level
	 * @return
	 */
	protected LogLevel convertLogLevel(String level) {
		if ((level == null) || (level.equalsIgnoreCase("none"))) {
			return LogLevel.NONE;
		} else if (level.equalsIgnoreCase("basic")) {
			return LogLevel.BASIC;
		} else if (level.equalsIgnoreCase("headers")) {
			return LogLevel.HEADERS;
		} else if (level.equalsIgnoreCase("full")) {
			return LogLevel.FULL;
		}
		return LogLevel.NONE;
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
		return null;
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
		List<IDeviceEvent> events = new ArrayList<IDeviceEvent>();
		return new SearchResults<IDeviceEvent>(events);
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
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assignmentToken);
		DeviceMeasurements mxs = SiteWherePersistence.deviceMeasurementsCreateLogic(measurements, assignment);
		mxs.setId(UUID.randomUUID().toString());
		Point.Builder builder = InfluxDbDeviceEvent.createBuilder();
		InfluxDbDeviceMeasurements.saveToBuilder(mxs, builder);
		influx.write(getDatabase(), getRetention(), builder.build());
		return mxs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceMeasurements(java
	 * .lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceMeasurements> listDeviceMeasurements(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return InfluxDbDeviceEvent.searchByAssignment(assignmentToken, DeviceEventType.Measurements,
				criteria, influx, getDatabase(), IDeviceMeasurements.class);
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
		return InfluxDbDeviceEvent.searchBySite(siteToken, DeviceEventType.Measurements, criteria, influx,
				getDatabase(), IDeviceMeasurements.class);
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
		IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assignmentToken);
		DeviceLocation location = SiteWherePersistence.deviceLocationCreateLogic(assignment, request);
		location.setId(UUID.randomUUID().toString());
		Point.Builder builder = InfluxDbDeviceEvent.createBuilder();
		InfluxDbDeviceLocation.saveToBuilder(location, builder);
		influx.write(getDatabase(), getRetention(), builder.build());
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocations(java.
	 * lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return InfluxDbDeviceEvent.searchByAssignment(assignmentToken, DeviceEventType.Location, criteria,
				influx, getDatabase(), IDeviceLocation.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocationsForSite
	 * (java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return InfluxDbDeviceEvent.searchBySite(siteToken, DeviceEventType.Location, criteria, influx,
				getDatabase(), IDeviceLocation.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocations(java.
	 * util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		List<IDeviceLocation> locations = new ArrayList<IDeviceLocation>();
		return new SearchResults<IDeviceLocation>(locations);
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
		Point.Builder builder = InfluxDbDeviceEvent.createBuilder();
		InfluxDbDeviceAlert.saveToBuilder(alert, builder);
		influx.write(getDatabase(), getRetention(), builder.build());
		return alert;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceAlerts(java.lang
	 * .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return InfluxDbDeviceEvent.searchByAssignment(assignmentToken, DeviceEventType.Alert, criteria,
				influx, getDatabase(), IDeviceAlert.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceAlertsForSite(java
	 * .lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		return InfluxDbDeviceEvent.searchBySite(siteToken, DeviceEventType.Alert, criteria, influx,
				getDatabase(), IDeviceAlert.class);
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
		throw new SiteWhereException("Streaming data not supported by InfluxDB.");
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
		throw new SiteWhereException("Streaming data not supported by InfluxDB.");
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
		List<IDeviceStreamData> results = new ArrayList<IDeviceStreamData>();
		return new SearchResults<IDeviceStreamData>(results);
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
	public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken,
			IDeviceCommand command, IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
		return null;
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
		List<IDeviceCommandInvocation> results = new ArrayList<IDeviceCommandInvocation>();
		return new SearchResults<IDeviceCommandInvocation>(results);
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
		List<IDeviceCommandInvocation> results = new ArrayList<IDeviceCommandInvocation>();
		return new SearchResults<IDeviceCommandInvocation>(results);
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
		List<IDeviceCommandResponse> results = new ArrayList<IDeviceCommandResponse>();
		return new SearchResults<IDeviceCommandResponse>(results);
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
		return null;
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
		List<IDeviceCommandResponse> results = new ArrayList<IDeviceCommandResponse>();
		return new SearchResults<IDeviceCommandResponse>(results);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceCommandResponsesForSite
	 * (java.lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
	 */
	@Override
	public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForSite(String siteToken,
			IDateRangeSearchCriteria criteria) throws SiteWhereException {
		List<IDeviceCommandResponse> results = new ArrayList<IDeviceCommandResponse>();
		return new SearchResults<IDeviceCommandResponse>(results);
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
		return null;
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
		List<IDeviceStateChange> results = new ArrayList<IDeviceStateChange>();
		return new SearchResults<IDeviceStateChange>(results);
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
		List<IDeviceStateChange> results = new ArrayList<IDeviceStateChange>();
		return new SearchResults<IDeviceStateChange>(results);
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
	 * @see
	 * com.sitewhere.spi.device.event.IDeviceEventManagement#setDeviceManagement(com.sitewhere
	 * .spi.device.IDeviceManagement)
	 */
	public void setDeviceManagement(IDeviceManagement deviceManagement) {
		this.deviceManagement = deviceManagement;
	}

	public String getConnectUrl() {
		return connectUrl;
	}

	public void setConnectUrl(String connectUrl) {
		this.connectUrl = connectUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getRetention() {
		return retention;
	}

	public void setRetention(String retention) {
		this.retention = retention;
	}

	public boolean isEnableBatch() {
		return enableBatch;
	}

	public void setEnableBatch(boolean enableBatch) {
		this.enableBatch = enableBatch;
	}

	public int getBatchChunkSize() {
		return batchChunkSize;
	}

	public void setBatchChunkSize(int batchChunkSize) {
		this.batchChunkSize = batchChunkSize;
	}

	public int getBatchIntervalMs() {
		return batchIntervalMs;
	}

	public void setBatchIntervalMs(int batchIntervalMs) {
		this.batchIntervalMs = batchIntervalMs;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
}