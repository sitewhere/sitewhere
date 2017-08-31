package com.sitewhere.user.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

/**
 * Mongo client that provides user management collections.
 * 
 * @author Derek
 */
public interface IUserManagementMongoClient {

    /** Default collection name for SiteWhere users */
    public static final String DEFAULT_USERS_COLLECTION_NAME = "users";

    /** Default collection name for SiteWhere granted authorities */
    public static final String DEFAULT_AUTHORITIES_COLLECTION_NAME = "authorities";

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
}