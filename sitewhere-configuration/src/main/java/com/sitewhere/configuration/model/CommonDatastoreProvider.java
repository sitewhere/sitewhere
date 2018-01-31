/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.configuration.parser.IDatastoreCommonParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Provides common datastore configuration model roles and elements.
 * 
 * @author Derek
 */
public class CommonDatastoreProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/common/datastore";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return CommonDatastoreRoles.Datastore;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createDeviceManagementDatastoreElement());
	addElement(creatEventManagementDatastoreElement());
	addElement(createMongoDbDatastoreElement());
	addElement(createMongoDbReferenceElement());
	addElement(createInfluxDbDatastoreElement());
	addElement(createInfluxDbReferenceElement());
    }

    /**
     * Create device management datastore element.
     * 
     * @return
     */
    protected ElementNode createDeviceManagementDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonDatastoreRoles.DeviceManagementDatastore.getRole().getName(),
		IDatastoreCommonParser.Elements.DeviceManagementDatastore.getLocalName(), "database",
		CommonDatastoreRoleKeys.DeviceManagementDatastore, this);

	builder.description("Specifies how data will be stored.");

	return builder.build();
    }

    /**
     * Create event management datastore element.
     * 
     * @return
     */
    protected ElementNode creatEventManagementDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonDatastoreRoles.EventManagementDatastore.getRole().getName(),
		IDatastoreCommonParser.Elements.EventManagementDatastore.getLocalName(), "database",
		CommonDatastoreRoleKeys.EventManagementDatastore, this);

	builder.description("Specifies how data will be stored.");

	return builder.build();
    }

    /**
     * Create MongoDB datastore element.
     * 
     * @return
     */
    protected ElementNode createMongoDbDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder(CommonDatastoreRoles.MongoDBDatastore.getRole().getName(),
		IDatastoreCommonParser.DeviceManagementDatastoreElements.MongoDBDatastore.getLocalName(), "database",
		CommonDatastoreRoleKeys.MongoDBDatastore, this);

	builder.description("Use a locally-defined MongoDB datastore.");
	addMongoDbAttributes(builder);

	return builder.build();
    }

    /**
     * Create MongoDB datastore reference element.
     * 
     * @return
     */
    protected ElementNode createMongoDbReferenceElement() {
	ElementNode.Builder builder = new ElementNode.Builder(CommonDatastoreRoles.MongoDBReference.getRole().getName(),
		IDatastoreCommonParser.DeviceManagementDatastoreElements.MongoDBReference.getLocalName(), "database",
		CommonDatastoreRoleKeys.MongoDBReference, this);

	builder.description("Use a globally-defined MongoDB datastore.");

	builder.attribute((new AttributeNode.Builder("Configuration Id", "id", AttributeType.String)
		.description("Unique id for global configuration").makeRequired().build()));

	return builder.build();
    }

    /**
     * Create InfluxDB datastore element.
     * 
     * @return
     */
    protected ElementNode createInfluxDbDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonDatastoreRoles.InfluxDBDatastore.getRole().getName(),
		IDatastoreCommonParser.EventManagementDatastoreElements.InfluxDBDatastore.getLocalName(), "database",
		CommonDatastoreRoleKeys.InfluxDBDatastore, this);

	builder.description("Use a locally-defined InfluxDB datastore.");
	addMongoDbAttributes(builder);

	return builder.build();
    }

    /**
     * Create InfluxDB datastore reference element.
     * 
     * @return
     */
    protected ElementNode createInfluxDbReferenceElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonDatastoreRoles.InfluxDBReference.getRole().getName(),
		IDatastoreCommonParser.EventManagementDatastoreElements.InfluxDBReference.getLocalName(), "database",
		CommonDatastoreRoleKeys.InfluxDBReference, this);

	builder.description("Use a globally-defined InfluxDB datastore.");

	builder.attribute((new AttributeNode.Builder("Configuration Id", "id", AttributeType.String)
		.description("Unique id for global configuration").makeRequired().build()));

	return builder.build();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (CommonDatastoreRoles role : CommonDatastoreRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

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

    /**
     * Adds InfluxDB configuration attributes.
     * 
     * @param builder
     */
    public static void addInfluxDbAttributes(ElementNode.Builder builder) {
	builder.attributeGroup("conn", "InfluxDB Connectivity");
	builder.attribute((new AttributeNode.Builder("Connection URL", "connectUrl", AttributeType.String)
		.description("Specifies URL used to connect to InfluxDB.").group("conn")
		.defaultValue("http://localhost:8086").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String)
		.description("Username for InfluxDB authentication.").group("conn").defaultValue("root").build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String)
		.description("Password for InfluxDB authentication.").group("conn").defaultValue("root").build()));
	builder.attribute((new AttributeNode.Builder("Database", "database", AttributeType.String)
		.description("InfluxDB database name.").group("conn").defaultValue("sitewhere").build()));
	builder.attribute((new AttributeNode.Builder("Retention policy", "retention", AttributeType.String)
		.description("InfluxDB retention policy name.").group("conn").defaultValue("autogen").build()));
	builder.attribute((new AttributeNode.Builder("Log level", "logLevel", AttributeType.String)
		.description("Log level for debugging InfluxDB interactions.").group("conn").choice("None", "none")
		.choice("Basic", "basic").choice("Headers", "headers").choice("Full", "full").defaultValue("none")
		.build()));

	builder.attributeGroup("batch", "InfluxDB Batch Event Processing");
	builder.attribute((new AttributeNode.Builder("Enable batch processing", "enableBatch", AttributeType.Boolean)
		.description("Enable delivery of events in batches.").group("batch").defaultValue("true").build()));
	builder.attribute((new AttributeNode.Builder("Max batch chunk size", "batchChunkSize", AttributeType.Integer)
		.description("Maximum number of events to send in a batch.").group("batch").defaultValue("1000")
		.build()));
	builder.attribute(
		(new AttributeNode.Builder("Max batch send interval (ms)", "batchIntervalMs", AttributeType.Integer)
			.description("Maximum amount of time (in ms) to wait before sending a batch.").group("batch")
			.defaultValue("100").build()));
    }
}