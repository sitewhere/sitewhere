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
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.core.Boilerplate;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.server.lifecycle.parameters.StringComponentParameter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Client used for connecting to and interacting with an MongoDB server.
 * 
 * @author Derek
 */
public abstract class MongoDbClient extends TenantEngineLifecycleComponent
	implements IDiscoverableTenantLifecycleComponent {

    /** MongoDB client */
    private MongoClient client;

    /** MongoDB Configuration */
    private MongoConfiguration configuration;

    /** Hostname parameter */
    private ILifecycleComponentParameter<String> hostname;

    /** Database parameter */
    private ILifecycleComponentParameter<String> databaseName;

    public MongoDbClient(MongoConfiguration configuration) {
	super(LifecycleComponentType.DataStore);
	this.configuration = configuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent#
     * isRequired()
     */
    @Override
    public boolean isRequired() {
	return true;
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initializeParameters()
     */
    @Override
    public void initializeParameters() throws SiteWhereException {
	// Add hostname.
	this.hostname = StringComponentParameter.newBuilder(this, "Hostname").value(getConfiguration().getHostname())
		.makeRequired().build();
	getParameters().add(hostname);

	// Add database name.
	this.databaseName = StringComponentParameter.newBuilder(this, "Database")
		.value(getConfiguration().getDatabaseName()).makeRequired().build();
	getParameters().add(databaseName);
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

	    getLogger().info("MongoDB Connection: hosts=" + getHostname().getValue() + " ports="
		    + getConfiguration().getPort() + " replicaSet=" + getConfiguration().getReplicaSetName());

	    // Parse hostname(s) and port(s) into address list.
	    List<ServerAddress> addresses = parseServerAddresses();

	    // Indicator for whether a replica set is being used.
	    boolean isUsingReplicaSet = ((addresses.size() > 1)
		    && (!StringUtils.isEmpty(getConfiguration().getReplicaSetName())));

	    if (isUsingReplicaSet) {
		getLogger().info("MongoDB using replicated mode.");
	    } else {
		getLogger().info("MongoDB using standalone mode.");
	    }

	    // Handle authenticated access.
	    if ((getConfiguration().getUsername() != null) && (getConfiguration().getPassword() != null)) {
		MongoCredential credential = MongoCredential.createCredential(getConfiguration().getUsername(),
			getConfiguration().getAuthDatabaseName(), getConfiguration().getPassword().toCharArray());
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
	    if ((isUsingReplicaSet) && (getConfiguration().isAutoConfigureReplication())) {
		doAutoConfigureReplication(addresses);
	    }

	    // Force interaction to test connectivity.
	    getDatabase().listCollectionNames();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException(
		    "Timed out connecting to MongoDB instance. " + "Verify that MongoDB is running on "
			    + getHostname().getValue() + ":" + getConfiguration().getPort() + " and restart server.",
		    e);
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
	if ((getConfiguration().getUsername() != null) && (getConfiguration().getPassword() != null)) {
	    MongoCredential credential = MongoCredential.createCredential(getConfiguration().getUsername(),
		    getConfiguration().getAuthDatabaseName(), getConfiguration().getPassword().toCharArray());
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
	getLogger().info("Checking for existing replica set...");
	try {
	    Document result = primary.getDatabase("admin").runCommand(new BasicDBObject("replSetGetStatus", 1));
	    if (result.getDouble("ok") == 1) {
		getLogger().warn("Replica set already configured. Skipping auto-configuration.");
		return;
	    }
	} catch (MongoCommandException e) {
	    getLogger().info("Replica set was not configured.");
	}

	// Create configuration for new replica set.
	try {
	    getLogger().info("Configuring new replica set '" + getConfiguration().getReplicaSetName() + "'.");
	    BasicDBObject config = new BasicDBObject("_id", getConfiguration().getReplicaSetName());
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
	    getLogger()
		    .info("Replica set '" + getConfiguration().getReplicaSetName() + "' creation command successful.");
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
	String[] hosts = getHostname().getValue().split(",");
	String[] ports = getConfiguration().getPort().split(",");

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
	messages.add("Hostname: " + getHostname().getValue());
	messages.add("Port: " + getConfiguration().getPort());
	messages.add("Database Name: " + getDatabaseName().getValue());
	String message = Boilerplate.boilerplate(messages, "*");
	getLogger().info("\n" + message + "\n");
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
    public MongoClient getMongoClient() throws SiteWhereException {
	if (client == null) {
	    throw new SiteWhereException("Client is null. Mongo client was not properly initialized.");
	}
	return client;
    }

    /**
     * Get MongoDB database client.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoDatabase getDatabase() throws SiteWhereException {
	return getMongoClient().getDatabase(getDatabaseName().getValue());
    }

    public MongoConfiguration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(MongoConfiguration configuration) {
	this.configuration = configuration;
    }

    public ILifecycleComponentParameter<String> getHostname() {
	return hostname;
    }

    public void setHostname(ILifecycleComponentParameter<String> hostname) {
	this.hostname = hostname;
    }

    public ILifecycleComponentParameter<String> getDatabaseName() {
	return databaseName;
    }

    public void setDatabaseName(ILifecycleComponentParameter<String> databaseName) {
	this.databaseName = databaseName;
    }
}