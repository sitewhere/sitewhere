/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.MongoDbClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with device state object model.
 */
public class DeviceStateManagementMongoClient extends MongoDbClient implements IDeviceStateManagementMongoClient {

    /** Injected name used for device states collection */
    private String deviceStatesCollectionName = IDeviceStateManagementMongoClient.DEFAULT_DEVICE_STATES_COLLECTION_NAME;

    public DeviceStateManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.sitewhere.devicestate.persistence.mongodb.
     * IDeviceStateManagementMongoClient#getDeviceStatesCollection()
     */
    @Override
    public MongoCollection<Document> getDeviceStatesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceStatesCollectionName());
    }

    public String getDeviceStatesCollectionName() {
	return deviceStatesCollectionName;
    }

    public void setDeviceStatesCollectionName(String deviceStatesCollectionName) {
	this.deviceStatesCollectionName = deviceStatesCollectionName;
    }
}