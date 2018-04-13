/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.mongodb;

import java.util.UUID;

import org.bson.Document;

import com.mongodb.MongoClientException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sitewhere.batch.persistence.BatchManagementPersistence;
import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.batch.BatchElement;
import com.sitewhere.rest.model.batch.BatchOperation;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

public class MongoBatchManagement extends TenantEngineLifecycleComponent implements IBatchManagement {

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with global SiteWhere Mongo client */
    private IBatchManagementMongoClient mongoClient;

    public MongoBatchManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /**
     * Ensure that expected collection indexes exist.
     * 
     * @throws SiteWhereException
     */
    protected void ensureIndexes() throws SiteWhereException {
	// Batch operation indexes.
	getMongoClient().getBatchOperationsCollection().createIndex(new Document(MongoBatchOperation.PROP_TOKEN, 1),
		new IndexOptions().unique(true));
	getMongoClient().getBatchOperationElementsCollection().createIndex(
		new Document(MongoBatchElement.PROP_BATCH_OPERATION_ID, 1).append(MongoBatchElement.PROP_DEVICE_ID, 1),
		new IndexOptions().unique(true));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.batch.IBatchManagement#createBatchOperation(com.
     * sitewhere.spi.device.request.IBatchOperationCreateRequest)
     */
    @Override
    public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException {
	// Use common logic so all backend implementations work the same.
	BatchOperation batch = BatchManagementPersistence.batchOperationCreateLogic(request);

	MongoCollection<Document> batches = getMongoClient().getBatchOperationsCollection();
	Document created = MongoBatchOperation.toDocument(batch);
	MongoPersistence.insert(batches, created, ErrorCode.DuplicateBatchOperationToken);

	MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection();
	for (String deviceToken : request.getDeviceTokens()) {
	    IDevice device = getDeviceManagement().getDeviceByToken(deviceToken);
	    if (device != null) {
		BatchElement element = BatchManagementPersistence.batchElementCreateLogic(batch, device);
		Document dbElement = MongoBatchElement.toDocument(element);
		MongoPersistence.insert(elements, dbElement, ErrorCode.DuplicateBatchElement);
	    } else {
		getLogger().warn("Invalid device reference in batch operation create: " + deviceToken);
	    }
	}

	return MongoBatchOperation.fromDocument(created);
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#updateBatchOperation(java.util.UUID,
     * com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest)
     */
    @Override
    public IBatchOperation updateBatchOperation(UUID batchOperationId, IBatchOperationUpdateRequest request)
	    throws SiteWhereException {
	MongoCollection<Document> batchops = getMongoClient().getBatchOperationsCollection();
	Document match = assertBatchOperation(batchOperationId);

	BatchOperation operation = MongoBatchOperation.fromDocument(match);
	BatchManagementPersistence.batchOperationUpdateLogic(request, operation);

	Document updated = MongoBatchOperation.toDocument(operation);

	Document query = new Document(MongoBatchOperation.PROP_ID, batchOperationId);
	MongoPersistence.update(batchops, query, updated);
	return MongoBatchOperation.fromDocument(updated);
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#getBatchOperation(java.util.UUID)
     */
    @Override
    public IBatchOperation getBatchOperation(UUID batchOperationId) throws SiteWhereException {
	Document found = getBatchOperationDocument(batchOperationId);
	if (found != null) {
	    return MongoBatchOperation.fromDocument(found);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#getBatchOperationByToken(java.lang.
     * String)
     */
    @Override
    public IBatchOperation getBatchOperationByToken(String token) throws SiteWhereException {
	Document found = getBatchOperationDocumentByToken(token);
	if (found != null) {
	    return MongoBatchOperation.fromDocument(found);
	}
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#listBatchOperations(com.sitewhere.
     * spi.search.batch.IBatchOperationSearchCriteria)
     */
    @Override
    public ISearchResults<IBatchOperation> listBatchOperations(IBatchOperationSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection();
	Document dbCriteria = new Document();
	if (!criteria.isIncludeDeleted()) {
	    MongoSiteWhereEntity.setDeleted(dbCriteria, false);
	}
	Document sort = new Document(MongoSiteWhereEntity.PROP_CREATED_DATE, -1);
	return MongoPersistence.search(IBatchOperation.class, ops, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#deleteBatchOperation(java.util.UUID,
     * boolean)
     */
    @Override
    public IBatchOperation deleteBatchOperation(UUID batchOperationId, boolean force) throws SiteWhereException {
	Document existing = assertBatchOperation(batchOperationId);
	if (force) {
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection();
	    MongoPersistence.delete(ops, existing);

	    // Delete operation elements as well.
	    MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection();
	    Document match = new Document(MongoBatchElement.PROP_BATCH_OPERATION_ID, batchOperationId);
	    MongoPersistence.delete(elements, match);

	    return MongoBatchOperation.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoBatchOperation.PROP_ID, batchOperationId);
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection();
	    MongoPersistence.update(ops, query, existing);
	    return MongoBatchOperation.fromDocument(existing);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#listBatchElements(java.util.UUID,
     * com.sitewhere.spi.search.device.IBatchElementSearchCriteria)
     */
    @Override
    public SearchResults<IBatchElement> listBatchElements(UUID batchOperationId, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection();
	Document dbCriteria = new Document(MongoBatchElement.PROP_BATCH_OPERATION_ID, batchOperationId);
	if (criteria.getProcessingStatus() != null) {
	    dbCriteria.put(MongoBatchElement.PROP_PROCESSING_STATUS, criteria.getProcessingStatus().name());
	}
	Document sort = new Document();
	return MongoPersistence.search(IBatchElement.class, elements, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#updateBatchElement(java.util.UUID,
     * com.sitewhere.spi.batch.request.IBatchElementUpdateRequest)
     */
    @Override
    public IBatchElement updateBatchElement(UUID elementId, IBatchElementUpdateRequest request)
	    throws SiteWhereException {
	MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection();
	Document dbElement = assertBatchElement(elementId);

	BatchElement element = MongoBatchElement.fromDocument(dbElement);
	BatchManagementPersistence.batchElementUpdateLogic(request, element);

	Document updated = MongoBatchElement.toDocument(element);

	Document query = new Document(MongoBatchElement.PROP_ID, elementId);
	MongoPersistence.update(elements, query, updated);
	return MongoBatchElement.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#createBatchCommandInvocation(com
     * .sitewhere.spi.device.request.IBatchCommandInvocationRequest)
     */
    @Override
    public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
	    throws SiteWhereException {
	String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
	IBatchOperationCreateRequest generic = BatchManagementPersistence.batchCommandInvocationCreateLogic(request,
		uuid);
	return createBatchOperation(generic);
    }

    /**
     * Returns the {@link Document} for the batch operation with the given token.
     * Returns null if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getBatchOperationDocumentByToken(String token) throws SiteWhereException {
	try {
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection();
	    Document query = new Document(MongoBatchOperation.PROP_TOKEN, token);
	    return ops.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    /**
     * Return the {@link Document} for the batch operation with the given token.
     * Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertBatchOperationByToken(String token) throws SiteWhereException {
	Document match = getBatchOperationDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Returns the {@link Document} for the batch operation with the given unique
     * id. Returns null if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getBatchOperationDocument(UUID id) throws SiteWhereException {
	try {
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection();
	    Document query = new Document(MongoBatchOperation.PROP_ID, id);
	    return ops.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    /**
     * Return the {@link Document} for the batch operation with the given token.
     * Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertBatchOperation(UUID id) throws SiteWhereException {
	Document match = getBatchOperationDocument(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationId, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Returns the {@link Document} for the batch operation element with the given
     * unique id. Returns null if not found.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document getBatchOperationElementDocument(UUID id) throws SiteWhereException {
	try {
	    MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection();
	    Document query = new Document(MongoBatchElement.PROP_ID, id);
	    return elements.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    /**
     * Return the {@link Document} for the batch operation element with the given
     * token. Throws an exception if the token is not valid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected Document assertBatchElement(UUID elementId) throws SiteWhereException {
	Document match = getBatchOperationElementDocument(elementId);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchElementId, ErrorLevel.ERROR);
	}
	return match;
    }

    public IDeviceManagement getDeviceManagement() {
	return ((IBatchOperationsMicroservice) getTenantEngine().getMicroservice()).getDeviceManagementApiDemux()
		.getApiChannel();
    }

    public IBatchManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(IBatchManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}