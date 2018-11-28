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
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.MongoDbClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with device stream managment object model.
 * 
 * @author Derek
 */
public class DeviceStreamManagementMongoClient extends MongoDbClient implements IDeviceStreamManagementMongoClient {

    /** Injected name used for stream data collection */
    private String deviceStreamsCollectionName = IDeviceStreamManagementMongoClient.DEFAULT_DEVICE_STREAM_COLLECTION_NAME;

    /** Injected name used for stream data collection */
    private String streamDataCollectionName = IDeviceStreamManagementMongoClient.DEFAULT_STREAM_DATA_COLLECTION_NAME;

    public DeviceStreamManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see
     * com.sitewhere.media.persistence.mongodb.IDeviceStreamManagementMongoClient#
     * getDeviceStreamsCollection()
     */
    @Override
    public MongoCollection<Document> getDeviceStreamsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceStreamsCollectionName());
    }

    /*
     * @see
     * com.sitewhere.media.persistence.mongodb.IDeviceStreamManagementMongoClient#
     * getStreamDataCollection()
     */
    @Override
    public MongoCollection<Document> getStreamDataCollection() throws SiteWhereException {
	return getDatabase().getCollection(getStreamDataCollectionName());
    }

    public String getDeviceStreamsCollectionName() {
	return deviceStreamsCollectionName;
    }

    public void setDeviceStreamsCollectionName(String deviceStreamsCollectionName) {
	this.deviceStreamsCollectionName = deviceStreamsCollectionName;
    }

    public String getStreamDataCollectionName() {
	return streamDataCollectionName;
    }

    public void setStreamDataCollectionName(String streamDataCollectionName) {
	this.streamDataCollectionName = streamDataCollectionName;
    }
}