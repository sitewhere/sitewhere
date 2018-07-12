/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.cassandra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.sitewhere.cassandra.CassandraClient;
import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IDeviceEventManagement} that stores events in Apache
 * Cassandra.
 * 
 * @author Derek
 */
public class CassandraDeviceEventManagement extends TenantEngineLifecycleComponent implements IDeviceEventManagement {

    /** Configured Cassandra client */
    private CassandraClient client;

    public CassandraDeviceEventManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getClient() == null) {
	    throw new SiteWhereException("No Cassandra client configured.");
	}
	getClient().start(monitor);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceEventBatch(
     * java.util.UUID, com.sitewhere.spi.device.event.IDeviceEventBatch)
     */
    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	return DeviceEventManagementPersistence.deviceEventBatchLogic(assignment, batch, this);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceEventById(java
     * .util.UUID)
     */
    @Override
    public IDeviceEvent getDeviceEventById(UUID eventId) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceMeasurements(
     * java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest[])
     */
    @Override
    public List<IDeviceMeasurement> addDeviceMeasurements(UUID deviceAssignmentId,
	    IDeviceMeasurementCreateRequest... requests) throws SiteWhereException {
	List<IDeviceMeasurement> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	for (IDeviceMeasurementCreateRequest request : requests) {
	    DeviceMeasurement mxs = DeviceEventManagementPersistence.deviceMeasurementCreateLogic(request, assignment);

	    // Build insert for event by id.
	    BoundStatement eventById = getClient().getInsertDeviceEventById().bind();
	    CassandraDeviceMeasurements.bindFields(getClient(), eventById, mxs);
	    process(eventById, mxs);

	    // Build insert for event by assignment.
	    BoundStatement eventByAssn = getClient().getInsertDeviceEventByAssignment().bind();
	    CassandraDeviceMeasurements.bindFields(getClient(), eventByAssn, mxs);
	    eventByAssn.setInt("bucket", getClient().getBucketValue(mxs.getEventDate().getTime()));
	    process(eventByAssn, mxs);

	    // Build insert for event by area.
	    if (assignment.getAreaId() != null) {
		BoundStatement eventByArea = getClient().getInsertDeviceEventByArea().bind();
		CassandraDeviceMeasurements.bindFields(getClient(), eventByArea, mxs);
		eventByArea.setInt("bucket", getClient().getBucketValue(mxs.getEventDate().getTime()));
		process(eventByArea, mxs);
	    }

	    // Build insert for event by asset.
	    if (assignment.getAssetId() != null) {
		BoundStatement eventByAsset = getClient().getInsertDeviceEventByAsset().bind();
		CassandraDeviceMeasurements.bindFields(getClient(), eventByAsset, mxs);
		eventByAsset.setInt("bucket", getClient().getBucketValue(mxs.getEventDate().getTime()));
		process(eventByAsset, mxs);
	    }
	    result.add(mxs);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	PreparedStatement query = getQueryForIndex(index);
	String queryField = getQueryFieldForIndex(index);
	Pager<IDeviceMeasurement> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(query, queryField, entityIds, criteria,
		    DeviceEventType.Measurement, bucket);
	    List<IDeviceMeasurement> bucketEvents = new ArrayList<>();
	    for (ResultSet perKey : perBucket) {
		for (Row row : perKey) {
		    DeviceMeasurement mx = new DeviceMeasurement();
		    CassandraDeviceMeasurements.loadFields(getClient(), mx, row);
		    bucketEvents.add(mx);
		}
	    }
	    addSortedEventsToPager(pager, bucketEvents, bucket);
	}
	return new SearchResults<IDeviceMeasurement>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceLocations(java
     * .util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest[])
     */
    @Override
    public List<IDeviceLocation> addDeviceLocations(UUID deviceAssignmentId, IDeviceLocationCreateRequest... requests)
	    throws SiteWhereException {
	List<IDeviceLocation> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	for (IDeviceLocationCreateRequest request : requests) {
	    DeviceLocation location = DeviceEventManagementPersistence.deviceLocationCreateLogic(assignment, request);

	    // Build insert for event by id.
	    BoundStatement eventById = getClient().getInsertDeviceEventById().bind();
	    CassandraDeviceLocation.bindFields(getClient(), eventById, location);
	    process(eventById, location);

	    // Build insert for event by assignment.
	    BoundStatement eventByAssn = getClient().getInsertDeviceEventByAssignment().bind();
	    CassandraDeviceLocation.bindFields(getClient(), eventByAssn, location);
	    eventByAssn.setInt("bucket", getClient().getBucketValue(location.getEventDate().getTime()));
	    process(eventByAssn, location);

	    // Build insert for event by area.
	    if (assignment.getAreaId() != null) {
		BoundStatement eventByArea = getClient().getInsertDeviceEventByArea().bind();
		CassandraDeviceLocation.bindFields(getClient(), eventByArea, location);
		eventByArea.setInt("bucket", getClient().getBucketValue(location.getEventDate().getTime()));
		process(eventByArea, location);
	    }

	    // Build insert for event by asset.
	    if (assignment.getAssetId() != null) {
		BoundStatement eventByAsset = getClient().getInsertDeviceEventByAsset().bind();
		CassandraDeviceLocation.bindFields(getClient(), eventByAsset, location);
		eventByAsset.setInt("bucket", getClient().getBucketValue(location.getEventDate().getTime()));
		process(eventByAsset, location);
	    }
	    result.add(location);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	PreparedStatement query = getQueryForIndex(index);
	String queryField = getQueryFieldForIndex(index);
	Pager<IDeviceLocation> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(query, queryField, entityIds, criteria,
		    DeviceEventType.Location, bucket);
	    List<IDeviceLocation> bucketEvents = new ArrayList<>();
	    for (ResultSet perKey : perBucket) {
		for (Row row : perKey) {
		    DeviceLocation location = new DeviceLocation();
		    CassandraDeviceLocation.loadFields(getClient(), location, row);
		    bucketEvents.add(location);
		}
	    }
	    addSortedEventsToPager(pager, bucketEvents, bucket);
	}
	return new SearchResults<IDeviceLocation>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceAlerts(java.
     * util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest[])
     */
    @Override
    public List<IDeviceAlert> addDeviceAlerts(UUID deviceAssignmentId, IDeviceAlertCreateRequest... requests)
	    throws SiteWhereException {
	List<IDeviceAlert> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	for (IDeviceAlertCreateRequest request : requests) {
	    DeviceAlert alert = DeviceEventManagementPersistence.deviceAlertCreateLogic(assignment, request);

	    // Build insert for event by id.
	    BoundStatement eventById = getClient().getInsertDeviceEventById().bind();
	    CassandraDeviceAlert.bindFields(getClient(), eventById, alert);
	    process(eventById, alert);

	    // Build insert for event by assignment.
	    BoundStatement eventByAssn = getClient().getInsertDeviceEventByAssignment().bind();
	    CassandraDeviceAlert.bindFields(getClient(), eventByAssn, alert);
	    eventByAssn.setInt("bucket", getClient().getBucketValue(alert.getEventDate().getTime()));
	    process(eventByAssn, alert);

	    // Build insert for event by area.
	    if (assignment.getAreaId() != null) {
		BoundStatement eventByArea = getClient().getInsertDeviceEventByArea().bind();
		CassandraDeviceAlert.bindFields(getClient(), eventByArea, alert);
		eventByArea.setInt("bucket", getClient().getBucketValue(alert.getEventDate().getTime()));
		process(eventByArea, alert);
	    }

	    // Build insert for event by asset.
	    if (assignment.getAssetId() != null) {
		BoundStatement eventByAsset = getClient().getInsertDeviceEventByAsset().bind();
		CassandraDeviceAlert.bindFields(getClient(), eventByAsset, alert);
		eventByAsset.setInt("bucket", getClient().getBucketValue(alert.getEventDate().getTime()));
		process(eventByAsset, alert);
	    }
	    result.add(alert);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	PreparedStatement query = getQueryForIndex(index);
	String queryField = getQueryFieldForIndex(index);
	Pager<IDeviceAlert> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(query, queryField, entityIds, criteria,
		    DeviceEventType.Alert, bucket);
	    List<IDeviceAlert> bucketEvents = new ArrayList<>();
	    for (ResultSet perKey : perBucket) {
		for (Row row : perKey) {
		    DeviceAlert alert = new DeviceAlert();
		    CassandraDeviceAlert.loadFields(getClient(), alert, row);
		    bucketEvents.add(alert);
		}
	    }
	    addSortedEventsToPager(pager, bucketEvents, bucket);
	}
	return new SearchResults<IDeviceAlert>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandInvocations(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest[
     * ])
     */
    @Override
    public List<IDeviceCommandInvocation> addDeviceCommandInvocations(UUID deviceAssignmentId,
	    IDeviceCommandInvocationCreateRequest... requests) throws SiteWhereException {
	List<IDeviceCommandInvocation> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	for (IDeviceCommandInvocationCreateRequest request : requests) {
	    DeviceCommandInvocation invocation = DeviceEventManagementPersistence
		    .deviceCommandInvocationCreateLogic(assignment, request);

	    // Build insert for event by id.
	    BoundStatement eventById = getClient().getInsertDeviceEventById().bind();
	    CassandraDeviceCommandInvocation.bindFields(getClient(), eventById, invocation);
	    process(eventById, invocation);

	    // Build insert for event by assignment.
	    BoundStatement eventByAssn = getClient().getInsertDeviceEventByAssignment().bind();
	    CassandraDeviceCommandInvocation.bindFields(getClient(), eventByAssn, invocation);
	    eventByAssn.setInt("bucket", getClient().getBucketValue(invocation.getEventDate().getTime()));
	    process(eventByAssn, invocation);

	    // Build insert for event by area.
	    if (assignment.getAreaId() != null) {
		BoundStatement eventByArea = getClient().getInsertDeviceEventByArea().bind();
		CassandraDeviceCommandInvocation.bindFields(getClient(), eventByArea, invocation);
		eventByArea.setInt("bucket", getClient().getBucketValue(invocation.getEventDate().getTime()));
		process(eventByArea, invocation);
	    }

	    // Build insert for event by asset.
	    if (assignment.getAssetId() != null) {
		BoundStatement eventByAsset = getClient().getInsertDeviceEventByAsset().bind();
		CassandraDeviceCommandInvocation.bindFields(getClient(), eventByAsset, invocation);
		eventByAsset.setInt("bucket", getClient().getBucketValue(invocation.getEventDate().getTime()));
		process(eventByAsset, invocation);
	    }
	    result.add(invocation);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	PreparedStatement query = getQueryForIndex(index);
	String queryField = getQueryFieldForIndex(index);
	Pager<IDeviceCommandInvocation> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(query, queryField, entityIds, criteria,
		    DeviceEventType.CommandInvocation, bucket);
	    List<IDeviceCommandInvocation> bucketEvents = new ArrayList<>();
	    for (ResultSet perKey : perBucket) {
		for (Row row : perKey) {
		    DeviceCommandInvocation invocation = new DeviceCommandInvocation();
		    CassandraDeviceCommandInvocation.loadFields(getClient(), invocation, row);
		    bucketEvents.add(invocation);
		}
	    }
	    addSortedEventsToPager(pager, bucketEvents, bucket);
	}
	return new SearchResults<IDeviceCommandInvocation>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationResponses(java.util.UUID)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID invocationId)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandResponses(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest[])
     */
    @Override
    public List<IDeviceCommandResponse> addDeviceCommandResponses(UUID deviceAssignmentId,
	    IDeviceCommandResponseCreateRequest... requests) throws SiteWhereException {
	List<IDeviceCommandResponse> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	for (IDeviceCommandResponseCreateRequest request : requests) {
	    DeviceCommandResponse response = DeviceEventManagementPersistence
		    .deviceCommandResponseCreateLogic(assignment, request);

	    // Build insert for event by id.
	    BoundStatement eventById = getClient().getInsertDeviceEventById().bind();
	    CassandraDeviceCommandResponse.bindFields(getClient(), eventById, response);
	    process(eventById, response);

	    // Build insert for event by assignment.
	    BoundStatement eventByAssn = getClient().getInsertDeviceEventByAssignment().bind();
	    CassandraDeviceCommandResponse.bindFields(getClient(), eventByAssn, response);
	    eventByAssn.setInt("bucket", getClient().getBucketValue(response.getEventDate().getTime()));
	    process(eventByAssn, response);

	    // Build insert for event by area.
	    if (assignment.getAreaId() != null) {
		BoundStatement eventByArea = getClient().getInsertDeviceEventByArea().bind();
		CassandraDeviceCommandResponse.bindFields(getClient(), eventByArea, response);
		eventByArea.setInt("bucket", getClient().getBucketValue(response.getEventDate().getTime()));
		process(eventByArea, response);
	    }

	    // Build insert for event by asset.
	    if (assignment.getAssetId() != null) {
		BoundStatement eventByAsset = getClient().getInsertDeviceEventByAsset().bind();
		CassandraDeviceCommandResponse.bindFields(getClient(), eventByAsset, response);
		eventByAsset.setInt("bucket", getClient().getBucketValue(response.getEventDate().getTime()));
		process(eventByAsset, response);
	    }
	    result.add(response);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	PreparedStatement query = getQueryForIndex(index);
	String queryField = getQueryFieldForIndex(index);
	Pager<IDeviceCommandResponse> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(query, queryField, entityIds, criteria,
		    DeviceEventType.CommandResponse, bucket);
	    List<IDeviceCommandResponse> bucketEvents = new ArrayList<>();
	    for (ResultSet perKey : perBucket) {
		for (Row row : perKey) {
		    DeviceCommandResponse response = new DeviceCommandResponse();
		    CassandraDeviceCommandResponse.loadFields(getClient(), response, row);
		    bucketEvents.add(response);
		}
	    }
	    addSortedEventsToPager(pager, bucketEvents, bucket);
	}
	return new SearchResults<IDeviceCommandResponse>(pager.getResults(), pager.getTotal());
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStateChanges(
     * java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest[])
     */
    @Override
    public List<IDeviceStateChange> addDeviceStateChanges(UUID deviceAssignmentId,
	    IDeviceStateChangeCreateRequest... requests) throws SiteWhereException {
	List<IDeviceStateChange> result = new ArrayList<>();
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	for (IDeviceStateChangeCreateRequest request : requests) {
	    DeviceStateChange state = DeviceEventManagementPersistence.deviceStateChangeCreateLogic(assignment,
		    request);

	    // Build insert for event by id.
	    BoundStatement eventById = getClient().getInsertDeviceEventById().bind();
	    CassandraDeviceStateChange.bindFields(getClient(), eventById, state);
	    process(eventById, state);

	    // Build insert for event by assignment.
	    BoundStatement eventByAssn = getClient().getInsertDeviceEventByAssignment().bind();
	    CassandraDeviceStateChange.bindFields(getClient(), eventByAssn, state);
	    eventByAssn.setInt("bucket", getClient().getBucketValue(state.getEventDate().getTime()));
	    process(eventByAssn, state);

	    // Build insert for event by area.
	    if (assignment.getAreaId() != null) {
		BoundStatement eventByArea = getClient().getInsertDeviceEventByArea().bind();
		CassandraDeviceStateChange.bindFields(getClient(), eventByArea, state);
		eventByArea.setInt("bucket", getClient().getBucketValue(state.getEventDate().getTime()));
		process(eventByArea, state);
	    }

	    // Build insert for event by asset.
	    if (assignment.getAssetId() != null) {
		BoundStatement eventByAsset = getClient().getInsertDeviceEventByAsset().bind();
		CassandraDeviceStateChange.bindFields(getClient(), eventByAsset, state);
		eventByAsset.setInt("bucket", getClient().getBucketValue(state.getEventDate().getTime()));
		process(eventByAsset, state);
	    }
	    result.add(state);
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	PreparedStatement query = getQueryForIndex(index);
	String queryField = getQueryFieldForIndex(index);
	Pager<IDeviceStateChange> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(query, queryField, entityIds, criteria,
		    DeviceEventType.StateChange, bucket);
	    List<IDeviceStateChange> bucketEvents = new ArrayList<>();
	    for (ResultSet perKey : perBucket) {
		for (Row row : perKey) {
		    DeviceStateChange response = new DeviceStateChange();
		    CassandraDeviceStateChange.loadFields(getClient(), response, row);
		    bucketEvents.add(response);
		}
	    }
	    addSortedEventsToPager(pager, bucketEvents, bucket);
	}
	return new SearchResults<IDeviceStateChange>(pager.getResults(), pager.getTotal());
    }

    /**
     * Process a Cassandra query and handle failures.
     * 
     * @param statement
     * @param event
     * @throws SiteWhereException
     */
    protected void process(BoundStatement statement, IDeviceEvent event) throws SiteWhereException {
	ResultSetFuture future = getClient().getSession().executeAsync(statement);
	Futures.addCallback(future, new FutureCallback<ResultSet>() {
	    /*
	     * @see
	     * com.google.common.util.concurrent.FutureCallback#onSuccess(java.lang.Object)
	     */
	    @Override
	    public void onSuccess(ResultSet result) {
	    }

	    /*
	     * @see com.google.common.util.concurrent.FutureCallback#onFailure(java.lang.
	     * Throwable)
	     */
	    @Override
	    public void onFailure(Throwable t) {
		getLogger().error("Failed to persist Cassandra event: " + event.getId(), t);
	    }
	}, MoreExecutors.directExecutor());
    }

    /**
     * Get query that corresponds to the given event index.
     * 
     * @param index
     * @return
     * @throws SiteWhereException
     */
    protected PreparedStatement getQueryForIndex(DeviceEventIndex index) throws SiteWhereException {
	switch (index) {
	case Area: {
	    return getClient().getSelectEventsByAreaForType();
	}
	case Asset: {
	    throw new SiteWhereException("Indexing by asset not implemented.");
	}
	case Assignment: {
	    return getClient().getSelectEventsByAssignmentForType();
	}
	case Customer: {
	    throw new SiteWhereException("Indexing by customer not implemented.");
	}
	}
	throw new SiteWhereException("Index type not implemented: " + index.name());
    }

    /**
     * Get query field that corresponds to index.
     * 
     * @param index
     * @return
     * @throws SiteWhereException
     */
    protected String getQueryFieldForIndex(DeviceEventIndex index) throws SiteWhereException {
	switch (index) {
	case Area: {
	    return "areaId";
	}
	case Asset: {
	    throw new SiteWhereException("Indexing by asset not implemented.");
	}
	case Assignment: {
	    return "assignmentId";
	}
	case Customer: {
	    throw new SiteWhereException("Indexing by customer not implemented.");
	}
	}
	throw new SiteWhereException("Index type not implemented: " + index.name());
    }

    /**
     * Find the list of buckets required to cover a given date range.
     * 
     * @param client
     * @param criteria
     * @return
     */
    protected List<Integer> getBucketsForDateRange(IDateRangeSearchCriteria criteria) {
	long bucket = getClient().getBucketLengthInMs();
	long current = criteria.getEndDate() != null ? criteria.getEndDate().getTime() : System.currentTimeMillis();
	long start = criteria.getStartDate() != null ? criteria.getStartDate().getTime() : current - 1;
	getLogger().info("Buckets in range " + new Date(start) + " to " + new Date(current));
	List<Integer> buckets = new ArrayList<>();
	while (current >= start) {
	    buckets.add(getClient().getBucketValue(current));
	    current -= bucket;
	}
	getLogger().info("Processing " + buckets.size() + " buckets.");
	return buckets;
    }

    /**
     * Perform parallel queries to get results for a single bucket.
     * 
     * @param keyField
     * @param keys
     * @param criteria
     * @param eventType
     * @param bucket
     * @return
     * @throws SiteWhereException
     */
    protected List<ResultSet> listResultsForBucket(PreparedStatement statement, String keyField, List<UUID> keys,
	    IDateRangeSearchCriteria criteria, DeviceEventType eventType, int bucket) throws SiteWhereException {
	List<ResultSetFuture> futures = new ArrayList<>();
	for (UUID key : keys) {
	    BoundStatement query = statement.bind();
	    query.setUUID(0, key);
	    query.setByte(1, CassandraDeviceEvent.getIndicatorForEventType(eventType));
	    query.setInt(2, bucket);
	    query.setTimestamp(3, criteria.getStartDate());
	    query.setTimestamp(4, criteria.getEndDate());
	    ResultSetFuture resultSetFuture = getClient().getSession().executeAsync(query);
	    futures.add(resultSetFuture);
	}
	List<ResultSet> results = new ArrayList<>();
	for (ResultSetFuture future : futures) {
	    results.add(future.getUninterruptibly());
	}
	return results;
    }

    /**
     * Sort the list of device events, then add them to the pager.
     * 
     * @param pager
     * @param events
     */
    protected <T extends IDeviceEvent> void addSortedEventsToPager(Pager<T> pager, List<T> events, int bucket) {
	Collections.sort(events, new Comparator<T>() {

	    @Override
	    public int compare(IDeviceEvent o1, IDeviceEvent o2) {
		return 1 - (o1.getEventDate().compareTo(o2.getEventDate()));
	    }
	});
	for (T event : events) {
	    pager.process(event);
	}
	getLogger().info("Processed " + events.size() + " events for bucket " + bucket + ".");
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignmentById(UUID id) throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(id);
	if (assignment == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
	}
	return assignment;
    }

    protected IDeviceManagement getDeviceManagement() {
	return ((IEventManagementMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }

    public CassandraClient getClient() {
	return client;
    }

    public void setClient(CassandraClient client) {
	this.client = client;
    }
}