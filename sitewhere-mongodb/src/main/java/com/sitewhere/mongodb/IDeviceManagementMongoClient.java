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
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client that provides device management collections.
 * 
 * @author Derek
 */
public interface IDeviceManagementMongoClient {

    /** Default collection name for SiteWhere sites */
    public static final String DEFAULT_SITES_COLLECTION_NAME = "sites";

    /** Default collection name for SiteWhere zones */
    public static final String DEFAULT_ZONES_COLLECTION_NAME = "zones";

    /** Default collection name for SiteWhere device specifications */
    public static final String DEFAULT_DEVICE_SPECIFICATIONS_COLLECTION_NAME = "specifications";

    /** Default collection name for SiteWhere device commands */
    public static final String DEFAULT_DEVICE_COMMANDS_COLLECTION_NAME = "commands";

    /** Default collection name for SiteWhere device statuses */
    public static final String DEFAULT_DEVICE_STATUSES_COLLECTION_NAME = "statuses";

    /** Default collection name for SiteWhere devices */
    public static final String DEFAULT_DEVICES_COLLECTION_NAME = "devices";

    /** Default collection name for SiteWhere device groups */
    public static final String DEFAULT_DEVICE_GROUPS_COLLECTION_NAME = "devicegroups";

    /** Default collection name for SiteWhere device group elements */
    public static final String DEFAULT_DEVICE_GROUP_ELEMENTS_COLLECTION_NAME = "groupelements";

    /** Default collection name for SiteWhere device assignments */
    public static final String DEFAULT_DEVICE_ASSIGNMENTS_COLLECTION_NAME = "assignments";

    /** Default collection name for SiteWhere events */
    public static final String DEFAULT_EVENTS_COLLECTION_NAME = "events";

    /** Default collection name for SiteWhere device streams */
    public static final String DEFAULT_DEVICE_STREAMS_COLLECTION_NAME = "streams";

    /** Default collection name for SiteWhere device streams data */
    public static final String DEFAULT_DEVICE_STREAM_DATA_COLLECTION_NAME = "streamdata";

    /** Default collection name for SiteWhere batch operations */
    public static final String DEFAULT_BATCH_OPERATIONS_COLLECTION_NAME = "batchoperations";

    /** Default collection name for SiteWhere batch operation elements */
    public static final String DEFAULT_BATCH_OPERATION_ELEMENTS_COLLECTION_NAME = "batchopelements";

    /**
     * Collection for device specifications.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceSpecificationsCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for device commands.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceCommandsCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for device statuses.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceStatusesCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for devices.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDevicesCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for device assignments.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceAssignmentsCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for sites.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getSitesCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for zones.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getZonesCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for device groups.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceGroupsCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for device group elements.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getGroupElementsCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for events.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getEventsCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for streams.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getStreamsCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for stream data.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getStreamDataCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for batch operations.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getBatchOperationsCollection(ITenant tenant) throws SiteWhereException;

    /**
     * Collection for batch operation elements.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getBatchOperationElementsCollection(ITenant tenant) throws SiteWhereException;
}