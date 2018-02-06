/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.instance.mongodb;

/**
 * Common configuration settings for a MongoDB client.
 * 
 * @author Derek
 */
public class MongoConfiguration {

    /** Default hostname for Mongo */
    private static final String DEFAULT_HOSTNAME = "localhost";

    /** Default port for Mongo */
    private static final String DEFAULT_PORT = "27017";

    /** Default database name */
    private static final String DEFAULT_DATABASE_NAME = "sitewhere";

    /** Default authentication database name */
    private static final String DEFAULT_AUTH_DATABASE_NAME = "admin";

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