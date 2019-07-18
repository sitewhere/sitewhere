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

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sitewhere.event.persistence.DeviceEventManagementPersistence;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.MongoTenantComponent;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
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
 * Device event management implementation that uses MongoDB for persistence.
 */
public class MongoDeviceEventManagement extends MongoTenantComponent<DeviceEventManagementMongoClient>
	implements IDeviceEventManagement {

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with global SiteWhere Mongo client */
    private DeviceEventManagementMongoClient mongoClient;

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
	super.start(monitor);

	// Support bulk inserts for events.
	if (isUseBulkEventInserts()) {
	    this.eventBuffer = new DeviceEventBuffer(getMongoClient().getEventsCollection(),
		    getBulkInsertMaxChunkSize());
	    getEventBuffer().start();
	    getLogger().info("MongoDB device event management is using bulk inserts for events.");
	} else {
	    getLogger().info("MongoDB device event management is not using bulk inserts for events.");
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
	super.stop(monitor);
    }

    /*
     * @see com.sitewhere.mongodb.MongoTenantComponent#ensureIndexes()
     */
    @Override
    public void ensureIndexes() throws SiteWhereException {
	getMongoClient().getEventsCollection().createIndex(new BasicDBObject(MongoDeviceEvent.PROP_ALTERNATE_ID, 1),
		new IndexOptions().unique(true).sparse(true));
	getMongoClient().getEventsCollection()
		.createIndex(new BasicDBObject(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID, 1)
			.append(MongoDeviceEvent.PROP_EVENT_TYPE, 1).append(MongoDeviceEvent.PROP_EVENT_DATE, -1));
	getMongoClient().getEventsCollection().createIndex(new BasicDBObject(MongoDeviceEvent.PROP_CUSTOMER_ID, 1)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, 1).append(MongoDeviceEvent.PROP_EVENT_DATE, -1));
	getMongoClient().getEventsCollection().createIndex(new BasicDBObject(MongoDeviceEvent.PROP_AREA_ID, 1)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, 1).append(MongoDeviceEvent.PROP_EVENT_DATE, -1));
	getMongoClient().getEventsCollection().createIndex(new BasicDBObject(MongoDeviceEvent.PROP_ASSET_ID, 1)
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, 1).append(MongoDeviceEvent.PROP_EVENT_DATE, -1));
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
	Document query = new Document(MongoDeviceEvent.PROP_ID, eventId);
	Document found = getMongoClient().getEventsCollection().find(query).first();
	if (found == null) {
	    return null;
	}
	return MongoDeviceEventManagementPersistence.unmarshalEvent(found);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(String alternateId) throws SiteWhereException {
	Document query = new Document(MongoDeviceEvent.PROP_ALTERNATE_ID, alternateId);
	Document found = getMongoClient().getEventsCollection().find(query).first();
	if (found == null) {
	    return null;
	}
	return MongoDeviceEventManagementPersistence.unmarshalEvent(found);
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
	    DeviceMeasurement measurements = DeviceEventManagementPersistence.deviceMeasurementCreateLogic(request,
		    assignment);

	    MongoCollection<Document> events = getMongoClient().getEventsCollection();
	    Document mObject = MongoDeviceMeasurement.toDocument(measurements, false);
	    MongoDeviceEventManagementPersistence.insertEvent(events, mObject, isUseBulkEventInserts(),
		    getEventBuffer());
	    result.add(MongoDeviceMeasurement.fromDocument(mObject, false));
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
    public SearchResults<IDeviceMeasurement> listDeviceMeasurementsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(getFieldForIndex(index), new Document("$in", entityIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Measurement.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1);
	return MongoPersistence.search(IDeviceMeasurement.class, events, query, sort, criteria, LOOKUP);
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

	    MongoCollection<Document> events = getMongoClient().getEventsCollection();
	    Document locObject = MongoDeviceLocation.toDocument(location, false);
	    MongoDeviceEventManagementPersistence.insertEvent(events, locObject, isUseBulkEventInserts(),
		    getEventBuffer());
	    result.add(MongoDeviceLocation.fromDocument(locObject, false));
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(getFieldForIndex(index), new Document("$in", entityIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Location.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1);
	return MongoPersistence.search(IDeviceLocation.class, events, query, sort, criteria, LOOKUP);
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

	    MongoCollection<Document> events = getMongoClient().getEventsCollection();
	    Document alertObject = MongoDeviceAlert.toDocument(alert, false);
	    MongoDeviceEventManagementPersistence.insertEvent(events, alertObject, isUseBulkEventInserts(),
		    getEventBuffer());
	    result.add(MongoDeviceAlert.fromDocument(alertObject, false));
	}
	return result;
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public SearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(getFieldForIndex(index), new Document("$in", entityIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.Alert.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1);
	return MongoPersistence.search(IDeviceAlert.class, events, query, sort, criteria, LOOKUP);
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
	    IDeviceCommand command = getCachedDeviceManagement().getDeviceCommandByToken(assignment.getDeviceTypeId(),
		    request.getCommandToken());
	    DeviceCommandInvocation ci = DeviceEventManagementPersistence.deviceCommandInvocationCreateLogic(assignment,
		    command, request);

	    MongoCollection<Document> events = getMongoClient().getEventsCollection();
	    Document ciObject = MongoDeviceCommandInvocation.toDocument(ci);
	    MongoDeviceEventManagementPersistence.insertEvent(events, ciObject, isUseBulkEventInserts(),
		    getEventBuffer());
	    result.add(MongoDeviceCommandInvocation.fromDocument(ciObject));
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
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(getFieldForIndex(index), new Document("$in", entityIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandInvocation.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1);
	return MongoPersistence.search(IDeviceCommandInvocation.class, events, query, sort, criteria, LOOKUP);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationResponses(java.util.UUID)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID invocationId)
	    throws SiteWhereException {
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name())
		.append(MongoDeviceCommandResponse.PROP_ORIGINATING_EVENT_ID, invocationId);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1);
	return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort, LOOKUP);
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

	    MongoCollection<Document> events = getMongoClient().getEventsCollection();
	    Document dbresponse = MongoDeviceCommandResponse.toDocument(response);
	    MongoDeviceEventManagementPersistence.insertEvent(events, dbresponse, isUseBulkEventInserts(),
		    getEventBuffer());
	    result.add(MongoDeviceCommandResponse.fromDocument(dbresponse));
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
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(getFieldForIndex(index), new Document("$in", entityIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.CommandResponse.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1);
	return MongoPersistence.search(IDeviceCommandResponse.class, events, query, sort, criteria, LOOKUP);
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

	    MongoCollection<Document> events = getMongoClient().getEventsCollection();
	    Document dbstate = MongoDeviceStateChange.toDocument(state);
	    MongoDeviceEventManagementPersistence.insertEvent(events, dbstate, isUseBulkEventInserts(),
		    getEventBuffer());
	    result.add(MongoDeviceStateChange.fromDocument(dbstate));
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
	MongoCollection<Document> events = getMongoClient().getEventsCollection();
	Document query = new Document(getFieldForIndex(index), new Document("$in", entityIds))
		.append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StateChange.name());
	MongoPersistence.addDateSearchCriteria(query, MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	Document sort = new Document(MongoDeviceEvent.PROP_EVENT_DATE, -1);
	return MongoPersistence.search(IDeviceStateChange.class, events, query, sort, criteria, LOOKUP);
    }

    /**
     * Assert that a device assignment exists and throw an exception if not.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceAssignment assertDeviceAssignmentById(UUID id) throws SiteWhereException {
	IDeviceAssignment assignment = getCachedDeviceManagement().getDeviceAssignment(id);
	if (assignment == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
	}
	return assignment;
    }

    /**
     * Get field name associated with index.
     * 
     * @param index
     * @return
     * @throws SiteWhereException
     */
    protected static String getFieldForIndex(DeviceEventIndex index) throws SiteWhereException {
	switch (index) {
	case Area: {
	    return MongoDeviceEvent.PROP_AREA_ID;
	}
	case Asset: {
	    return MongoDeviceEvent.PROP_ASSET_ID;
	}
	case Assignment: {
	    return MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID;
	}
	case Customer: {
	    return MongoDeviceEvent.PROP_CUSTOMER_ID;
	}
	}
	throw new SiteWhereException("Unknown index: " + index.name());
    }

    protected IDeviceManagement getCachedDeviceManagement() {
	return ((IEventManagementMicroservice) getTenantEngine().getMicroservice()).getCachedDeviceManagement();
    }

    /**
     * Create "in" clause for a list of area ids.
     * 
     * @param areaIds
     * @return
     */
    protected Document createAreasInClause(List<UUID> areaIds) {
	return new Document("$in", areaIds);
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

    /*
     * @see com.sitewhere.mongodb.MongoTenantComponent#getMongoClient()
     */
    @Override
    public DeviceEventManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(DeviceEventManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}