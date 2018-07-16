/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.persistence.mongodb;

import java.util.UUID;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sitewhere.devicestate.microservice.DeviceStateMicroservice;
import com.sitewhere.devicestate.persistence.DeviceStatePersistence;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.IDeviceStateManagement;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IDeviceStateSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Device state management implementation that uses MongoDB for persistence.
 * 
 * @author Derek
 */
public class MongoDeviceStateManagement extends TenantEngineLifecycleComponent implements IDeviceStateManagement {

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** MongoDB client */
    private IDeviceStateManagementMongoClient mongoClient;

    public MongoDeviceStateManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Ensure that collection indexes exist.
	ensureIndexes();
    }

    /**
     * Ensure that expected collection indexes exist.
     * 
     * @throws SiteWhereException
     */
    protected void ensureIndexes() throws SiteWhereException {
	getMongoClient().getDeviceStatesCollection().createIndex(
		new BasicDBObject(MongoDeviceState.PROP_DEVICE_ASSIGNMENT_ID, 1), new IndexOptions().unique(true));
	getMongoClient().getDeviceStatesCollection()
		.createIndex(new Document(MongoDeviceState.PROP_LAST_INTERACTION_DATE, 1));
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#createDeviceState(com.
     * sitewhere.spi.device.state.request.IDeviceStateCreateRequest)
     */
    @Override
    public IDeviceState createDeviceState(IDeviceStateCreateRequest request) throws SiteWhereException {
	// Look up device.
	IDevice device = null;
	if (request.getDeviceId() != null) {
	    device = getDeviceManagement().getDevice(request.getDeviceId());
	}
	if (device == null) {
	    getLogger().warn("Device state references invalid device id: " + request.getDeviceId());
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceId, ErrorLevel.ERROR);
	}

	// Look up device assignment.
	IDeviceAssignment deviceAssignment = null;
	if (request.getDeviceAssignmentId() != null) {
	    deviceAssignment = getDeviceManagement().getDeviceAssignment(request.getDeviceAssignmentId());
	}
	if (deviceAssignment == null) {
	    getLogger()
		    .warn("Device state references invalid device assignment id: " + request.getDeviceAssignmentId());
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentId, ErrorLevel.ERROR);
	}

	// Use common logic so all backend implementations work the same.
	DeviceState state = DeviceStatePersistence.deviceStateCreateLogic(request, device, deviceAssignment);

	MongoCollection<Document> states = getMongoClient().getDeviceStatesCollection();
	Document created = MongoDeviceState.toDocument(state);
	MongoPersistence.insert(states, created, ErrorCode.DuplicateId);

	return MongoDeviceState.fromDocument(created);
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#getDeviceState(java.
     * util.UUID)
     */
    @Override
    public IDeviceState getDeviceState(UUID id) throws SiteWhereException {
	Document dbType = getDeviceStateDocumentById(id);
	if (dbType != null) {
	    return MongoDeviceState.fromDocument(dbType);
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceStateManagement#
     * getDeviceStateByDeviceAssignmentId(java.util.UUID)
     */
    @Override
    public IDeviceState getDeviceStateByDeviceAssignmentId(UUID assignmentId) throws SiteWhereException {
	Document dbType = getDeviceStateDocumentByDeviceAssignmentId(assignmentId);
	if (dbType != null) {
	    return MongoDeviceState.fromDocument(dbType);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#updateDeviceState(java.
     * util.UUID, com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest)
     */
    @Override
    public IDeviceState updateDeviceState(UUID id, IDeviceStateCreateRequest request) throws SiteWhereException {
	Document match = assertDeviceState(id);
	DeviceState deviceState = MongoDeviceState.fromDocument(match);

	// Use common update logic.
	DeviceStatePersistence.deviceStateUpdateLogic(request, deviceState);
	Document updated = MongoDeviceState.toDocument(deviceState);

	Document query = new Document(MongoDeviceState.PROP_ID, id);
	MongoCollection<Document> states = getMongoClient().getDeviceStatesCollection();
	MongoPersistence.update(states, query, updated);

	return MongoDeviceState.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#searchDeviceStates(com.
     * sitewhere.spi.search.device.IDeviceStateSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceState> searchDeviceStates(IDeviceStateSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> states = getMongoClient().getDeviceStatesCollection();
	Document query = new Document();
	if (criteria.getLastInteractionDateBefore() != null) {
	    Document dateClause = new Document();
	    dateClause.append("$lte", criteria.getLastInteractionDateBefore());
	    query.put(MongoDeviceState.PROP_LAST_INTERACTION_DATE, dateClause);
	}
	if ((criteria.getDeviceTypeIds() != null) && (criteria.getDeviceTypeIds().size() > 0)) {
	    query.append(MongoDeviceState.PROP_DEVICE_TYPE_ID, new Document("$in", criteria.getDeviceTypeIds()));
	}
	if ((criteria.getCustomerIds() != null) && (criteria.getCustomerIds().size() > 0)) {
	    query.append(MongoDeviceState.PROP_CUSTOMER_ID, new Document("$in", criteria.getCustomerIds()));
	}
	if ((criteria.getAreaIds() != null) && (criteria.getAreaIds().size() > 0)) {
	    query.append(MongoDeviceState.PROP_AREA_ID, new Document("$in", criteria.getAreaIds()));
	}
	if ((criteria.getAssetIds() != null) && (criteria.getAssetIds().size() > 0)) {
	    query.append(MongoDeviceState.PROP_ASSET_ID, new Document("$in", criteria.getAssetIds()));
	}
	Document sort = new Document(MongoDeviceState.PROP_LAST_INTERACTION_DATE, 1);
	return MongoPersistence.search(IDeviceState.class, states, query, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.device.state.IDeviceStateManagement#deleteDeviceState(java.
     * util.UUID)
     */
    @Override
    public IDeviceState deleteDeviceState(UUID id) throws SiteWhereException {
	Document existing = assertDeviceState(id);

	MongoCollection<Document> states = getMongoClient().getDeviceStatesCollection();
	MongoPersistence.delete(states, existing);
	return MongoDeviceState.fromDocument(existing);
    }

    public IDeviceStateManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(IDeviceStateManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }

    /**
     * Return the {@link Document} for the device state with the given id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStateDocumentById(UUID id) throws SiteWhereException {
	MongoCollection<Document> states = getMongoClient().getDeviceStatesCollection();
	Document query = new Document(MongoDeviceState.PROP_ID, id);
	return states.find(query).first();
    }

    /**
     * Return the {@link Document} for the device state with the given device
     * assignment id.
     * 
     * @param assignmentId
     * @return
     * @throws SiteWhereException
     */
    protected Document getDeviceStateDocumentByDeviceAssignmentId(UUID assignmentId) throws SiteWhereException {
	MongoCollection<Document> states = getMongoClient().getDeviceStatesCollection();
	Document query = new Document(MongoDeviceState.PROP_DEVICE_ASSIGNMENT_ID, assignmentId);
	return states.find(query).first();
    }

    /**
     * Return the {@link Document} for the device state with the given id. Throws an
     * exception if the id is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertDeviceState(UUID id) throws SiteWhereException {
	Document match = getDeviceStateDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidDeviceStateId, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Get device management implementation from microservice.
     * 
     * @return
     */
    public IDeviceManagement getDeviceManagement() {
	return ((DeviceStateMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }
}