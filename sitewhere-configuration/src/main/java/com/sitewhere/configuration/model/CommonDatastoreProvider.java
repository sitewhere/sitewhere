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
import com.sitewhere.spi.microservice.configuration.model.IAttributeGroup;
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
	addElement(createDeviceStateManagementDatastoreElement());
	addElement(createMongoDbDatastoreElement());
	addElement(createMongoDbReferenceElement());
	addElement(createInfluxDbDatastoreElement());
	addElement(createInfluxDbReferenceElement());
	addElement(createCassandraDatastoreElement());
	addElement(createCassandraReferenceElement());
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

	builder.description("Specifies how device management data will be stored.");

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

	builder.description("Specifies how device event data will be stored.");

	return builder.build();
    }

    /**
     * Create device state management datastore element.
     * 
     * @return
     */
    protected ElementNode createDeviceStateManagementDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonDatastoreRoles.DeviceStateManagementDatastore.getRole().getName(),
		IDatastoreCommonParser.Elements.DeviceStateManagementDatastore.getLocalName(), "database",
		CommonDatastoreRoleKeys.DeviceStateManagementDatastore, this);

	builder.description("Specifies how device state data will be stored.");

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
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	addMongoDbAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

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
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Configuration Id", "id", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("Unique id for global configuration")
			.makeRequired().build()));

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
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_BATCH);

	addInfluxDbAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY,
		ConfigurationModelProvider.ATTR_GROUP_BATCH);

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
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Configuration Id", "id", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("Unique id for global configuration")
			.makeRequired().build()));

	return builder.build();
    }

    /**
     * Create Cassandra datastore element.
     * 
     * @return
     */
    protected ElementNode createCassandraDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonDatastoreRoles.CassandraDatastore.getRole().getName(),
		IDatastoreCommonParser.EventManagementDatastoreElements.CassandraDatastore.getLocalName(), "database",
		CommonDatastoreRoleKeys.CassandraDatastore, this);

	builder.description("Use a locally-defined Apache Cassandra datastore.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	addCassandraAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	return builder.build();
    }

    /**
     * Create Cassandra datastore reference element.
     * 
     * @return
     */
    protected ElementNode createCassandraReferenceElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommonDatastoreRoles.CassandraReference.getRole().getName(),
		IDatastoreCommonParser.EventManagementDatastoreElements.CassandraReference.getLocalName(), "database",
		CommonDatastoreRoleKeys.CassandraReference, this);

	builder.description("Use a globally-defined Cassandra datastore.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Configuration Id", "id", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("Unique id for global configuration")
			.makeRequired().build()));

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
     * @param connectivity
     */
    public static void addMongoDbAttributes(ElementNode.Builder builder, IAttributeGroup connectivity) {
	builder.attribute((new AttributeNode.Builder("Hostname", "hostname", AttributeType.String, connectivity)
		.description("Hostname for MongoDB instance").defaultValue("${mongodb.host:mongodb}").build()));
	builder.attribute((new AttributeNode.Builder("Port", "port", AttributeType.Integer, connectivity)
		.description("Port on which MongoDB is running").defaultValue("${mongodb.port:27017}").build()));
	builder.attribute(
		(new AttributeNode.Builder("Database name", "databaseName", AttributeType.String, connectivity)
			.description("Database name").defaultValue("${mongodb.database:sitewhere}").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String, connectivity)
		.description("Database authentication username").build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String, connectivity)
		.description("Database authentication password").build()));
	builder.attribute((new AttributeNode.Builder("Authentication DB name", "authDatabaseName", AttributeType.String,
		connectivity).description("Authentication database name").build()));
	builder.attribute(
		(new AttributeNode.Builder("Replica set name", "replicaSetName", AttributeType.String, connectivity)
			.description("Name of replica set if using replication.").defaultValue("${mongodb.replicaset:}")
			.build()));
	builder.attribute((new AttributeNode.Builder("Auto-configure replication", "autoConfigureReplication",
		AttributeType.Boolean, connectivity)
			.description("Indicates whether replication should be configured automatically "
				+ "when multiple hosts/ports are specified.")
			.build()));
    }

    /**
     * Adds InfluxDB configuration attributes.
     * 
     * @param builder
     * @param connectivity
     * @param batch
     */
    public static void addInfluxDbAttributes(ElementNode.Builder builder, IAttributeGroup connectivity,
	    IAttributeGroup batch) {
	// Connectivity attributes.
	builder.attribute((new AttributeNode.Builder("Hostname", "hostname", AttributeType.String, connectivity)
		.description("Specifies hostname for InfluxDB instance.").defaultValue("influxdb").build()));
	builder.attribute((new AttributeNode.Builder("Port", "port", AttributeType.Integer, connectivity)
		.description("Port on which InfluxDB is running").defaultValue("${influxdb.port:8086}").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String, connectivity)
		.description("Username for InfluxDB authentication.").defaultValue("root").build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String, connectivity)
		.description("Password for InfluxDB authentication.").defaultValue("root").build()));
	builder.attribute((new AttributeNode.Builder("Database", "database", AttributeType.String, connectivity)
		.description("InfluxDB database name.").defaultValue("sitewhere").build()));
	builder.attribute(
		(new AttributeNode.Builder("Retention policy", "retention", AttributeType.String, connectivity)
			.description("InfluxDB retention policy name.").defaultValue("autogen").build()));
	builder.attribute((new AttributeNode.Builder("Log level", "logLevel", AttributeType.String, connectivity)
		.description("Log level for debugging InfluxDB interactions.").choice("None", "none")
		.choice("Basic", "basic").choice("Headers", "headers").choice("Full", "full").defaultValue("none")
		.build()));

	// Batch attributes.
	builder.attribute(
		(new AttributeNode.Builder("Enable batch processing", "enableBatch", AttributeType.Boolean, batch)
			.description("Enable delivery of events in batches.").defaultValue("true").build()));
	builder.attribute(
		(new AttributeNode.Builder("Max batch chunk size", "batchChunkSize", AttributeType.Integer, batch)
			.description("Maximum number of events to send in a batch.").defaultValue("1000").build()));
	builder.attribute(
		(new AttributeNode.Builder("Max batch send interval (ms)", "batchIntervalMs", AttributeType.Integer,
			batch).description("Maximum amount of time (in ms) to wait before sending a batch.")
				.defaultValue("100").build()));
    }

    /**
     * Adds Apache Cassandra configuration attributes.
     * 
     * @param builder
     * @param connectivity
     */
    public static void addCassandraAttributes(ElementNode.Builder builder, IAttributeGroup connectivity) {
	builder.attribute(
		(new AttributeNode.Builder("Contact Points", "contactPoints", AttributeType.String, connectivity)
			.description("Comma-delimited list of contact points for Cassandra cluster.")
			.defaultValue("${cassandra.contact.points:cassandra}").build()));
	builder.attribute((new AttributeNode.Builder("Keyspace", "keyspace", AttributeType.String, connectivity)
		.description("Keyspace used for accessing data.").defaultValue("${cassandra.keyspace:sitewhere}")
		.build()));
    }
}