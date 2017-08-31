package com.sitewhere.event.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client that provides device event management collections.
 * 
 * @author Derek
 */
public interface IDeviceEventManagementMongoClient {

    /** Default collection name for SiteWhere events */
    public static final String DEFAULT_EVENTS_COLLECTION_NAME = "events";

    /**
     * Collection for events.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getEventsCollection(ITenant tenant) throws SiteWhereException;
}
