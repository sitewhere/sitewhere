/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.mongodb;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sitewhere.batch.persistence.BatchManagementPersistence;
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
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

public class MongoBatchManagement extends TenantEngineLifecycleComponent implements IBatchManagement {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(MongoBatchManagement.class);

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
	getMongoClient().getBatchOperationElementsCollection()
		.createIndex(new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, 1));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.batch.IBatchManagement#createBatchOperation(com.
     * sitewhere.spi.device.request.IBatchOperationCreateRequest)
     */
    @Override
    public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException {
	String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
	BatchOperation batch = BatchManagementPersistence.batchOperationCreateLogic(request, uuid);

	MongoCollection<Document> batches = getMongoClient().getBatchOperationsCollection();
	Document created = MongoBatchOperation.toDocument(batch);
	MongoPersistence.insert(batches, created, ErrorCode.DuplicateBatchOperationToken);

	// Insert element for each hardware id.
	long index = 0;
	MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection();
	for (String hardwareId : request.getHardwareIds()) {
	    BatchElement element = BatchManagementPersistence.batchElementCreateLogic(batch.getToken(), hardwareId,
		    ++index);
	    Document dbElement = MongoBatchElement.toDocument(element);
	    MongoPersistence.insert(elements, dbElement, ErrorCode.DuplicateBatchElement);
	}

	return MongoBatchOperation.fromDocument(created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.batch.IBatchManagement#updateBatchOperation(java.lang.
     * String, com.sitewhere.spi.device.request.IBatchOperationUpdateRequest)
     */
    @Override
    public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
	    throws SiteWhereException {
	MongoCollection<Document> batchops = getMongoClient().getBatchOperationsCollection();
	Document match = assertBatchOperation(token);

	BatchOperation operation = MongoBatchOperation.fromDocument(match);
	BatchManagementPersistence.batchOperationUpdateLogic(request, operation);

	Document updated = MongoBatchOperation.toDocument(operation);

	Document query = new Document(MongoBatchOperation.PROP_TOKEN, token);
	MongoPersistence.update(batchops, query, updated);
	return MongoBatchOperation.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.batch.IBatchManagement#getBatchOperation(java.lang.
     * String)
     */
    @Override
    public IBatchOperation getBatchOperation(String token) throws SiteWhereException {
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.batch.IBatchManagement#deleteBatchOperation(java.lang.
     * String, boolean)
     */
    @Override
    public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException {
	Document existing = assertBatchOperation(token);
	if (force) {
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection();
	    MongoPersistence.delete(ops, existing);

	    // Delete operation elements as well.
	    MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection();
	    Document match = new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, token);
	    MongoPersistence.delete(elements, match);

	    return MongoBatchOperation.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoBatchOperation.PROP_TOKEN, token);
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationsCollection();
	    MongoPersistence.update(ops, query, existing);
	    return MongoBatchOperation.fromDocument(existing);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.batch.IBatchManagement#listBatchElements(java.lang.
     * String, com.sitewhere.spi.search.device.IBatchElementSearchCriteria)
     */
    @Override
    public SearchResults<IBatchElement> listBatchElements(String batchToken, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException {
	MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection();
	Document dbCriteria = new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, batchToken);
	if (criteria.getProcessingStatus() != null) {
	    dbCriteria.put(MongoBatchElement.PROP_PROCESSING_STATUS, criteria.getProcessingStatus());
	}
	Document sort = new Document(MongoBatchElement.PROP_INDEX, 1);
	return MongoPersistence.search(IBatchElement.class, elements, dbCriteria, sort, criteria, LOOKUP);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.batch.IBatchManagement#updateBatchElement(java.lang.
     * String, long, com.sitewhere.spi.device.request.IBatchElementUpdateRequest)
     */
    @Override
    public IBatchElement updateBatchElement(String operationToken, long index, IBatchElementUpdateRequest request)
	    throws SiteWhereException {
	MongoCollection<Document> elements = getMongoClient().getBatchOperationElementsCollection();
	Document dbElement = assertBatchElement(operationToken, index);

	BatchElement element = MongoBatchElement.fromDocument(dbElement);
	BatchManagementPersistence.batchElementUpdateLogic(request, element);

	Document updated = MongoBatchElement.toDocument(element);

	Document query = new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, operationToken)
		.append(MongoBatchElement.PROP_INDEX, index);
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
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
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
    protected Document assertBatchOperation(String token) throws SiteWhereException {
	Document match = getBatchOperationDocumentByToken(token);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchOperationToken, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Return the {@link Document} for the batch operation element based on the
     * token for its parent operation and its index.
     * 
     * @param operationToken
     * @param index
     * @return
     * @throws SiteWhereException
     */
    protected Document getBatchElementDocumentByIndex(String operationToken, long index) throws SiteWhereException {
	try {
	    MongoCollection<Document> ops = getMongoClient().getBatchOperationElementsCollection();
	    Document query = new Document(MongoBatchElement.PROP_BATCH_OPERATION_TOKEN, operationToken)
		    .append(MongoBatchElement.PROP_INDEX, index);
	    return ops.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    protected Document assertBatchElement(String operationToken, long index) throws SiteWhereException {
	Document match = getBatchElementDocumentByIndex(operationToken, index);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidBatchElement, ErrorLevel.ERROR);
	}
	return match;
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

    public IBatchManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(IBatchManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}