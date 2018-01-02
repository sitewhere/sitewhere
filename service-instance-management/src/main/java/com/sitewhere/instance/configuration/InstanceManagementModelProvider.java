/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.configuration;

import com.sitewhere.configuration.CommonConnectorModel;
import com.sitewhere.configuration.CommonDatastoreModel;
import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.parser.IInstanceManagementParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for instance management microservice.
 * 
 * @author Derek
 */
public class InstanceManagementModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/instance-management";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return InstanceManagementRoles.InstanceManagement;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createInstanceManagementElement());

	// Persistence configurations.
	addElement(createPersistenceConfigurationsElement());
	addElement(createMongoDBPersistenceConfigurationsElement());
	addElement(createDefaultMongoConfigurationElement());
	addElement(createAlternateMongoConfigurationElement());

	// addElement(createMongoInfluxDbTenantDatastoreElement());
	// addElement(createHBaseTenantDatastoreElement());

	// Connector configurations.
	addElement(createConnectorConfigurationsElement());
	addElement(createSolrConnectorConfigurationsElement());
	addElement(createDefaultSolrConfigurationElement());
	addElement(createAlternateSolrConfigurationElement());
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (InstanceManagementRoles role : InstanceManagementRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /**
     * Create instance management element.
     * 
     * @return
     */
    protected ElementNode createInstanceManagementElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.InstanceManagement.getRole().getName(), IInstanceManagementParser.ROOT, "globe",
		InstanceManagementRoleKeys.InstanceManagement, this);

	builder.description("Handles global instance configuration.");

	return builder.build();
    }

    /**
     * Create persistence configurations element.
     * 
     * @return
     */
    protected ElementNode createPersistenceConfigurationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.PersistenceConfigurations.getRole().getName(),
		IInstanceManagementParser.TopLevelElements.PersistenceConfigurations.getLocalName(), "database",
		InstanceManagementRoleKeys.PersistenceConfigurations, this);

	builder.description("Provides global persistence configurations that can be reused in tenants.");

	return builder.build();
    }

    /**
     * Create MongoDB persistence configurations element.
     * 
     * @return
     */
    protected ElementNode createMongoDBPersistenceConfigurationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.MongoDBConfigurations.getRole().getName(),
		IInstanceManagementParser.PersistenceConfigurationsElements.MongoConfigurations.getLocalName(),
		"database", InstanceManagementRoleKeys.MongoDBConfigurations, this);

	builder.description("Provides global persistence configurations that can be reused in tenants.");

	return builder.build();
    }

    /**
     * Create element configuration for MonogoDB tenant datastore.
     * 
     * @return
     */
    protected ElementNode createDefaultMongoConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.DefaultMongoDBConfiguration.getRole().getName(),
		IInstanceManagementParser.MongoDbElements.DefaultMongoConfiguration.getLocalName(), "database",
		InstanceManagementRoleKeys.DefaultMongoDBConfiguration, this);

	builder.description("Default configuration for MongoDB data persistence.");
	CommonDatastoreModel.addMongoDbAttributes(builder);

	return builder.build();
    }

    /**
     * Create element configuration for MonogoDB tenant datastore.
     * 
     * @return
     */
    protected ElementNode createAlternateMongoConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.AltMongoDBConfiguration.getRole().getName(),
		IInstanceManagementParser.MongoDbElements.AlternateMongoConfiguration.getLocalName(), "database",
		InstanceManagementRoleKeys.AltMongoDBConfiguration, this);

	builder.description("Alternate configuration for MongoDB data persistence.");

	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String)
		.description("Unique id for referencing configuration.").build()));
	CommonDatastoreModel.addMongoDbAttributes(builder);

	return builder.build();
    }

    /**
     * Create element configuration for MonogoDB/InfluxDB tenant datastore.
     * 
     * @return
     */
    // protected ElementNode createMongoInfluxDbTenantDatastoreElement() {
    // ElementNode.Builder builder = new ElementNode.Builder("MongoDB/InfluxDB
    // Tenant Datastore",
    // ITenantDatastoreParser.Elements.MongoInfluxDbTenantDatastore.getLocalName(),
    // "database",
    // InstanceManagementRoleKeys.DefaultMongoDBConfiguration, this);
    //
    // builder.description("Store tenant master data using a MongoDB database and
    // store tenant event "
    // + "data in InfluxDB. Note that the global datastore must be configured to "
    // + "use MongoDB if this tenant datastore is to be used. Most core "
    // + "MongoDB settings are configured at the global level.");
    // builder.attributeGroup("conn", "InfluxDB Connectivity");
    // builder.attribute((new AttributeNode.Builder("Connection URL", "connectUrl",
    // AttributeType.String)
    // .description("Specifies URL used to connect to InfluxDB.").group("conn")
    // .defaultValue("http://localhost:8086").build()));
    // builder.attribute((new AttributeNode.Builder("Username", "username",
    // AttributeType.String)
    // .description("Username for InfluxDB
    // authentication.").group("conn").defaultValue("root").build()));
    // builder.attribute((new AttributeNode.Builder("Password", "password",
    // AttributeType.String)
    // .description("Password for InfluxDB
    // authentication.").group("conn").defaultValue("root").build()));
    // builder.attribute((new AttributeNode.Builder("Database", "database",
    // AttributeType.String)
    // .description("InfluxDB database
    // name.").group("conn").defaultValue("sitewhere").build()));
    // builder.attribute((new AttributeNode.Builder("Retention policy", "retention",
    // AttributeType.String)
    // .description("InfluxDB retention policy
    // name.").group("conn").defaultValue("autogen").build()));
    // builder.attribute((new AttributeNode.Builder("Log level", "logLevel",
    // AttributeType.String)
    // .description("Log level for debugging InfluxDB
    // interactions.").group("conn").choice("none")
    // .choice("basic").choice("headers").choice("full").defaultValue("none").build()));
    //
    // builder.attributeGroup("batch", "InfluxDB Batch Event Processing");
    // builder.attribute((new AttributeNode.Builder("Enable batch processing",
    // "enableBatch", AttributeType.Boolean)
    // .description("Enable delivery of events in
    // batches.").group("batch").defaultValue("true").build()));
    // builder.attribute((new AttributeNode.Builder("Max batch chunk size",
    // "batchChunkSize", AttributeType.Integer)
    // .description("Maximum number of events to send in a
    // batch.").group("batch").defaultValue("1000")
    // .build()));
    // builder.attribute(
    // (new AttributeNode.Builder("Max batch send interval (ms)", "batchIntervalMs",
    // AttributeType.Integer)
    // .description("Maximum amount of time (in ms) to wait before sending a
    // batch.").group("batch")
    // .defaultValue("100").build()));
    // return builder.build();
    // }

    /**
     * Create element configuration for HBase tenant datastore.
     * 
     * @return
     */
    // protected ElementNode createHBaseTenantDatastoreElement() {
    // ElementNode.Builder builder = new ElementNode.Builder("HBase Tenant
    // Datastore",
    // ITenantDatastoreParser.Elements.HBaseTenantDatastore.getLocalName(),
    // "database",
    // InstanceManagementRoleKeys.DefaultMongoDBConfiguration, this);
    // builder.description("Store tenant data using tables in an HBase instance.
    // Note that the "
    // + "global datastore must be configured to use HBase if this tenant datastore
    // is to "
    // + "be used. Most core HBase settings are configured at the global level.");
    // return builder.build();
    // }

    /**
     * Create connector configurations element.
     * 
     * @return
     */
    protected ElementNode createConnectorConfigurationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.ConnectorConfigurations.getRole().getName(),
		IInstanceManagementParser.TopLevelElements.ConnectorConfigurations.getLocalName(), "database",
		InstanceManagementRoleKeys.ConnectorConfigurations, this);

	builder.description("Provides global connector configurations that can be reused in tenants.");

	return builder.build();
    }

    /**
     * Create Solr connector configurations element.
     * 
     * @return
     */
    protected ElementNode createSolrConnectorConfigurationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.SolrConfigurations.getRole().getName(),
		IInstanceManagementParser.ConnectorConfigurationsElements.SolrConfigurations.getLocalName(), "database",
		InstanceManagementRoleKeys.SolrConfigurations, this);

	builder.description("Provides Solr configurations that can be reused in tenants.");

	return builder.build();
    }

    /**
     * Create element which defines the default Apache Solr configuration.
     * 
     * @return
     */
    protected ElementNode createDefaultSolrConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.DefaultSolrConfiguration.getRole().getName(),
		IInstanceManagementParser.SolrElements.DefaultSolrConfiguration.getLocalName(), "cogs",
		InstanceManagementRoleKeys.DefaultSolrConfiguration, this);

	builder.description("Provides default Solr configuration for tenants.");
	CommonConnectorModel.adSolrConnectivityAttributes(builder);

	return builder.build();
    }

    /**
     * Create element which defines an alternate Apache Solr configuration.
     * 
     * @return
     */
    protected ElementNode createAlternateSolrConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.AltSolrConfiguration.getRole().getName(),
		IInstanceManagementParser.SolrElements.AlternateSolrConfiguration.getLocalName(), "cogs",
		InstanceManagementRoleKeys.AltSolrConfiguration, this);

	builder.description("Provides alternate Solr configuration for tenants.");
	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String)
		.description("Unique id for referencing configuration.").build()));
	CommonConnectorModel.adSolrConnectivityAttributes(builder);

	return builder.build();
    }
}