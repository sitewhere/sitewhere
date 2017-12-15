/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;

/**
 * Common shared datastore model characteristics.
 * 
 * @author Derek
 */
public class CommonDatastoreModel {

    /**
     * Adds MongoDB configuration attributes.
     * 
     * @param builder
     */
    public static void addMongoDbAttributes(ElementNode.Builder builder) {
	builder.attribute((new AttributeNode.Builder("Hostname", "hostname", AttributeType.String)
		.description("Hostname for MongoDB instance").defaultValue("${datastore.host:mongodb}").build()));
	builder.attribute((new AttributeNode.Builder("Port", "port", AttributeType.Integer)
		.description("Port on which MongoDB is running").defaultValue("${datastore.port:27017}").build()));
	builder.attribute((new AttributeNode.Builder("Database name", "databaseName", AttributeType.String)
		.description("Database name").defaultValue("${datastore.database:sitewhere}").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String)
		.description("Database authentication username").build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String)
		.description("Database authentication password").build()));
	builder.attribute((new AttributeNode.Builder("Authentication DB name", "authDatabaseName", AttributeType.String)
		.description("Authentication database name").build()));
	builder.attribute((new AttributeNode.Builder("Replica set name", "replicaSetName", AttributeType.String)
		.description("Name of replica set if using replication.").defaultValue("${datastore.replicaset:}")
		.build()));
	builder.attribute((new AttributeNode.Builder("Auto-configure replication", "autoConfigureReplication",
		AttributeType.Boolean)
			.description("Indicates whether replication should be configured automatically "
				+ "when multiple hosts/ports are specified.")
			.build()));
    }
}