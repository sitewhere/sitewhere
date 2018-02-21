/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.persistence.mongodb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.MongoDbClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with tenant management object model.
 * 
 * @author Derek
 */
public class TenantManagementMongoClient extends MongoDbClient implements ITenantManagementMongoClient {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(TenantManagementMongoClient.class);

    /** Injected name used for tenants collection */
    private String tenantsCollectionName = ITenantManagementMongoClient.DEFAULT_TENANTS_COLLECTION_NAME;

    public TenantManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IGlobalManagementMongoClient#getTenantsCollection()
     */
    @Override
    public MongoCollection<Document> getTenantsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getTenantsCollectionName());
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

    public String getTenantsCollectionName() {
	return tenantsCollectionName;
    }

    public void setTenantsCollectionName(String tenantsCollectionName) {
	this.tenantsCollectionName = tenantsCollectionName;
    }
}