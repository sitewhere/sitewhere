/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.reactivestreams.Processor;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.event.persistence.streaming.DeviceAssignmentEventCreateProcessor;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.event.DeviceStreamData;
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
 * Device event management implementation that uses MongoDB for persistence.
 * 
 * @author Derek
 */
public class MongoDeviceEventManagement extends TenantEngineLifecycleComponent implements IDeviceEventManagement {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(MongoDeviceEventManagement.class);

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with global SiteWhere Mongo client */
    private IDeviceEventManagementMongoClient mongoClient;

    /** Device event buffer implementation */
    private IDeviceEventBuffer eventBuffer;

    /** Indicates whether bulk inserts should be used for adding events */
    private boolean useBulkEventInserts = true;

    /** Maximum number of records to write in a chunk */
    private int bulkInsertMaxChunkSize = 200;

    public MongoDeviceEventManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Ensure that collection indexes exist.
	ensureIndexes();

	// Support bulk inserts for events.
	if (isUseBulkEventInserts()) {
	    this.eventBuffer = new DeviceEventBuffer(getMongoClient().getEventsCollection(),
		    getBulkInsertMaxChunkSize());
	    getEventBuffer().start();
	    LOGGER.info("MongoDB device event management is using bulk inserts for events.");
	} else {
	    LOGGER.info("MongoDB device event management is not using bulk inserts for events.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop the event buffer if used.
	if (getEventBuffer() != null) {
	    getEventBuffer().stop();
	}
    }

    /**
     * Ensure that expected collection indexes exist.
     * 
     * @throws SiteWhereException
     */
    protected void ensureIndexes() throws SiteWhereException {
	getMongoClient().getEventsCollection().createIndex(new BasicDBObject(MongoDeviceEvent.PROP_ALTERNATE_ID, 1),
		new IndexOptions().unique(true).sparse(true));
	getMongoClient().getEventsCollection()
		.createIndex(new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, 1)
			.append(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_EVENT_TYPE, 1));
	getMongoClient().getEventsCollection().createIndex(new BasicDBObject(MongoDeviceEvent.PROP_AREA_ID, 1)
		.append(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_EVENT_TYPE, 1));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
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
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ID, deviceId).append(MongoDeviceEvent.PROP_ID,
		eventId);
	Document found = getMongoClient().getEventsCollection().find(query).first();
	if (found == null) {
	    return null;
	}
	return MongoDeviceEventManagementPersistence.unmarshalEvent(found);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.util.UUID, java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(UUID deviceId, String alternateId) throws SiteWhereException {
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ID, deviceId)
		.append(MongoDeviceEvent.PROP_ALTERNATE_ID, alternateId);
	Document found = getMongoClient().getEventsCollection().find(query).first();
	if (found == null) {
	    return null;
	}
	return MongoDeviceEventManagementPersistence.unmarshalEvent(found);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceEvents(java.
     * util.UUID, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceEvent> listDeviceEvents(UUID deviceAssignmentId, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, assignment.getId());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);

	int offset = Math.max(0, criteria.getPageNumber() - 1) * criteria.getPageSize();
	FindIterable<Document> found = events.find(query).skip(offset).limit(criteria.getPageSize()).sort(sort);
	MongoCursor<Document> cursor = found.iterator();

