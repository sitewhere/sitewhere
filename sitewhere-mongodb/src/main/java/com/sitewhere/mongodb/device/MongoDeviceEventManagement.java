/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.device.AssignmentStateManager;
import com.sitewhere.mongodb.IDeviceManagementMongoClient;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.event.DeviceStreamData;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IAssignmentStateManager;
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
public class MongoDeviceEventManagement extends TenantLifecycleComponent implements IDeviceEventManagement {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected with global SiteWhere Mongo client */
    private IDeviceManagementMongoClient mongoClient;

    /** Injected device management implementation */
    private IDeviceManagement deviceManagement;

    /** Device event buffer implementation */
    private IDeviceEventBuffer eventBuffer;

    /** Assignment state manager */
    private IAssignmentStateManager assignmentStateManager;

    /** Indicates whether bulk inserts should be used for adding events */
    private boolean useBulkEventInserts = false;

    /** Maximum number of records to write in a chunk */
    private int bulkInsertMaxChunkSize = 1000;

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
	    this.eventBuffer = new DeviceEventBuffer(getMongoClient().getEventsCollection(getTenant()),
		    getBulkInsertMaxChunkSize());
	    getEventBuffer().start();
	    LOGGER.info("MongoDB device event management is using bulk inserts for events.");
	} else {
	    LOGGER.info("MongoDB device event management is not using bulk inserts for events.");
	}

	// Create assignment state manager and start it.
	this.assignmentStateManager = new AssignmentStateManager(getDeviceManagement());
	startNestedComponent(assignmentStateManager, monitor, true);
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

	// Stop the assignment state manager.
	if (assignmentStateManager != null) {
	    assignmentStateManager.stop(monitor);
	}
    }

    /**
     * Ensure that expected collection indexes exist.
     * 
     * @throws SiteWhereException
     */
    protected void ensureIndexes() throws SiteWhereException {
	getMongoClient().getEventsCollection(getTenant())
		.createIndex(new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, 1)
			.append(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_EVENT_TYPE, 1));
	getMongoClient().getEventsCollection(getTenant())
		.createIndex(new BasicDBObject(MongoDeviceEvent.PROP_SITE_TOKEN, 1)
			.append(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_EVENT_TYPE, 1));
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
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceEventBatch
     * (java. lang.String, com.sitewhere.spi.device.event.IDeviceEventBatch)
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
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceEventById(
     * java.lang .String)
     */
    @Override
    public IDeviceEvent getDeviceEventById(String id) throws SiteWhereException {
	Document searchById = new Document("_id", new ObjectId(id));
	Document found = getMongoClient().getEventsCollection(getTenant()).find(searchById).first();
	if (found == null) {
	    return null;
	}
	return MongoPersistence.unmarshalEvent(found);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceEvents(
     * java.lang .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceEvent> listDeviceEvents(String assignmentToken, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken);
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
		matches.add(MongoPersistence.unmarshalEvent(match));
	    }
	} finally {
	    cursor.close();
	}
	return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceMeasurements(java .lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
     */
    @Override
    public IDeviceMeasurements addDeviceMeasurements(String assignmentToken, IDeviceMeasurementsCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
	DeviceMeasurements measurements = SiteWherePersistence.deviceMeasurementsCreateLogic(request, assignment);

	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document mObject = MongoDeviceMeasurements.toDocument(measurements, false);
	MongoPersistence.insertEvent(events, mObject, isUseBulkEventInserts(), getEventBuffer());

	// Update assignment state if requested.
	measurements = MongoDeviceMeasurements.fromDocument(mObject, false);
	if (request.isUpdateState()) {
	    getAssignmentStateManager().addMeasurements(assignmentToken, measurements);
	}

	return measurements;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurements(java .lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceMeasurements> listDeviceMeasurements(String token, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, token)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Measurements.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceMeasurements.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForSite (java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceMeasurements> listDeviceMeasurementsForSite(String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Measurements.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceMeasurements.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceLocation(
     * java.lang .String,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
     */
    @Override
    public IDeviceLocation addDeviceLocation(String assignmentToken, IDeviceLocationCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
	DeviceLocation location = SiteWherePersistence.deviceLocationCreateLogic(assignment, request);

	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document locObject = MongoDeviceLocation.toDocument(location, false);
	MongoPersistence.insertEvent(events, locObject, isUseBulkEventInserts(), getEventBuffer());

	// Update assignment state if requested.
	location = MongoDeviceLocation.fromDocument(locObject, false);
	if (request.isUpdateState()) {
	    getAssignmentStateManager().addLocation(assignmentToken, location);
	}

	return location;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocations
     * (java. lang.String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceLocation> listDeviceLocations(String assignmentToken, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceLocation.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForSite (java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceLocation> listDeviceLocationsForSite(String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceLocation.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceLocations
     * (java. util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceLocation> listDeviceLocations(List<String> assignmentTokens,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document();
	query.put(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, new BasicDBObject("$in", assignmentTokens));
	query.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceLocation.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceAlert(java
     * .lang. String,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
     */
    @Override
    public IDeviceAlert addDeviceAlert(String assignmentToken, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
	DeviceAlert alert = SiteWherePersistence.deviceAlertCreateLogic(assignment, request);

	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document alertObject = MongoDeviceAlert.toDocument(alert, false);
	MongoPersistence.insertEvent(events, alertObject, isUseBulkEventInserts(), getEventBuffer());

	// Update assignment state if requested.
	alert = MongoDeviceAlert.fromDocument(alertObject, false);
	if (request.isUpdateState()) {
	    getAssignmentStateManager().addAlert(assignmentToken, alert);
	}

	return alert;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceAlerts(
     * java.lang .String, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceAlert> listDeviceAlerts(String assignmentToken, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Alert.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceAlert.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForSite(java .lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceAlert> listDeviceAlertsForSite(String siteToken, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Alert.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceAlert.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStreamData
     * (java. lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public IDeviceStreamData addDeviceStreamData(String assignmentToken, IDeviceStreamDataCreateRequest request)
	    throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
	DeviceStreamData streamData = SiteWherePersistence.deviceStreamDataCreateLogic(assignment, request);

	// Verify that a stream with the given id exists for the assignment.
	if (getDeviceManagement().getDeviceStream(assignmentToken, request.getStreamId()) == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidStreamId, ErrorLevel.ERROR);
	}

	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document streamDataObject = MongoDeviceStreamData.toDocument(streamData, false);
	MongoPersistence.insertEvent(events, streamDataObject, isUseBulkEventInserts(), getEventBuffer());

	return MongoDeviceStreamData.fromDocument(streamDataObject, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceStreamData
     * (java. lang.String, java.lang.String, long)
     */
    @Override
    public IDeviceStreamData getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	Document dbData = getDeviceStreamDataDocument(assignmentToken, streamId, sequenceNumber);
	if (dbData == null) {
	    return null;
	}
	return MongoDeviceStreamData.fromDocument(dbData, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStreamData(java .lang.String, java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStreamData> listDeviceStreamData(String assignmentToken, String streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StreamData.name())
		.append(MongoDeviceStreamData.PROP_STREAM_ID, streamId);
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceStreamData.PROP_SEQUENCE_NUMBER, 1);
	return MongoPersistence.search(IDeviceStreamData.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandInvocation (java.lang.String,
     * com.sitewhere.spi.device.command.IDeviceCommand,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandInvocationCreateRequest)
     */
    @Override
    public IDeviceCommandInvocation addDeviceCommandInvocation(String assignmentToken, IDeviceCommand command,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
	DeviceCommandInvocation ci = SiteWherePersistence.deviceCommandInvocationCreateLogic(assignment, command,
		request);

	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document ciObject = MongoDeviceCommandInvocation.toDocument(ci);
	MongoPersistence.insertEvent(events, ciObject, isUseBulkEventInserts(), getEventBuffer());

	return MongoDeviceCommandInvocation.fromDocument(ciObject);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocations (java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocations(String assignmentToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandInvocation.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandInvocation.class, events, query, sort, criteria);
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
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandInvocation.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandInvocation.class, events, query, sort, criteria);
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
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name())
		.append(MongoDeviceCommandResponse.PROP_ORIGINATING_EVENT_ID, invocationId);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandResponse( java.lang.String,
     * com.sitewhere.spi.device.event.request.
     * IDeviceCommandResponseCreateRequest)
     */
    @Override
    public IDeviceCommandResponse addDeviceCommandResponse(String assignmentToken,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
	DeviceCommandResponse response = SiteWherePersistence.deviceCommandResponseCreateLogic(assignment, request);

	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document dbresponse = MongoDeviceCommandResponse.toDocument(response);
	MongoPersistence.insertEvent(events, dbresponse, isUseBulkEventInserts(), getEventBuffer());

	return MongoDeviceCommandResponse.fromDocument(dbresponse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponses (java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponses(String assignmentToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort, criteria);
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
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceStateChange(java .lang.String,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
     */
    @Override
    public IDeviceStateChange addDeviceStateChange(String assignmentToken, IDeviceStateChangeCreateRequest request)
	    throws SiteWhereException {
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentToken);
	DeviceStateChange state = SiteWherePersistence.deviceStateChangeCreateLogic(assignment, request);

	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document dbstate = MongoDeviceStateChange.toDocument(state);
	MongoPersistence.insertEvent(events, dbstate, isUseBulkEventInserts(), getEventBuffer());

	state = MongoDeviceStateChange.fromDocument(dbstate);
	if (request.isUpdateState()) {
	    getAssignmentStateManager().addStateChange(assignmentToken, state);
	}

	return state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChanges(java .lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChanges(String assignmentToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StateChange.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceStateChange.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForSite (java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForSite(String siteToken,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	Document query = new Document(MongoDeviceEvent.PROP_SITE_TOKEN, siteToken)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StateChange.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1).append(MongoDeviceEvent.PROP_RECEIVED_DATE,
		-1);
	return MongoPersistence.search(IDeviceStateChange.class, events, query, sort, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceManagement
     * ()
     */
    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#setDeviceManagement
     * (com. sitewhere .spi.device.IDeviceManagement)
     */
    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Get the {@link Document} for an {@link IDeviceStreamData} chunk based on
     * assignment token, stream id, and sequence number.
     * 
     * @param assignmentToken
     * @param streamId
     * @param sequenceNumber
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStreamDataDocument(String assignmentToken, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	try {
	    MongoCollection<Document> events = getMongoClient().getEventsCollection(getTenant());
	    Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_TOKEN, assignmentToken)
		    .append(MongoDeviceStreamData.PROP_STREAM_ID, streamId)
		    .append(MongoDeviceStreamData.PROP_SEQUENCE_NUMBER, sequenceNumber);
	    return events.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    /**
     * Return an {@link IDeviceAssignment} for the given token. Throws an
     * exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertApiDeviceAssignment(String token) throws SiteWhereException {
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(token);
	if (assignment == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
	}
	return assignment;
    }

    public IDeviceEventBuffer getEventBuffer() {
	return eventBuffer;
    }

    public void setEventBuffer(IDeviceEventBuffer eventBuffer) {
	this.eventBuffer = eventBuffer;
    }

    public IAssignmentStateManager getAssignmentStateManager() {
	return assignmentStateManager;
    }

    public void setAssignmentStateManager(IAssignmentStateManager assignmentStateManager) {
	this.assignmentStateManager = assignmentStateManager;
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

    public IDeviceManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(IDeviceManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}