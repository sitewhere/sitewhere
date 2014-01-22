/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.util.StringMessageUtils;
import org.springframework.beans.factory.InitializingBean;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * Spring wrapper for initializing a Mongo client used by SiteWhere components.
 * 
 * @author dadams
 */
public class SiteWhereMongoClient implements InitializingBean {

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
			IMongoCollectionNames.DEFAULT_DEVICE_SPECIFICATIONS_COLLECTION_NAME;

	/** Injected name used for device commands collection */
	private String deviceCommandsCollectionName =
			IMongoCollectionNames.DEFAULT_DEVICE_COMMANDS_COLLECTION_NAME;

	/** Injected name used for devices collection */
	private String devicesCollectionName = IMongoCollectionNames.DEFAULT_DEVICES_COLLECTION_NAME;

	/** Injected name used for device assignments collection */
	private String deviceAssignmentsCollectionName =
			IMongoCollectionNames.DEFAULT_DEVICE_ASSIGNMENTS_COLLECTION_NAME;

	/** Injected name used for sites collection */
	private String sitesCollectionName = IMongoCollectionNames.DEFAULT_SITES_COLLECTION_NAME;

	/** Injected name used for zones collection */
	private String zonesCollectionName = IMongoCollectionNames.DEFAULT_ZONES_COLLECTION_NAME;

	/** Injected name used for measurements collection */
	private String measurementsCollectionName = IMongoCollectionNames.DEFAULT_MEASUREMENTS_COLLECTION_NAME;

	/** Injected name used for locations collection */
	private String locationsCollectionName = IMongoCollectionNames.DEFAULT_LOCATIONS_COLLECTION_NAME;

	/** Injected name used for alerts collection */
	private String alertsCollectionName = IMongoCollectionNames.DEFAULT_ALERTS_COLLECTION_NAME;

	/** Injected name used for command invocations collection */
	private String invocationsCollectionName = IMongoCollectionNames.DEFAULT_INVOCATIONS_COLLECTION_NAME;

	/** Injected name used for users collection */
	private String usersCollectionName = IMongoCollectionNames.DEFAULT_USERS_COLLECTION_NAME;

	/** Injected name used for authorities collection */
	private String authoritiesCollectionName = IMongoCollectionNames.DEFAULT_AUTHORITIES_COLLECTION_NAME;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		this.client = new MongoClient(getHostname(), getPort());
		List<String> messages = new ArrayList<String>();
		messages.add("Mongo client initialized. Version: " + client.getVersion());
		messages.add("Hostname: " + hostname);
		messages.add("Port: " + port);
		messages.add("Database Name: " + databaseName);
		messages.add("");
		messages.add("Device specifications collection name: " + getDeviceSpecificationsCollectionName());
		messages.add("Device commands collection name: " + getDeviceCommandsCollectionName());
		messages.add("Devices collection name: " + getDevicesCollectionName());
		messages.add("Device assignments collection name: " + getDeviceAssignmentsCollectionName());
		messages.add("Sites collection name: " + getSitesCollectionName());
		messages.add("Zones collection name: " + getZonesCollectionName());
		messages.add("Measurements collection name: " + getMeasurementsCollectionName());
		messages.add("Locations collection name: " + getLocationsCollectionName());
		messages.add("Alerts collection name: " + getAlertsCollectionName());
		messages.add("Invocations collection name: " + getInvocationsCollectionName());
		messages.add("Users collection name: " + getUsersCollectionName());
		messages.add("Authorities collection name: " + getAuthoritiesCollectionName());
		String message = StringMessageUtils.getBoilerPlate(messages, '*', 60);
		LOGGER.info("\n" + message + "\n");
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

	public DBCollection getMeasurementsCollection() {
		return getSiteWhereDatabase().getCollection(getMeasurementsCollectionName());
	}

	public DBCollection getLocationsCollection() {
		return getSiteWhereDatabase().getCollection(getLocationsCollectionName());
	}

	public DBCollection getAlertsCollection() {
		return getSiteWhereDatabase().getCollection(getAlertsCollectionName());
	}

	public DBCollection getInvocationsCollection() {
		return getSiteWhereDatabase().getCollection(getInvocationsCollectionName());
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

	public String getMeasurementsCollectionName() {
		return measurementsCollectionName;
	}

	public void setMeasurementsCollectionName(String measurementsCollectionName) {
		this.measurementsCollectionName = measurementsCollectionName;
	}

	public String getLocationsCollectionName() {
		return locationsCollectionName;
	}

	public void setLocationsCollectionName(String locationsCollectionName) {
		this.locationsCollectionName = locationsCollectionName;
	}

	public String getAlertsCollectionName() {
		return alertsCollectionName;
	}

	public void setAlertsCollectionName(String alertsCollectionName) {
		this.alertsCollectionName = alertsCollectionName;
	}

	public String getInvocationsCollectionName() {
		return invocationsCollectionName;
	}

	public void setInvocationsCollectionName(String invocationsCollectionName) {
		this.invocationsCollectionName = invocationsCollectionName;
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