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

import org.reactivestreams.Processor;

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
import com.sitewhere.event.persistence.streaming.DeviceAssignmentEventCreateProcessor;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
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
import com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.streaming.IEventStreamAck;
import com.sitewhere.spi.device.streaming.IDeviceStream;
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
     * .util.UUID, java.util.UUID)
     */
    @Override
    public IDeviceEvent getDeviceEventById(UUID deviceId, UUID eventId) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.util.UUID, java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(UUID deviceId, String alternateId) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * streamDeviceAssignmentCreateEvents()
     */
    @Override
    public Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> streamDeviceAssignmentCreateEvents() {
	return new DeviceAssignmentEventCreateProcessor(this);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceMeasurements(
     * java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
     */
    @Override
    public IDeviceMeasurements addDeviceMeasurements(UUID deviceAssignmentId, IDeviceMeasurementsCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	DeviceMeasurements mxs = DeviceEventManagementPersistence.deviceMeasurementsCreateLogic(request, assignment);
	return mxs;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForAssignments(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceLocation(java.
     * util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
     */
    @Override
    public IDeviceLocation addDeviceLocation(UUID deviceAssignmentId, IDeviceLocationCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
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

	return location;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForAssignments(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<IDeviceLocation> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(getClient().getSelectEventsByAssignmentForType(),
		    "assignmentId", assignmentIds, criteria, DeviceEventType.Location, bucket);
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
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<IDeviceLocation> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(getClient().getSelectEventsByAreaForType(), "areaId",
		    areaIds, criteria, DeviceEventType.Location, bucket);
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
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceAlert(java.
     * util.UUID, com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
     */
    @Override
    public IDeviceAlert addDeviceAlert(UUID deviceAssignmentId, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
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

	return alert;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForAssignments(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	Pager<IDeviceAlert> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(getClient().getSelectEventsByAssignmentForType(),
		    "assignmentId", assignmentIds, criteria, DeviceEventType.Alert, bucket);
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
     * listDeviceAlertsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForAreas(List<UUID> areaIds, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	Pager<IDeviceAlert> pager = new Pager<>(criteria);
	List<Integer> buckets = getBucketsForDateRange(criteria);
	for (int bucket : buckets) {
	    List<ResultSet> perBucket = listResultsForBucket(getClient().getSelectEventsByAreaForType(), "areaId",
		    areaIds, criteria, DeviceEventType.Alert, bucket);
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
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStreamData(
     * java.util.UUID, com.sitewhere.spi.device.streaming.IDeviceStream,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public IDeviceStreamData addDeviceStreamData(UUID deviceAssignmentId, IDeviceStream stream,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceStreamData(
     * java.util.UUID, java.lang.String, long)
     */
    @Override
    public IDeviceStreamData getDeviceStreamData(UUID deviceAssignmentId, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStreamDataForAssignment(java.util.UUID, java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStreamData> listDeviceStreamDataForAssignment(UUID assignmentId, String streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandInvocation(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
     */
    @Override
    public IDeviceCommandInvocation addDeviceCommandInvocation(UUID deviceAssignmentId,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForAssignments(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationResponses(java.util.UUID, java.util.UUID)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID deviceId, UUID invocationId)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandResponse(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
     */
    @Override
    public IDeviceCommandResponse addDeviceCommandResponse(UUID deviceAssignmentId,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForAssignments(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStateChange(
     * java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
     */
    @Override
    public IDeviceStateChange addDeviceStateChange(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest request)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForAssignments(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForAssignments(List<UUID> assignmentIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
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