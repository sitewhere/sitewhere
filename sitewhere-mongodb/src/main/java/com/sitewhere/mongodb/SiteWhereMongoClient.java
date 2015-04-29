/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.util.StringMessageUtils;
import org.springframework.beans.factory.InitializingBean;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Spring wrapper for initializing a Mongo client used by SiteWhere components.
 * 
 * @author dadams
 */
public class SiteWhereMongoClient extends LifecycleComponent implements InitializingBean,
		IUserManagementMongoClient, IDeviceManagementMongoClient {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereMongoClient.class);

	/** Default hostname for Mongo */
	private static final String DEFAULT_HOSTNAME = "localhost";

	/** Default port for Mongo */
	private static final int DEFAULT_PORT = 27017;

	/** Default database name */
	private static final String DEFAULT_DATABASE_NAME = "sitewhere";

	/** Mongo client */
	private MongoClient client;

	/** Hostname used to access the Mongo datastore */
	private String hostname = DEFAULT_HOSTNAME;

	/** Port used to access the Mongo datastore */
	private int port = DEFAULT_PORT;

	/** Database that holds sitewhere collections */
	private String databaseName = DEFAULT_DATABASE_NAME;

	/** Injected name used for device specifications collection */
	private String deviceSpecificationsCollectionName =
			IDeviceManagementMongoClient.DEFAULT_DEVICE_SPECIFICATIONS_COLLECTION_NAME;

	/** Injected name used for device commands collection */
	private String deviceCommandsCollectionName =
			IDeviceManagementMongoClient.DEFAULT_DEVICE_COMMANDS_COLLECTION_NAME;

	/** Injected name used for devices collection */
	private String devicesCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICES_COLLECTION_NAME;

	/** Injected name used for device assignments collection */
	private String deviceAssignmentsCollectionName =
			IDeviceManagementMongoClient.DEFAULT_DEVICE_ASSIGNMENTS_COLLECTION_NAME;

	/** Injected name used for sites collection */
	private String sitesCollectionName = IDeviceManagementMongoClient.DEFAULT_SITES_COLLECTION_NAME;

	/** Injected name used for zones collection */
	private String zonesCollectionName = IDeviceManagementMongoClient.DEFAULT_ZONES_COLLECTION_NAME;

	/** Injected name used for device groups collection */
	private String deviceGroupsCollectionName =
			IDeviceManagementMongoClient.DEFAULT_DEVICE_GROUPS_COLLECTION_NAME;

	/** Injected name used for group elements collection */
	private String groupElementsCollectionName =
			IDeviceManagementMongoClient.DEFAULT_DEVICE_GROUP_ELEMENTS_COLLECTION_NAME;

	/** Injected name used for events collection */
	private String eventsCollectionName = IDeviceManagementMongoClient.DEFAULT_EVENTS_COLLECTION_NAME;

	/** Injected name used for batch operations collection */
	private String batchOperationsCollectionName =
			IDeviceManagementMongoClient.DEFAULT_BATCH_OPERATIONS_COLLECTION_NAME;

	/** Injected name used for batch operation elements collection */
	private String batchOperationElementsCollectionName =
			IDeviceManagementMongoClient.DEFAULT_BATCH_OPERATION_ELEMENTS_COLLECTION_NAME;

	/** Injected name used for users collection */
	private String usersCollectionName = IUserManagementMongoClient.DEFAULT_USERS_COLLECTION_NAME;

	/** Injected name used for authorities collection */
	private String authoritiesCollectionName = IUserManagementMongoClient.DEFAULT_AUTHORITIES_COLLECTION_NAME;

	public SiteWhereMongoClient() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		SiteWhere.getServer().getRegisteredLifecycleComponents().add(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		try {
			MongoClientOptions.Builder builder =  new MongoClientOptions.Builder();
                        builder.maxConnectionIdleTime(60 * 60 * 1000); //1hour
                        this.client = new MongoClient(
                                new ServerAddress(getHostname(), getPort()),
                                builder.build()
                        );
			List<String> messages = new ArrayList<String>();
			messages.add("------------------");
			messages.add("-- MONGO CLIENT --");
			messages.add("------------------");
			messages.add("Hostname: " + hostname);
			messages.add("Port: " + port);
			messages.add("Database Name: " + databaseName);
			messages.add("");
			messages.add("-----------------------");
			messages.add("-- Device Management --");
			messages.add("-----------------------");
			messages.add("Device specifications collection name: " + getDeviceSpecificationsCollectionName());
			messages.add("Device commands collection name: " + getDeviceCommandsCollectionName());
			messages.add("Devices collection name: " + getDevicesCollectionName());
			messages.add("Device groups collection name: " + getDeviceGroupsCollectionName());
			messages.add("Group elements collection name: " + getGroupElementsCollectionName());
			messages.add("Device assignments collection name: " + getDeviceAssignmentsCollectionName());
			messages.add("Sites collection name: " + getSitesCollectionName());
			messages.add("Zones collection name: " + getZonesCollectionName());
			messages.add("Events collection name: " + getEventsCollectionName());
			messages.add("Batch operations collection name: " + getBatchOperationsCollectionName());
			messages.add("Batch operation elements collection name: "
					+ getBatchOperationElementsCollectionName());
			messages.add("");
			messages.add("---------------------");
			messages.add("-- User Management --");
			messages.add("---------------------");
			messages.add("Users collection name: " + getUsersCollectionName());
			messages.add("Authorities collection name: " + getAuthoritiesCollectionName());
			String message = StringMessageUtils.getBoilerPlate(messages, '*', 60);
			LOGGER.info("\n" + message + "\n");
		} catch (UnknownHostException e) {
			throw new SiteWhereException(e);
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		client.close();
		LOGGER.info("Mongo client shutdown completed.");
	}

	/**
	 * Get the MongoClient.
	 * 
	 * @return
	 */
	public MongoClient getMongoClient() {
		return client;
	}

	public DB getSiteWhereDatabase() {
		return client.getDB(getDatabaseName());
	}

	public DBCollection getDeviceSpecificationsCollection() {
		return getSiteWhereDatabase().getCollection(getDeviceSpecificationsCollectionName());
	}

	public DBCollection getDeviceCommandsCollection() {
		return getSiteWhereDatabase().getCollection(getDeviceCommandsCollectionName());
	}

	public DBCollection getDevicesCollection() {
		return getSiteWhereDatabase().getCollection(getDevicesCollectionName());
	}

	public DBCollection getDeviceAssignmentsCollection() {
		return getSiteWhereDatabase().getCollection(getDeviceAssignmentsCollectionName());
	}

	public DBCollection getSitesCollection() {
		return getSiteWhereDatabase().getCollection(getSitesCollectionName());
	}

	public DBCollection getZonesCollection() {
		return getSiteWhereDatabase().getCollection(getZonesCollectionName());
	}

	public DBCollection getDeviceGroupsCollection() {
		return getSiteWhereDatabase().getCollection(getDeviceGroupsCollectionName());
	}

	public DBCollection getGroupElementsCollection() {
		return getSiteWhereDatabase().getCollection(getGroupElementsCollectionName());
	}

	public DBCollection getEventsCollection() {
		return getSiteWhereDatabase().getCollection(getEventsCollectionName());
	}

	public DBCollection getBatchOperationsCollection() {
		return getSiteWhereDatabase().getCollection(getBatchOperationsCollectionName());
	}

	public DBCollection getBatchOperationElementsCollection() {
		return getSiteWhereDatabase().getCollection(getBatchOperationElementsCollectionName());
	}

	public DBCollection getUsersCollection() {
		return getSiteWhereDatabase().getCollection(getUsersCollectionName());
	}

	public DBCollection getAuthoritiesCollection() {
		return getSiteWhereDatabase().getCollection(getAuthoritiesCollectionName());
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
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

	public String getEventsCollectionName() {
		return eventsCollectionName;
	}

	public void setEventsCollectionName(String eventsCollectionName) {
		this.eventsCollectionName = eventsCollectionName;
	}

	public String getBatchOperationsCollectionName() {
		return batchOperationsCollectionName;
	}

	public void setBatchOperationsCollectionName(String batchOperationsCollectionName) {
		this.batchOperationsCollectionName = batchOperationsCollectionName;
	}

	public String getBatchOperationElementsCollectionName() {
		return batchOperationElementsCollectionName;
	}

	public void setBatchOperationElementsCollectionName(String batchOperationElementsCollectionName) {
		this.batchOperationElementsCollectionName = batchOperationElementsCollectionName;
	}

	public String getUsersCollectionName() {
		return usersCollectionName;
	}

	public void setUsersCollectionName(String usersCollectionName) {
		this.usersCollectionName = usersCollectionName;
	}

	public String getAuthoritiesCollectionName() {
		return authoritiesCollectionName;
	}

	public void setAuthoritiesCollectionName(String authoritiesCollectionName) {
		this.authoritiesCollectionName = authoritiesCollectionName;
	}
}
