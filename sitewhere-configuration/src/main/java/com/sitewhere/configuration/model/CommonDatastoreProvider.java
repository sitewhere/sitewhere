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
	addElement(createMongoDbDatastoreElement());
	addElement(createMongoDbReferenceElement());
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
     * Create MongoDB alternate datastore element.
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
     * Create MongoDB alternate datastore element.
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
}