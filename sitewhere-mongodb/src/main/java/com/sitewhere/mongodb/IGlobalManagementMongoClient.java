/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client that provides user management collections.
 * 
 * @author Derek
 */
public interface IGlobalManagementMongoClient {

    /** Default collection name for SiteWhere users */
    public static final String DEFAULT_USERS_COLLECTION_NAME = "users";

    /** Default collection name for SiteWhere granted authorities */
    public static final String DEFAULT_AUTHORITIES_COLLECTION_NAME = "authorities";

    /** Default collection name for SiteWhere tenants */
    public static final String DEFAULT_TENANTS_COLLECTION_NAME = "tenants";

    /** Default collection name for SiteWhere tenant groups */
    public static final String DEFAULT_TENANT_GROUPS_COLLECTION_NAME = "tgroups";

    /** Default collection name for SiteWhere tenant group elements */
    public static final String DEFAULT_TENANT_GROUP_ELEMENTS_COLLECTION_NAME = "tgroupelements";

    /**
     * Get the collection for users.
     * 
     * @return
     */
    public MongoCollection<Document> getUsersCollection();

    /**
     * Get the collection for authorities.
     * 
     * @return
     */
    public MongoCollection<Document> getAuthoritiesCollection();

    /**
     * Get the collection for tenants.
     * 
     * @return
     */
    public MongoCollection<Document> getTenantsCollection();

    /**
     * Deletes all tenant data. (non recoverable)
     * 
     * @param tenantId
     * @throws SiteWhereException
     */
    public void deleteTenantData(String tenantId) throws SiteWhereException;
}