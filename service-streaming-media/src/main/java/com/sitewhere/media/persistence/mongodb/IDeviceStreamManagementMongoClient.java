/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client that provides device event management collections.
 * 
 * @author Derek
 */
public interface IDeviceStreamManagementMongoClient {

    /** Default collection name for SiteWhere events */
    public static final String DEFAULT_STREAM_DATA_COLLECTION_NAME = "streamdata";

    /**
     * Collection for stream data.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getStreamDataCollection() throws SiteWhereException;
}
