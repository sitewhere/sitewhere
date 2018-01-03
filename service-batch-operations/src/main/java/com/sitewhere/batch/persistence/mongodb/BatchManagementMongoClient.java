/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.mongodb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.BaseMongoClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client for interacting with batch management object model.
 * 
 * @author Derek
 */
public class BatchManagementMongoClient extends BaseMongoClient implements IBatchManagementMongoClient {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected name used for batch operations collection */
    private String batchOperationsCollectionName = IBatchManagementMongoClient.DEFAULT_BATCH_OPERATIONS_COLLECTION_NAME;

    /** Injected name used for batch operation elements collection */
    private String batchOperationElementsCollectionName = IBatchManagementMongoClient.DEFAULT_BATCH_OPERATION_ELEMENTS_COLLECTION_NAME;

    public BatchManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getBatchOperationsCollection (com.sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getBatchOperationsCollection(ITenant tenant) throws SiteWhereException {
	return getTenantDatabase(tenant).getCollection(getBatchOperationsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getBatchOperationElementsCollection (com.sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getBatchOperationElementsCollection(ITenant tenant) throws SiteWhereException {
	return getTenantDatabase(tenant).getCollection(getBatchOperationElementsCollectionName());
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