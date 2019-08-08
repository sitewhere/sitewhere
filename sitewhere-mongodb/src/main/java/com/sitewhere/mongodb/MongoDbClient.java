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

import org.springframework.util.StringUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
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
	implements IDiscoverableTenantLifecycleComponent, CommandListener {

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
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	while (true) {
	    try {
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.serverSelectionTimeout(-1); // Wait indefinitely for server selection.
		builder.maxConnectionIdleTime(60 * 60 * 1000); // 1hour
		builder.applicationName("SiteWhere/" + getMicroservice().getName());
		builder.addCommandListener(this);

		getLogger().info("MongoDB Connection: hosts=" + getHostname().getValue() + " ports="
			+ getConfiguration().getPort() + " replicaSet=" + getConfiguration().getReplicaSetName());

		// Parse hostname(s) and port(s) into address list.
		List<ServerAddress> addresses = parseServerAddresses();

		// Indicator for whether a replica set is being used.
		boolean isUsingReplicaSet = !StringUtils.isEmpty(getConfiguration().getReplicaSetName());

		if (isUsingReplicaSet) {
		    getLogger().info("MongoDB using replica set mode.");
		    builder.requiredReplicaSetName(getConfiguration().getReplicaSetName());
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

		getDatabase().listCollectionNames();
		return;
	    } catch (MongoTimeoutException e) {
		getLogger().warn("Timed out connecting to MongoDB. Will attempt to reconnect to "
			+ getHostname().getValue() + ":" + getConfiguration().getPort() + ".", e);
	    }
	}
    }

    /*
     * @see com.mongodb.event.CommandListener#commandStarted(com.mongodb.event.
     * CommandStartedEvent)
     */
    @Override
    public void commandStarted(CommandStartedEvent event) {
    }

    /*
     * @see com.mongodb.event.CommandListener#commandSucceeded(com.mongodb.event.
     * CommandSucceededEvent)
     */
    @Override
    public void commandSucceeded(CommandSucceededEvent event) {
    }

    /*
     * @see com.mongodb.event.CommandListener#commandFailed(com.mongodb.event.
     * CommandFailedEvent)
     */
    @Override
    public void commandFailed(CommandFailedEvent event) {
	getLogger().warn("MongoDB command failed.", event);
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
	getLogger().info("MongoDB client will connect to " + getHostname().getValue() + ":"
		+ getConfiguration().getPort() + " for database '" + getDatabaseName().getValue() + "'");
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