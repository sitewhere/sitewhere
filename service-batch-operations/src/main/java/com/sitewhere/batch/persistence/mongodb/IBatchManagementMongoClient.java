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
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client that provides batch management collections.
 * 
 * @author Derek
 */
public interface IBatchManagementMongoClient {

    /** Default collection name for SiteWhere batch operations */
    public static final String DEFAULT_BATCH_OPERATIONS_COLLECTION_NAME = "batchoperations";

    /** Default collection name for SiteWhere batch operation elements */
    public static final String DEFAULT_BATCH_OPERATION_ELEMENTS_COLLECTION_NAME = "batchopelements";

    /**
     * Collection for batch operations.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getBatchOperationsCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for batch operation elements.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getBatchOperationElementsCollection(ITenant tenant) throws SiteWhereException;
}