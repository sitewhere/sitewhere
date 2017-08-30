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
import com.mongodb.MongoCommandException;
import com.mongodb.MongoCredential;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
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
public class BaseMongoClient extends TenantLifecycleComponent implements IDiscoverableTenantLifecycleComponent {

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

    public BaseMongoClient() {
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
		    this.client = new MongoClient(addresses, Arrays.asList(credential), builder.build());
		} else {
		    this.client = new MongoClient(addresses.get(0), Arrays.asList(credential), builder.build());
		}
	    }

	    // Handle unauthenticated access.
	    else {
		if (isUsingReplicaSet) {
		    this.client = new MongoClient(addresses, builder.build());
		} else {
		    this.client = new MongoClient(addresses.get(0), builder.build());
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
     * Create a connection to the primary in a replica set.
     * 
     * @param addresses
     * @return
     */
    protected MongoClient getPrimaryConnection(List<ServerAddress> addresses) {
	MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
	if ((getUsername() != null) && (getPassword() != null)) {
	    MongoCredential credential = MongoCredential.createCredential(getUsername(), getAuthDatabaseName(),
		    getPassword().toCharArray());
	    return new MongoClient(addresses.get(0), Arrays.asList(credential), builder.build());
	} else {
	    return new MongoClient(addresses.get(0), builder.build());
	}
    }

    /**
     * Detects whether a replica set is configured and creates one if not.
     * 
     * @param addresses
     */
    protected void doAutoConfigureReplication(List<ServerAddress> addresses) throws SiteWhereException {
	// Only connect to primary for sending commands.
	MongoClient primary = getPrimaryConnection(addresses);

	// Check for existing replica set configuration.
	LOGGER.info("Checking for existing replica set...");
	try {
	    Document result = primary.getDatabase("admin").runCommand(new BasicDBObject("replSetGetStatus", 1));
	    if (result.getDouble("ok") == 1) {
		LOGGER.warn("Replica set already configured. Skipping auto-configuration.");
		return;
	    }
	} catch (MongoCommandException e) {
	    LOGGER.info("Replica set was not configured.");
	}

	// Create configuration for new replica set.
	try {
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
	    Document result = primary.getDatabase("admin").runCommand(new BasicDBObject("replSetInitiate", config));
	    if (result.getDouble("ok") != 1) {
		throw new SiteWhereException("Unable to auto-configure replica set.\n" + result.toJson());
	    }
	    LOGGER.info("Replica set '" + getReplicaSetName() + "' creation command successful.");
	} finally {
	    primary.close();
	}
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
	String message = Boilerplate.boilerplate(messages, "*");
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

    /**
     * Get database associated with a tenant.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    public MongoDatabase getTenantDatabase(ITenant tenant) throws SiteWhereException {
	if (tenant != null) {
	    return getTenantDatabase(tenant.getId());
	}
	throw new SiteWhereException("Called getTenantDatabase() with null tenant.");
    }

    /**
     * Get database associated with a tenant id.
     * 
     * @param tenantId
     * @return
     */
    public MongoDatabase getTenantDatabase(String tenantId) {
	return client.getDatabase("tenant-" + tenantId);
    }

    /**
     * Get database for storing global model objects.
     * 
     * @return
     */
    public MongoDatabase getGlobalDatabase() {
	return client.getDatabase(getDatabaseName());
    }

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
}