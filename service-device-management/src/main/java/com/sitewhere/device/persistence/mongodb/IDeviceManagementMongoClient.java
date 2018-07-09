/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client that provides device management collections.
 * 
 * @author Derek
 */
public interface IDeviceManagementMongoClient {

    /** Default collection name for SiteWhere customers */
    public static final String DEFAULT_CUSTOMERS_COLLECTION_NAME = "customers";

    /** Default collection name for SiteWhere customer types */
    public static final String DEFAULT_CUSTOMER_TYPES_COLLECTION_NAME = "customertypes";

    /** Default collection name for SiteWhere areas */
    public static final String DEFAULT_AREAS_COLLECTION_NAME = "areas";

    /** Default collection name for SiteWhere area types */
    public static final String DEFAULT_AREA_TYPES_COLLECTION_NAME = "areatypes";

    /** Default collection name for SiteWhere zones */
    public static final String DEFAULT_ZONES_COLLECTION_NAME = "zones";

    /** Default collection name for SiteWhere device types */
    public static final String DEFAULT_DEVICE_TYPES_COLLECTION_NAME = "devicetypes";

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

    /** Default collection name for SiteWhere device alarms */
    public static final String DEFAULT_DEVICE_ALARMS_COLLECTION_NAME = "devicealarms";

    /** Default collection name for SiteWhere device streams */
    public static final String DEFAULT_DEVICE_STREAMS_COLLECTION_NAME = "streams";

    /** Default collection name for SiteWhere device streams data */
    public static final String DEFAULT_DEVICE_STREAM_DATA_COLLECTION_NAME = "streamdata";

    /**
     * Collection for customers.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getCustomersCollection() throws SiteWhereException;

    /**
     * Collection for customer types.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getCustomerTypesCollection() throws SiteWhereException;

    /**
     * Collection for areas.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getAreasCollection() throws SiteWhereException;

    /**
     * Collection for area types.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getAreaTypesCollection() throws SiteWhereException;

    /**
     * Collection for zones.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getZonesCollection() throws SiteWhereException;

    /**
     * Collection for device types.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceTypesCollection() throws SiteWhereException;

    /**
     * Collection for device commands.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceCommandsCollection() throws SiteWhereException;

    /**
     * Collection for device statuses.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceStatusesCollection() throws SiteWhereException;

    /**
     * Collection for devices.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDevicesCollection() throws SiteWhereException;

    /**
     * Collection for device assignments.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceAssignmentsCollection() throws SiteWhereException;

    /**
     * Collection for device alarms.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceAlarmsCollection() throws SiteWhereException;

    /**
     * Collection for device groups.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getDeviceGroupsCollection() throws SiteWhereException;

    /**
     * Collection for device group elements.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getGroupElementsCollection() throws SiteWhereException;

    /**
     * Collection for streams.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getStreamsCollection() throws SiteWhereException;

    /**
     * Collection for stream data.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getStreamDataCollection() throws SiteWhereException;
}