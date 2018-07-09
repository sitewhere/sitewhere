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
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.MongoDbClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with device management object model.
 * 
 * @author Derek
 */
public class DeviceManagementMongoClient extends MongoDbClient implements IDeviceManagementMongoClient {

    /** Injected name used for customers collection */
    private String customersCollectionName = IDeviceManagementMongoClient.DEFAULT_CUSTOMERS_COLLECTION_NAME;

    /** Injected name used for customer types collection */
    private String customerTypesCollectionName = IDeviceManagementMongoClient.DEFAULT_CUSTOMER_TYPES_COLLECTION_NAME;

    /** Injected name used for areas collection */
    private String areasCollectionName = IDeviceManagementMongoClient.DEFAULT_AREAS_COLLECTION_NAME;

    /** Injected name used for area types collection */
    private String areaTypesCollectionName = IDeviceManagementMongoClient.DEFAULT_AREA_TYPES_COLLECTION_NAME;

    /** Injected name used for zones collection */
    private String zonesCollectionName = IDeviceManagementMongoClient.DEFAULT_ZONES_COLLECTION_NAME;

    /** Injected name used for device types collection */
    private String deviceTypesCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_TYPES_COLLECTION_NAME;

    /** Injected name used for device commands collection */
    private String deviceCommandsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_COMMANDS_COLLECTION_NAME;

    /** Injected name used for device statuses collection */
    private String deviceStatusesCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_STATUSES_COLLECTION_NAME;

    /** Injected name used for devices collection */
    private String devicesCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICES_COLLECTION_NAME;

    /** Injected name used for device assignments collection */
    private String deviceAssignmentsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_ASSIGNMENTS_COLLECTION_NAME;

    /** Injected name user for device alarms collection */
    private String deviceAlarmsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_ALARMS_COLLECTION_NAME;

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
     * getCustomersCollection()
     */
    @Override
    public MongoCollection<Document> getCustomersCollection() throws SiteWhereException {
	return getDatabase().getCollection(getCustomersCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getCustomerTypesCollection()
     */
    @Override
    public MongoCollection<Document> getCustomerTypesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getCustomerTypesCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getAreasCollection()
     */
    @Override
    public MongoCollection<Document> getAreasCollection() throws SiteWhereException {
	return getDatabase().getCollection(getAreasCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getAreaTypesCollection()
     */
    @Override
    public MongoCollection<Document> getAreaTypesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getAreaTypesCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceTypesCollection()
     */
    @Override
    public MongoCollection<Document> getDeviceTypesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceTypesCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceCommandsCollection()
     */
    @Override
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
    @Override
    public MongoCollection<Document> getDevicesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDevicesCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceAssignmentsCollection()
     */
    @Override
    public MongoCollection<Document> getDeviceAssignmentsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceAssignmentsCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceAlarmsCollection()
     */
    @Override
    public MongoCollection<Document> getDeviceAlarmsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getDeviceAlarmsCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getZonesCollection()
     */
    @Override
    public MongoCollection<Document> getZonesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getZonesCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getDeviceGroupsCollection()
     */
    @Override
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
    @Override
    public MongoCollection<Document> getStreamsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getStreamsCollectionName());
    }

    /*
     * @see com.sitewhere.device.persistence.mongodb.IDeviceManagementMongoClient#
     * getStreamDataCollection()
     */
    @Override
    public MongoCollection<Document> getStreamDataCollection() throws SiteWhereException {
	return getDatabase().getCollection(getStreamDataCollectionName());
    }

    public String getCustomersCollectionName() {
	return customersCollectionName;
    }

    public void setCustomersCollectionName(String customersCollectionName) {
	this.customersCollectionName = customersCollectionName;
    }

    public String getCustomerTypesCollectionName() {
	return customerTypesCollectionName;
    }

    public void setCustomerTypesCollectionName(String customerTypesCollectionName) {
	this.customerTypesCollectionName = customerTypesCollectionName;
    }

    public String getAreasCollectionName() {
	return areasCollectionName;
    }

    public void setAreasCollectionName(String areasCollectionName) {
	this.areasCollectionName = areasCollectionName;
    }

    public String getAreaTypesCollectionName() {
	return areaTypesCollectionName;
    }

    public void setAreaTypesCollectionName(String areaTypesCollectionName) {
	this.areaTypesCollectionName = areaTypesCollectionName;
    }

    public String getZonesCollectionName() {
	return zonesCollectionName;
    }

    public void setZonesCollectionName(String zonesCollectionName) {
	this.zonesCollectionName = zonesCollectionName;
    }

    public String getDeviceTypesCollectionName() {
	return deviceTypesCollectionName;
    }

    public void setDeviceTypesCollectionName(String deviceTypesCollectionName) {
	this.deviceTypesCollectionName = deviceTypesCollectionName;
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

    public String getDeviceAlarmsCollectionName() {
	return deviceAlarmsCollectionName;
    }

    public void setDeviceAlarmsCollectionName(String deviceAlarmsCollectionName) {
	this.deviceAlarmsCollectionName = deviceAlarmsCollectionName;
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