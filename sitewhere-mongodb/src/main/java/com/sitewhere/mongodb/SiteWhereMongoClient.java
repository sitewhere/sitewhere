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
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
import com.sitewhere.core.Boilerplate;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IInternetConnected;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Spring wrapper for initializing a Mongo client used by SiteWhere components.
 * 
 * @author dadams
 */
public class SiteWhereMongoClient extends TenantLifecycleComponent
		implements IDiscoverableTenantLifecycleComponent, IGlobalManagementMongoClient, IDeviceManagementMongoClient,
		IAssetManagementMongoClient, IScheduleManagementMongoClient, IInternetConnected {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

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

	/** Username used for authentication */
	private String username;

	/** Password used for authentication */
	private String password;

	/** Database that holds sitewhere collections */
	private String databaseName = DEFAULT_DATABASE_NAME;

	/** Injected name used for device specifications collection */
	private String deviceSpecificationsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_SPECIFICATIONS_COLLECTION_NAME;

	/** Injected name used for device commands collection */
	private String deviceCommandsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_COMMANDS_COLLECTION_NAME;

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

	/** Injected name used for events collection */
	private String eventsCollectionName = IDeviceManagementMongoClient.DEFAULT_EVENTS_COLLECTION_NAME;

	/** Injected name used for device streams collection */
	private String streamsCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_STREAMS_COLLECTION_NAME;

	/** Injected name used for device stream data collection */
	private String streamDataCollectionName = IDeviceManagementMongoClient.DEFAULT_DEVICE_STREAM_DATA_COLLECTION_NAME;

	/** Injected name used for batch operations collection */
	private String batchOperationsCollectionName = IDeviceManagementMongoClient.DEFAULT_BATCH_OPERATIONS_COLLECTION_NAME;

	/** Injected name used for batch operation elements collection */
	private String batchOperationElementsCollectionName = IDeviceManagementMongoClient.DEFAULT_BATCH_OPERATION_ELEMENTS_COLLECTION_NAME;

	/** Injected name used for users collection */
	private String usersCollectionName = IGlobalManagementMongoClient.DEFAULT_USERS_COLLECTION_NAME;

	/** Injected name used for authorities collection */
	private String authoritiesCollectionName = IGlobalManagementMongoClient.DEFAULT_AUTHORITIES_COLLECTION_NAME;

	/** Injected name used for tenants collection */
	private String tenantsCollectionName = IGlobalManagementMongoClient.DEFAULT_TENANTS_COLLECTION_NAME;

	/** Injected name used for tenant groups collection */
	private String tenantGroupsCollectionName = IGlobalManagementMongoClient.DEFAULT_TENANT_GROUPS_COLLECTION_NAME;

	/** Injected name used for tenant group elements collection */
	private String tenantGroupElementsCollectionName = IGlobalManagementMongoClient.DEFAULT_TENANT_GROUP_ELEMENTS_COLLECTION_NAME;

	/** Injected name used for asset categories collection */
	private String assetCategoriesCollectionName = IAssetManagementMongoClient.DEFAULT_ASSET_CATEGORIES_COLLECTION_NAME;

	/** Injected name used for assets collection */
	private String assetsCollectionName = IAssetManagementMongoClient.DEFAULT_ASSETS_COLLECTION_NAME;

	/** Injected name used for schedules collection */
	private String schedulesCollectionName = IScheduleManagementMongoClient.DEFAULT_SCHEDULES_COLLECTION_NAME;

	/** Injected name used for scheduled jobs collection */
	private String scheduledJobsCollectionName = IScheduleManagementMongoClient.DEFAULT_SCHEDULED_JOBS_COLLECTION_NAME;

	public SiteWhereMongoClient() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		try {
			MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
			builder.maxConnectionIdleTime(60 * 60 * 1000); // 1hour

			// Handle authenticated access.
			if ((getUsername() != null) && (getPassword() != null)) {
				MongoCredential credential = MongoCredential.createCredential(getUsername(), getDatabaseName(),
						getPassword().toCharArray());
				this.client = new MongoClient(new ServerAddress(getHostname(), getPort()), Arrays.asList(credential),
						builder.build());
			}

			// Handle unauthenticated access.
			else {
				this.client = new MongoClient(new ServerAddress(getHostname(), getPort()), builder.build());
			}

			// Force interaction to test connectivity.
			getGlobalDatabase().getStats();

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
			messages.add("Streams collection name: " + getStreamsCollectionName());
			messages.add("Batch operations collection name: " + getBatchOperationsCollectionName());
			messages.add("Batch operation elements collection name: " + getBatchOperationElementsCollectionName());
			messages.add("");
			messages.add("---------------------");
			messages.add("-- User Management --");
			messages.add("---------------------");
			messages.add("Users collection name: " + getUsersCollectionName());
			messages.add("Authorities collection name: " + getAuthoritiesCollectionName());
			messages.add("");
			messages.add("-----------------------");
			messages.add("-- Tenant Management --");
			messages.add("-----------------------");
			messages.add("Tenants collection name: " + getTenantsCollectionName());
			messages.add("Tenant groups collection name: " + getTenantGroupsCollectionName());
			messages.add("Tenant group elements collection name: " + getTenantGroupElementsCollectionName());
			messages.add("");
			messages.add("----------------------");
			messages.add("-- Asset Management --");
			messages.add("----------------------");
			messages.add("Asset categories collection name: " + getAssetCategoriesCollectionName());
			messages.add("Assets collection name: " + getAssetsCollectionName());
			messages.add("");
			messages.add("-------------------------");
			messages.add("-- Schedule Management --");
			messages.add("-------------------------");
			messages.add("Schedules collection name: " + getSchedulesCollectionName());
			messages.add("Scheduled jobs collection name: " + getScheduledJobsCollectionName());
			String message = Boilerplate.boilerplate(messages, '*', 60);
			LOGGER.info("\n" + message + "\n");
		} catch (UnknownHostException e) {
			throw new SiteWhereException("Unable to contact host for MongoDB instance. "
					+ "Verify that MongoDB is running on " + hostname + ":" + port + " and restart server.", e);
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Could not connect to MongoDB instance. "
					+ "Verify that MongoDB is running on " + hostname + ":" + port + " and restart server.", e);
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

	public DB getTenantDatabase(ITenant tenant) {
		return client.getDB("tenant-" + tenant.getId());
	}

	public DB getGlobalDatabase() {
		return client.getDB(getDatabaseName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
	 * getDeviceSpecificationsCollection (com.sitewhere.spi.user.ITenant)
	 */
	public DBCollection getDeviceSpecificationsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getDeviceSpecificationsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
	 * getDeviceCommandsCollection( com.sitewhere.spi.user.ITenant)
	 */
	public DBCollection getDeviceCommandsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getDeviceCommandsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.mongodb.IDeviceManagementMongoClient#getDevicesCollection(
	 * com. sitewhere .spi.user.ITenant)
	 */
	public DBCollection getDevicesCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getDevicesCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
	 * getDeviceAssignmentsCollection (com.sitewhere.spi.user.ITenant)
	 */
	public DBCollection getDeviceAssignmentsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getDeviceAssignmentsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.mongodb.IDeviceManagementMongoClient#getSitesCollection(com
	 * .sitewhere .spi.user.ITenant)
	 */
	public DBCollection getSitesCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getSitesCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.mongodb.IDeviceManagementMongoClient#getZonesCollection(com
	 * .sitewhere .spi.user.ITenant)
	 */
	public DBCollection getZonesCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getZonesCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
	 * getDeviceGroupsCollection(com .sitewhere.spi.user.ITenant)
	 */
	public DBCollection getDeviceGroupsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getDeviceGroupsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
	 * getGroupElementsCollection(com .sitewhere.spi.user.ITenant)
	 */
	public DBCollection getGroupElementsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getGroupElementsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.mongodb.IDeviceManagementMongoClient#getEventsCollection(
	 * com. sitewhere .spi.user.ITenant)
	 */
	public DBCollection getEventsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getEventsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.mongodb.IDeviceManagementMongoClient#getStreamsCollection(
	 * com. sitewhere .spi.user.ITenant)
	 */
	public DBCollection getStreamsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getStreamsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
	 * getStreamDataCollection(com. sitewhere.spi.user.ITenant)
	 */
	public DBCollection getStreamDataCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getStreamDataCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
	 * getBatchOperationsCollection (com.sitewhere.spi.user.ITenant)
	 */
	public DBCollection getBatchOperationsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getBatchOperationsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
	 * getBatchOperationElementsCollection (com.sitewhere.spi.user.ITenant)
	 */
	public DBCollection getBatchOperationElementsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getBatchOperationElementsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.mongodb.IGlobalManagementMongoClient#getUsersCollection()
	 */
	@Override
	public DBCollection getUsersCollection() {
		return getGlobalDatabase().getCollection(getUsersCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IGlobalManagementMongoClient#
	 * getAuthoritiesCollection()
	 */
	@Override
	public DBCollection getAuthoritiesCollection() {
		return getGlobalDatabase().getCollection(getAuthoritiesCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.mongodb.IGlobalManagementMongoClient#getTenantsCollection()
	 */
	@Override
	public DBCollection getTenantsCollection() {
		return getGlobalDatabase().getCollection(getTenantsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IGlobalManagementMongoClient#
	 * getTenantGroupsCollection()
	 */
	@Override
	public DBCollection getTenantGroupsCollection() {
		return getGlobalDatabase().getCollection(getTenantGroupsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IGlobalManagementMongoClient#
	 * getTenantGroupElementsCollection()
	 */
	@Override
	public DBCollection getTenantGroupElementsCollection() {
		return getGlobalDatabase().getCollection(getTenantGroupElementsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IAssetManagementMongoClient#
	 * getAssetCategoriesCollection( com.sitewhere.spi.user.ITenant)
	 */
	public DBCollection getAssetCategoriesCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getAssetCategoriesCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.mongodb.IAssetManagementMongoClient#getAssetsCollection(com
	 * .sitewhere .spi.user.ITenant)
	 */
	public DBCollection getAssetsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getAssetsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IScheduleManagementMongoClient#
	 * getSchedulesCollection(com .sitewhere.spi.user.ITenant)
	 */
	@Override
	public DBCollection getSchedulesCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getSchedulesCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.mongodb.IScheduleManagementMongoClient#
	 * getScheduledJobsCollection (com.sitewhere.spi.user.ITenant)
	 */
	@Override
	public DBCollection getScheduledJobsCollection(ITenant tenant) {
		return getTenantDatabase(tenant).getCollection(getScheduledJobsCollectionName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.common.IInternetConnected#getHostname()
	 */
	public String getHostname() {
		return hostname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.common.IInternetConnected#setHostname(java.lang.String)
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.common.IInternetConnected#getPort()
	 */
	public int getPort() {
		return port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.common.IInternetConnected#setPort(int)
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getTenantsCollectionName() {
		return tenantsCollectionName;
	}

	public String getTenantGroupsCollectionName() {
		return tenantGroupsCollectionName;
	}

	public void setTenantGroupsCollectionName(String tenantGroupsCollectionName) {
		this.tenantGroupsCollectionName = tenantGroupsCollectionName;
	}

	public String getTenantGroupElementsCollectionName() {
		return tenantGroupElementsCollectionName;
	}

	public void setTenantGroupElementsCollectionName(String tenantGroupElementsCollectionName) {
		this.tenantGroupElementsCollectionName = tenantGroupElementsCollectionName;
	}

	public void setTenantsCollectionName(String tenantsCollectionName) {
		this.tenantsCollectionName = tenantsCollectionName;
	}

	public String getAssetCategoriesCollectionName() {
		return assetCategoriesCollectionName;
	}

	public void setAssetCategoriesCollectionName(String assetCategoriesCollectionName) {
		this.assetCategoriesCollectionName = assetCategoriesCollectionName;
	}

	public String getAssetsCollectionName() {
		return assetsCollectionName;
	}

	public void setAssetsCollectionName(String assetsCollectionName) {
		this.assetsCollectionName = assetsCollectionName;
	}

	public String getSchedulesCollectionName() {
		return schedulesCollectionName;
	}

	public void setSchedulesCollectionName(String schedulesCollectionName) {
		this.schedulesCollectionName = schedulesCollectionName;
	}

	public String getScheduledJobsCollectionName() {
		return scheduledJobsCollectionName;
	}

	public void setScheduledJobsCollectionName(String scheduledJobsCollectionName) {
		this.scheduledJobsCollectionName = scheduledJobsCollectionName;
	}
}