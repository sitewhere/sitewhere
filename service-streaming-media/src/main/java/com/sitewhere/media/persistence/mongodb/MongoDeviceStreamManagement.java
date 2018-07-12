/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media.persistence.mongodb;

import java.util.UUID;

import org.bson.Document;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.device.streaming.IDeviceStreamData;
import com.sitewhere.spi.device.streaming.IDeviceStreamManagement;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IDeviceStreamManagement} that stores data in
 * MongoDB.
 * 
 * @author Derek
 */
public class MongoDeviceStreamManagement extends TenantEngineLifecycleComponent implements IDeviceStreamManagement {

    /** Injected with Mongo client */
    private IDeviceStreamManagementMongoClient mongoClient;

    public MongoDeviceStreamManagement() {
	super(LifecycleComponentType.DataStore);
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
	// IDeviceAssignment assignment =
	// assertDeviceAssignmentById(deviceAssignmentId);
	// // Use common logic so all backend implementations work the same.
	// DeviceStreamData streamData =
	// DeviceEventManagementPersistence.deviceStreamDataCreateLogic(assignment,
	// request);
	//
	// MongoCollection<Document> streamdata =
	// getMongoClient().getStreamDataCollection();
	// Document streamDataObject = MongoDeviceStreamData.toDocument(streamData,
	// false);
	// MongoDeviceEventManagementPersistence.insertEvent(events, streamDataObject,
	// isUseBulkEventInserts(),
	// getEventBuffer());
	//
	// return MongoDeviceStreamData.fromDocument(streamDataObject, false);
	throw new SiteWhereException("Streaming data not supported by MongoDB.");
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceStreamData(
     * java.util.UUID, java.lang.String, long)
     */
    @Override
    public IDeviceStreamData getDeviceStreamData(UUID deviceAssignmentId, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	// Document dbData = getDeviceStreamDataDocument(deviceAssignmentId, streamId,
	// sequenceNumber);
	// if (dbData == null) {
	// return null;
	// }
	// return MongoDeviceStreamData.fromDocument(dbData, false);
	throw new SiteWhereException("Streaming data not supported by MongoDB.");
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStreamDataForAssignment(java.util.UUID, java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStreamData> listDeviceStreamDataForAssignment(UUID assignmentId, String streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	// MongoCollection<Document> streamdata =
	// getMongoClient().getStreamDataCollection();
	// Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID,
	// assignmentId)
	// .append(MongoDeviceEvent.PROP_EVENT_TYPE, DeviceEventType.StreamData.name())
	// .append(MongoDeviceStreamData.PROP_STREAM_ID, streamId);
	// MongoPersistence.addDateSearchCriteria(query,
	// MongoDeviceEvent.PROP_EVENT_DATE, criteria);
	// Document sort = new Document(MongoDeviceStreamData.PROP_SEQUENCE_NUMBER, 1);
	// return MongoPersistence.search(IDeviceStreamData.class, events, query, sort,
	// criteria, LOOKUP);
	throw new SiteWhereException("Streaming data not supported by MongoDB.");
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
	// try {
	// MongoCollection<Document> events = getMongoClient().getEventsCollection();
	// Document query = new Document(MongoDeviceEvent.PROP_DEVICE_ASSIGNMENT_ID,
	// assignmentId)
	// .append(MongoDeviceStreamData.PROP_STREAM_ID, streamId)
	// .append(MongoDeviceStreamData.PROP_SEQUENCE_NUMBER, sequenceNumber);
	// return events.find(query).first();
	// } catch (MongoTimeoutException e) {
	// throw new SiteWhereException("Connection to MongoDB lost.", e);
	// }
	return null;
    }

    public IDeviceStreamManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(IDeviceStreamManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}
