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
import com.sitewhere.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.MongoDbClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with device event managment object model.
 */
public class DeviceEventManagementMongoClient extends MongoDbClient implements IDeviceEventManagementMongoClient {

    /** Injected name used for events collection */
    private String eventsCollectionName = IDeviceEventManagementMongoClient.DEFAULT_EVENTS_COLLECTION_NAME;

    public DeviceEventManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see
     * com.sitewhere.event.persistence.mongodb.IDeviceEventManagementMongoClient#
     * getEventsCollection()
     */
    public MongoCollection<Document> getEventsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getEventsCollectionName());
    }

    public String getEventsCollectionName() {
	return eventsCollectionName;
    }

    public void setEventsCollectionName(String eventsCollectionName) {
	this.eventsCollectionName = eventsCollectionName;
    }
}