	List<IDeviceEvent> matches = new ArrayList<IDeviceEvent>();
	SearchResults<IDeviceEvent> results = new SearchResults<IDeviceEvent>(matches);
	try {
	    results.setNumResults(events.count(query));
	    while (cursor.hasNext()) {
		Document match = cursor.next();
		matches.add(MongoDeviceEventManagementPersistence.unmarshalEvent(match));
	    }
	} finally {
	    cursor.close();
	}
	return results;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * streamDeviceAssignmentCreateEvents()
     */
    @Override
    public Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> streamDeviceAssignmentCreateEvents()
	    throws SiteWhereException {
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
	DeviceMeasurements measurements = DeviceEventManagementPersistence.deviceMeasurementsCreateLogic(request,
		assignment);

	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document mObject = MongoDeviceMeasurements.toDocument(measurements, false);
	MongoDeviceEventManagementPersistence.insertEvent(events, mObject, isUseBulkEventInserts(), getEventBuffer());
	return MongoDeviceMeasurements.fromDocument(mObject, false);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceMeasurements> listDeviceMeasurementsForAssignment(UUID assignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, assignmentId)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Measurements.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceMeasurements.class, events, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceMeasurements> listDeviceMeasurementsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_AREA_ID, createAreasInClause(areaIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Measurements.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceMeasurements.class, events, query, sort, criteria, LOOKUP);
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

	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document locObject = MongoDeviceLocation.toDocument(location, false);
	MongoDeviceEventManagementPersistence.insertEvent(events, locObject, isUseBulkEventInserts(), getEventBuffer());
	return MongoDeviceLocation.fromDocument(locObject, false);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceLocation> listDeviceLocationsForAssignment(UUID assignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, assignmentId)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceLocation.class, events, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceLocation> listDeviceLocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_AREA_ID, createAreasInClause(areaIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceLocation.class, events, query, sort, criteria, LOOKUP);
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

	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document alertObject = MongoDeviceAlert.toDocument(alert, false);
	MongoDeviceEventManagementPersistence.insertEvent(events, alertObject, isUseBulkEventInserts(),
		getEventBuffer());
	return MongoDeviceAlert.fromDocument(alertObject, false);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceAlert> listDeviceAlertsForAssignment(UUID assignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, assignmentId)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Alert.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceAlert.class, events, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceAlert> listDeviceAlertsForAreas(List<UUID> areaIds, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_AREA_ID, createAreasInClause(areaIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Alert.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceAlert.class, events, query, sort, criteria, LOOKUP);
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
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	// Use common logic so all backend implementations work the same.
	DeviceStreamData streamData = DeviceEventManagementPersistence.deviceStreamDataCreateLogic(assignment, request);

	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document streamDataObject = MongoDeviceStreamData.toDocument(streamData, false);
	MongoDeviceEventManagementPersistence.insertEvent(events, streamDataObject, isUseBulkEventInserts(),
		getEventBuffer());

	return MongoDeviceStreamData.fromDocument(streamDataObject, false);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceStreamData(
     * java.util.UUID, java.lang.String, long)
     */
    @Override
    public IDeviceStreamData getDeviceStreamData(UUID deviceAssignmentId, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	Document dbData = getDeviceStreamDataDocument(deviceAssignmentId, streamId, sequenceNumber);
	if (dbData == null) {
	    return null;
	}
	return MongoDeviceStreamData.fromDocument(dbData, false);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStreamDataForAssignment(java.util.UUID, java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStreamData> listDeviceStreamDataForAssignment(UUID assignmentId, String streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, assignmentId)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StreamData.name())
		.append(MongoDeviceStreamData.PROP_STREAM_ID, streamId);
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceStreamData.PROP_SEQUENCE_NUMBER, 1);
	return MongoPersistence.search(IDeviceStreamData.class, events, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandInvocation(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
     */
    @Override
    public IDeviceCommandInvocation addDeviceCommandInvocation(UUID deviceAssignmentId,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	DeviceCommandInvocation ci = DeviceEventManagementPersistence.deviceCommandInvocationCreateLogic(assignment,
		request);

	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document ciObject = MongoDeviceCommandInvocation.toDocument(ci);
	MongoDeviceEventManagementPersistence.insertEvent(events, ciObject, isUseBulkEventInserts(), getEventBuffer());

	return MongoDeviceCommandInvocation.fromDocument(ciObject);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForAssignment(UUID assignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, assignmentId)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandInvocation.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandInvocation.class, events, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_AREA_ID, createAreasInClause(areaIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandInvocation.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandInvocation.class, events, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationResponses(java.util.UUID, java.util.UUID)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID deviceId, UUID invocationId)
	    throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ID, deviceId)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name())
		.append(MongoDeviceCommandResponse.PROP_ORIGINATING_EVENT_ID, invocationId);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandResponse(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
     */
    @Override
    public IDeviceCommandResponse addDeviceCommandResponse(UUID deviceAssignmentId,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	DeviceCommandResponse response = DeviceEventManagementPersistence.deviceCommandResponseCreateLogic(assignment,
		request);

	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document dbresponse = MongoDeviceCommandResponse.toDocument(response);
	MongoDeviceEventManagementPersistence.insertEvent(events, dbresponse, isUseBulkEventInserts(),
		getEventBuffer());

	return MongoDeviceCommandResponse.fromDocument(dbresponse);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForAssignment(UUID assignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, assignmentId)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_AREA_ID, createAreasInClause(areaIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort, criteria, LOOKUP);
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
	IDeviceAssignment assignment = assertDeviceAssignmentById(deviceAssignmentId);
	DeviceStateChange state = DeviceEventManagementPersistence.deviceStateChangeCreateLogic(assignment, request);

	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document dbstate = MongoDeviceStateChange.toDocument(state);
	MongoDeviceEventManagementPersistence.insertEvent(events, dbstate, isUseBulkEventInserts(), getEventBuffer());
	return MongoDeviceStateChange.fromDocument(dbstate);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForAssignment(UUID assignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, assignmentId)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StateChange.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceStateChange.class, events, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_AREA_ID, createAreasInClause(areaIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StateChange.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceStateChange.class, events, query, sort, criteria, LOOKUP);
    }

    /**
     * Get the {@link Document} for an {@link IDeviceStreamData} chunk based on
     * assignment token, stream id, and sequence number.
     * 
     * @param assignmentId
     * @param streamId
     * @param sequenceNumber
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStreamDataDocument(UUID assignmentId, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	try {
	    MongoCollection<Document> events = getMongoClient().getEventsCollection();
	    Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, assignmentId)
		    .append(MongoDeviceStreamData.PROP_STREAM_ID, streamId)
		    .append(MongoDeviceStreamData.PROP_SEQUENCE_NUMBER, sequenceNumber);
	    return events.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
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

    /**
     * Create "in" clause for a list of areas.
     * 
     * @param areas
     * @return
     */
    protected Document createAreasInClause(List<UUID> areas) {
	return new Document("$in", areas);
    }

    public IDeviceEventBuffer getEventBuffer() {
	return eventBuffer;
    }

    public void setEventBuffer(IDeviceEventBuffer eventBuffer) {
	this.eventBuffer = eventBuffer;
    }

    public boolean isUseBulkEventInserts() {
	return useBulkEventInserts;
    }

    public void setUseBulkEventInserts(boolean useBulkEventInserts) {
	this.useBulkEventInserts = useBulkEventInserts;
    }

    public int getBulkInsertMaxChunkSize() {
	return bulkInsertMaxChunkSize;
    }

    public void setBulkInsertMaxChunkSize(int bulkInsertMaxChunkSize) {
	this.bulkInsertMaxChunkSize = bulkInsertMaxChunkSize;
    }

    public IDeviceEventManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(IDeviceEventManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}