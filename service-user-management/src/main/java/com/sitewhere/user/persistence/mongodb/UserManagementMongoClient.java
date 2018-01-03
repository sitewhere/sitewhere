/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.persistence.mongodb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.BaseMongoClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with user management object model.
 * 
 * @author Derek
 */
public class UserManagementMongoClient extends BaseMongoClient implements IUserManagementMongoClient {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected name used for users collection */
    private String usersCollectionName = IUserManagementMongoClient.DEFAULT_USERS_COLLECTION_NAME;

    /** Injected name used for authorities collection */
    private String authoritiesCollectionName = IUserManagementMongoClient.DEFAULT_AUTHORITIES_COLLECTION_NAME;

    public UserManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IGlobalManagementMongoClient#getUsersCollection()
     */
    @Override
    public MongoCollection<Document> getUsersCollection() throws SiteWhereException {
	return getGlobalDatabase().getCollection(getUsersCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IGlobalManagementMongoClient#
     * getAuthoritiesCollection()
     */
    @Override
    public MongoCollection<Document> getAuthoritiesCollection() throws SiteWhereException {
	return getGlobalDatabase().getCollection(getAuthoritiesCollectionName());
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

    public String getUsersCollectionName() {
	return usersCollectionName;
    }

    public void setUsersCollectionName(String usersCollectionName) {
	this.usersCollectionName = usersCollectionName;
    }

    public String getAuthoritiesCollectionName() {
	return authoritiesCollectionName;
    }

    public void setAuthoritiesCollectionName(String authoritiesCollectionName) {
	this.authoritiesCollectionName = authoritiesCollectionName;
    }
}