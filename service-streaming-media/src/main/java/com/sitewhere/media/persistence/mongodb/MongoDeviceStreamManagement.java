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

import com.mongodb.MongoClientException;
import com.mongodb.client.MongoCollection;
import com.sitewhere.media.persistence.DeviceStreamPersistence;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoPersistentEntity;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.device.streaming.IDeviceStreamManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * 
 * @author Derek
 *
 */
public class MongoDeviceStreamManagement extends TenantEngineLifecycleComponent implements IDeviceStreamManagement {

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with Mongo client */
    private IDeviceStreamManagementMongoClient mongoClient;

    public MongoDeviceStreamManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see
     * com.sitewhere.spi.device.streaming.IDeviceStreamManagement#createDeviceStream
     * (java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public IDeviceStream createDeviceStream(UUID assignmentId, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	IDeviceAssignment assignment = assertApiDeviceAssignment(assignmentId);
	DeviceStream stream = DeviceStreamPersistence.deviceStreamCreateLogic(assignment, request);

	MongoCollection<Document> streams = getMongoClient().getDeviceStreamsCollection();
	Document created = MongoDeviceStream.toDocument(stream);
	MongoPersistence.insert(streams, created, ErrorCode.DuplicateStreamId);
	return MongoDeviceStream.fromDocument(created);
    }

    /*
     * @see
     * com.sitewhere.spi.device.streaming.IDeviceStreamManagement#getDeviceStream(
     * java.util.UUID)
     */
    @Override
    public IDeviceStream getDeviceStream(UUID streamId) throws SiteWhereException {
	Document dbStream = getDeviceStreamDocument(streamId);
	if (dbStream == null) {
	    return null;
	}
	return MongoDeviceStream.fromDocument(dbStream);
    }

    /*
     * @see
     * com.sitewhere.spi.device.streaming.IDeviceStreamManagement#listDeviceStreams(
     * java.util.UUID, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(UUID assignmentId, ISearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> streams = getMongoClient().getDeviceStreamsCollection();
	Document query = new Document(MongoDeviceStream.PROP_ASSIGNMENT_ID, assignmentId);
	Document sort = new Document(MongoPersistentEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IDeviceStream.class, streams, query, sort, criteria, LOOKUP);
    }

    /**
     * Get the {@link Document} for an {@link IDeviceStream} based on stream id.
     * 
     * @param streamId
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStreamDocument(UUID streamId) throws SiteWhereException {
	try {
	    MongoCollection<Document> streams = getMongoClient().getDeviceStreamsCollection();
	    Document query = new Document(MongoPersistentEntity.PROP_ID, streamId);
	    return streams.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    protected IDeviceAssignment assertApiDeviceAssignment(UUID assignmentId) {
	return null;
    }

    public IDeviceStreamManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(IDeviceStreamManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}
