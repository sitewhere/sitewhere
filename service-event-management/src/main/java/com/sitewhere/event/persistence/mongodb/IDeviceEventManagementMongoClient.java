/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.spi.SiteWhereException;

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
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getEventsCollection() throws SiteWhereException;
}
