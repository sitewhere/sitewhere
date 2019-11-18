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
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client that provides device state management collections.
 */
public interface IDeviceStateManagementMongoClient {

    /** Default collection name for device state */
    public static final String DEFAULT_DEVICE_STATES_COLLECTION_NAME = "devicestates";

    /**
     * Collection for device states.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceStatesCollection() throws SiteWhereException;
}
