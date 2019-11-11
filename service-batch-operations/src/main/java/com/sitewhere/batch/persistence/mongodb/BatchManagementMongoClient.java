/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.MongoDbClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with batch management object model.
 */
public class BatchManagementMongoClient extends MongoDbClient implements IBatchManagementMongoClient {

    /** Injected name used for batch operations collection */
    private String batchOperationsCollectionName = IBatchManagementMongoClient.DEFAULT_BATCH_OPERATIONS_COLLECTION_NAME;

    /** Injected name used for batch operation elements collection */
    private String batchOperationElementsCollectionName = IBatchManagementMongoClient.DEFAULT_BATCH_OPERATION_ELEMENTS_COLLECTION_NAME;

    public BatchManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.sitewhere.batch.persistence.mongodb.IBatchManagementMongoClient#
     * getBatchOperationsCollection()
     */
    public MongoCollection<Document> getBatchOperationsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getBatchOperationsCollectionName());
    }

    /*
     * @see com.sitewhere.batch.persistence.mongodb.IBatchManagementMongoClient#
     * getBatchOperationElementsCollection()
     */
    public MongoCollection<Document> getBatchOperationElementsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getBatchOperationElementsCollectionName());
    }

    public String getBatchOperationsCollectionName() {
	return batchOperationsCollectionName;
    }

    public void setBatchOperationsCollectionName(String batchOperationsCollectionName) {
	this.batchOperationsCollectionName = batchOperationsCollectionName;
    }

    public String getBatchOperationElementsCollectionName() {
	return batchOperationElementsCollectionName;
    }

    public void setBatchOperationElementsCollectionName(String batchOperationElementsCollectionName) {
	this.batchOperationElementsCollectionName = batchOperationElementsCollectionName;
    }
}