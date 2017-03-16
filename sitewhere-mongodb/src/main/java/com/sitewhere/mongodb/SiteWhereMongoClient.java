/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sitewhere.core.Boilerplate;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Spring wrapper for initializing a Mongo client used by SiteWhere components.
 * 
 * @author dadams
 */
public class SiteWhereMongoClient extends TenantLifecycleComponent
	implements IDiscoverableTenantLifecycleComponent, IGlobalManagementMongoClient, IDeviceManagementMongoClient,
	IAssetManagementMongoClient, IScheduleManagementMongoClient {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Default hostname for Mongo */
    private static final String DEFAULT_HOSTNAME = "localhost";

    /** Default port for Mongo */
    private static final String DEFAULT_PORT = "27017";

    /** Default database name */
    private static final String DEFAULT_DATABASE_NAME = "sitewhere";

    /** Default authentication database name */
    private static final String DEFAULT_AUTH_DATABASE_NAME = "admin";

    /** Mongo client */
    private MongoClient client;

    /** Hostname used to access the Mongo datastore */
    private String hostname = DEFAULT_HOSTNAME;

    /** Port used to access the Mongo datastore */
    private String port = DEFAULT_PORT;

    /** Username used for authentication */
    private String username;

    /** Password used for authentication */
    private String password;

    /** Replica set name (blank or null for none) */
    private String replicaSetName;

    /** Indicates if replication should be auto-configured */
    private boolean autoConfigureReplication = true;

    /** Database that holds sitewhere collections */
    private String databaseName = DEFAULT_DATABASE_NAME;

    /** Database that holds user credentials */
    private String authDatabaseName = DEFAULT_AUTH_DATABASE_NAME;

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
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
	    builder.maxConnectionIdleTime(60 * 60 * 1000); // 1hour

	    LOGGER.info("MongoDB Connection: hosts=" + getHostname() + " ports=" + getPort() + " replicaSet="
		    + getReplicaSetName());

	    // Parse hostname(s) and port(s) into address list.
	    List<ServerAddress> addresses = parseServerAddresses();

	    // Indicator for whether a replica set is being used.
	    boolean isUsingReplicaSet = ((addresses.size() > 1) && (!StringUtils.isEmpty(getReplicaSetName())));

	    if (isUsingReplicaSet) {
		LOGGER.info("MongoDB using replicated mode.");
	    } else {
		LOGGER.info("MongoDB using standalone mode.");
	    }

	    // Handle authenticated access.
	    if ((getUsername() != null) && (getPassword() != null)) {
		MongoCredential credential = MongoCredential.createCredential(getUsername(), getAuthDatabaseName(),
			getPassword().toCharArray());
		if (isUsingReplicaSet) {
		    this.client = new MongoClient(addresses.get(0), Arrays.asList(credential), builder.build());
		} else {
		    this.client = new MongoClient(addresses, Arrays.asList(credential), builder.build());
		}
	    }

	    // Handle unauthenticated access.
	    else {
		if (isUsingReplicaSet) {
		    this.client = new MongoClient(addresses.get(0), builder.build());
		} else {
		    this.client = new MongoClient(addresses, builder.build());
		}
	    }

	    // Handle automatic configuration of replication.
	    if ((isUsingReplicaSet) && (isAutoConfigureReplication())) {
		doAutoConfigureReplication(addresses);
	    }

	    // Force interaction to test connectivity.
	    getGlobalDatabase().listCollectionNames();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Timed out connecting to MongoDB instance. "
		    + "Verify that MongoDB is running on " + hostname + ":" + port + " and restart server.", e);
	}
    }

    /**
     * Detects whether a replica set is configured and creates one if not.
     * 
     * @param addresses
     */
    protected void doAutoConfigureReplication(List<ServerAddress> addresses) throws SiteWhereException {
	// Check for existing replica set configuration.
	LOGGER.info("Checking for existing replica set...");
	Document result = getMongoClient().getDatabase("admin").runCommand(new BasicDBObject("replSetGetStatus", 1));
	if (result.getDouble("ok") == 1) {
	    LOGGER.warn("Replica set already configured. Skipping auto-configuration.");
	    return;
	}

	// Create configuration for new replica set.
	LOGGER.info("Configuring new replica set '" + getReplicaSetName() + "'.");
	BasicDBObject config = new BasicDBObject("_id", getReplicaSetName());
	List<BasicDBObject> servers = new ArrayList<BasicDBObject>();

	// Create list of members in replica set.
	int index = 0;
	for (final ServerAddress address : addresses) {
	    BasicDBObject server = new BasicDBObject("_id", index);
	    server.put("host", (address.getHost() + ":" + address.getPort()));

	    // First server in list is preferred primary.
	    if (index == 0) {
		server.put("priority", 10);
	    }

	    servers.add(server);
	    index++;
	}
	config.put("members", servers);

	// Send command.
	result = getMongoClient().getDatabase("admin").runCommand(new BasicDBObject("replSetInitiate", config));
	if (result.getDouble("ok") != 1) {
	    throw new SiteWhereException("Unable to auto-configure replica set.\n" + result.toJson());
	}
	LOGGER.info("Replica set '" + getReplicaSetName() + "' creation command successful.");
    }

    /**
     * Represent an array as a space-delimited string.
     * 
     * @param input
     * @return
     */
    protected String arrayAsString(String[] input) {
	String result = "";
	for (int i = 0; i < input.length; i++) {
	    if (i > 0) {
		result += " ";
	    }
	    result += input[i];
	}
	return result;
    }

    /**
     * Parse hostname(s) and port(s) into {@link ServerAddress} entries.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected List<ServerAddress> parseServerAddresses() throws SiteWhereException {
	String[] hosts = getHostname().split(",");
	String[] ports = getPort().split(",");

	if (hosts.length != ports.length) {
	    throw new SiteWhereException("Number of hosts does not match number of ports. Hosts(" + arrayAsString(hosts)
		    + ") Ports(" + arrayAsString(ports) + ").");
	}

	List<ServerAddress> addresses = new ArrayList<ServerAddress>();
	for (int i = 0; i < hosts.length; i++) {
	    try {
		addresses.add(new ServerAddress(hosts[i].trim(), Integer.parseInt(ports[i].trim())));
	    } catch (NumberFormatException e) {
		throw new SiteWhereException("Non-numeric port number specified for MQTT broker.");
	    }
	}
	return addresses;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	client.close();
    }

    /**
     * Get the MongoClient.
     * 
     * @return
     */
    public MongoClient getMongoClient() {
	return client;
    }

    public MongoDatabase getTenantDatabase(ITenant tenant) {
	return client.getDatabase("tenant-" + tenant.getId());
    }

    public MongoDatabase getGlobalDatabase() {
	return client.getDatabase(getDatabaseName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getDeviceSpecificationsCollection(com.sitewhere.spi.tenant.ITenant)
     */
    public MongoCollection<Document> getDeviceSpecificationsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getDeviceSpecificationsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getDeviceCommandsCollection( com.sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getDeviceCommandsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getDeviceCommandsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IDeviceManagementMongoClient#getDevicesCollection(
     * com. sitewhere .spi.user.ITenant)
     */
    public MongoCollection<Document> getDevicesCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getDevicesCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getDeviceAssignmentsCollection (com.sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getDeviceAssignmentsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getDeviceAssignmentsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IDeviceManagementMongoClient#getSitesCollection(com
     * .sitewhere .spi.user.ITenant)
     */
    public MongoCollection<Document> getSitesCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getSitesCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IDeviceManagementMongoClient#getZonesCollection(com
     * .sitewhere .spi.user.ITenant)
     */
    public MongoCollection<Document> getZonesCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getZonesCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getDeviceGroupsCollection(com .sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getDeviceGroupsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getDeviceGroupsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getGroupElementsCollection(com .sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getGroupElementsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getGroupElementsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IDeviceManagementMongoClient#getEventsCollection(
     * com. sitewhere .spi.user.ITenant)
     */
    public MongoCollection<Document> getEventsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getEventsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IDeviceManagementMongoClient#getStreamsCollection(
     * com. sitewhere .spi.user.ITenant)
     */
    public MongoCollection<Document> getStreamsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getStreamsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getStreamDataCollection(com. sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getStreamDataCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getStreamDataCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getBatchOperationsCollection (com.sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getBatchOperationsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getBatchOperationsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IDeviceManagementMongoClient#
     * getBatchOperationElementsCollection (com.sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getBatchOperationElementsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getBatchOperationElementsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IGlobalManagementMongoClient#getUsersCollection()
     */
    @Override
    public MongoCollection<Document> getUsersCollection() {
	return getGlobalDatabase().getCollection(getUsersCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IGlobalManagementMongoClient#
     * getAuthoritiesCollection()
     */
    @Override
    public MongoCollection<Document> getAuthoritiesCollection() {
	return getGlobalDatabase().getCollection(getAuthoritiesCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IGlobalManagementMongoClient#getTenantsCollection()
     */
    @Override
    public MongoCollection<Document> getTenantsCollection() {
	return getGlobalDatabase().getCollection(getTenantsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IAssetManagementMongoClient#
     * getAssetCategoriesCollection( com.sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getAssetCategoriesCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getAssetCategoriesCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IAssetManagementMongoClient#getAssetsCollection(com
     * .sitewhere .spi.user.ITenant)
     */
    public MongoCollection<Document> getAssetsCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getAssetsCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IScheduleManagementMongoClient#
     * getSchedulesCollection(com .sitewhere.spi.user.ITenant)
     */
    @Override
    public MongoCollection<Document> getSchedulesCollection(ITenant tenant) {
	return getTenantDatabase(tenant).getCollection(getSchedulesCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IScheduleManagementMongoClient#
     * getScheduledJobsCollection (com.sitewhere.spi.user.ITenant)
     */
    @Override
    public MongoCollection<Document> getScheduledJobsCollection(ITenant tenant) {
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

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public String getPort() {
	return port;
    }

    public void setPort(String port) {
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

    public String getReplicaSetName() {
	return replicaSetName;
    }

    public void setReplicaSetName(String replicaSetName) {
	this.replicaSetName = replicaSetName;
    }

    public boolean isAutoConfigureReplication() {
	return autoConfigureReplication;
    }

    public void setAutoConfigureReplication(boolean autoConfigureReplication) {
	this.autoConfigureReplication = autoConfigureReplication;
    }

    public String getDatabaseName() {
	return databaseName;
    }

    public void setDatabaseName(String databaseName) {
	this.databaseName = databaseName;
    }

    public String getAuthDatabaseName() {
	return authDatabaseName;
    }

    public void setAuthDatabaseName(String authDatabaseName) {
	this.authDatabaseName = authDatabaseName;
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