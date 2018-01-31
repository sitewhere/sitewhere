/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.mongodb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.BaseMongoClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with device management object model.
 * 
 * @author Derek
 */
public class DeviceManagementMongoClient extends BaseMongoClient implements IDeviceManagementMongoClient {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected name used for device specifications collection */
    private String deviceSpecificationsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_SPECIFICATIONS_COLLECTION_NAME;

    /** Injected name used for device commands collection */
    private String deviceCommandsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_COMMANDS_COLLECTION_NAME;

    /** Injected name used for device statuses collection */
    private String deviceStatusesCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_STATUSES_COLLECTION_NAME;

    /** Injected name used for devices collection */
    private String devicesCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICES_COLLECTION_NAME;

    /** Injected name used for device assignments collection */
    private String deviceAssignmentsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_ASSIGNMENTS_COLLECTION_NAME;

    /** Injected name used for sites collection */
    private String sitesCollectionName = IDeviceManagementMongoClient.DEFAULT_SITES_COLLECTION_NAME;

    /** Injected name used for zones collection */
    private String zonesCollectionName = IDeviceManagementMongoClient.DEFAULT_ZONES_COLLECTION_NAME;

    /** Injected name used for device groups collection */
    private String deviceGroupsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_GROUPS_COLLECTION_NAME;

    /** Injected name used for group elements collection */
    private String groupElementsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_GROUP_ELEMENTS_COLLECTION_NAME;

    /** Injected name used for device streams collection */
    private String streamsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_STREAMS_COLLECTION_NAME;

    /** Injected name used for device stream data collection */
    private String streamDataCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_STREAM_DATA_COLLECTION_NAME;

    public DeviceManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceSpecificationsCollection()
     */
    public MongoCollection<Document> getDeviceSpecificationsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceSpecificationsCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceCommandsCollection()
     */
    public MongoCollection<Document> getDeviceCommandsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceCommandsCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceStatusesCollection()
     */
    @Override
    public MongoCollection<Document> getDeviceStatusesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceStatusesCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDevicesCollection()
     */
    public MongoCollection<Document> getDevicesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDevicesCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceAssignmentsCollection()
     */
    public MongoCollection<Document> getDeviceAssignmentsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceAssignmentsCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getSitesCollection()
     */
    public MongoCollection<Document> getSitesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getSitesCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getZonesCollection()
     */
    public MongoCollection<Document> getZonesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getZonesCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceGroupsCollection()
     */
    public MongoCollection<Document> getDeviceGroupsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceGroupsCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getGroupElementsCollection()
     */
    public MongoCollection<Document> getGroupElementsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getGroupElementsCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getStreamsCollection()
     */
    public MongoCollection<Document> getStreamsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getStreamsCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getStreamDataCollection()
     */
    public MongoCollection<Document> getStreamDataCollection() throws SiteWhereException {
	return getDatabase().getCollection(getStreamDataCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public String getDeviceSpecificationsCollectionName() {
	return deviceSpecificationsCollectionName;
    }

    public void setDeviceSpecificationsCollectionName(String deviceSpecificationsCollectionName) {
	this.deviceSpecificationsCollectionName = deviceSpecificationsCollectionName;
    }

    public String getDeviceCommandsCollectionName() {
	return deviceCommandsCollectionName;
    }

    public void setDeviceCommandsCollectionName(String deviceCommandsCollectionName) {
	this.deviceCommandsCollectionName = deviceCommandsCollectionName;
    }

    public String getDeviceStatusesCollectionName() {
	return deviceStatusesCollectionName;
    }

    public void setDeviceStatusesCollectionName(String deviceStatusesCollectionName) {
	this.deviceStatusesCollectionName = deviceStatusesCollectionName;
    }

    public String getDevicesCollectionName() {
	return devicesCollectionName;
    }

    public void setDevicesCollectionName(String devicesCollectionName) {
	this.devicesCollectionName = devicesCollectionName;
    }

    public String getDeviceGroupsCollectionName() {
	return deviceGroupsCollectionName;
    }

    public void setDeviceGroupsCollectionName(String deviceGroupsCollectionName) {
	this.deviceGroupsCollectionName = deviceGroupsCollectionName;
    }

    public String getGroupElementsCollectionName() {
	return groupElementsCollectionName;
    }

    public void setGroupElementsCollectionName(String groupElementsCollectionName) {
	this.groupElementsCollectionName = groupElementsCollectionName;
    }

    public String getDeviceAssignmentsCollectionName() {
	return deviceAssignmentsCollectionName;
    }

    public void setDeviceAssignmentsCollectionName(String deviceAssignmentsCollectionName) {
	this.deviceAssignmentsCollectionName = deviceAssignmentsCollectionName;
    }

    public String getSitesCollectionName() {
	return sitesCollectionName;
    }

    public void setSitesCollectionName(String sitesCollectionName) {
	this.sitesCollectionName = sitesCollectionName;
    }

    public String getZonesCollectionName() {
	return zonesCollectionName;
    }

    public void setZonesCollectionName(String zonesCollectionName) {
	this.zonesCollectionName = zonesCollectionName;
    }

    public String getStreamsCollectionName() {
	return streamsCollectionName;
    }

    public void setStreamsCollectionName(String streamsCollectionName) {
	this.streamsCollectionName = streamsCollectionName;
    }

    public String getStreamDataCollectionName() {
	return streamDataCollectionName;
    }

    public void setStreamDataCollectionName(String streamDataCollectionName) {
	this.streamDataCollectionName = streamDataCollectionName;
    }
}