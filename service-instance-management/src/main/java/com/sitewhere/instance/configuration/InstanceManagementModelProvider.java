/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.configuration;

import com.sitewhere.configuration.CommonConnectorModel;
import com.sitewhere.configuration.model.CommonDatastoreProvider;
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

	// MongoDB persistence configurations.
	addElement(createMongoDBPersistenceConfigurationsElement());
	addElement(createMongoConfigurationElement());

	// RDB persistence configurations.
	addElement(createRDBPersistenceConfigurationsElement());
	addElement(createRDBConfigurationElement());

	// InfluxDB persistence configurations.
	addElement(createInfluxDBPersistenceConfigurationsElement());
	addElement(createInfluxDBConfigurationElement());

	// Apache Cassandra persistence configurations.
	addElement(createCassandraPersistenceConfigurationsElement());
	addElement(createCassandraConfigurationElement());

	// Connector configurations.
	addElement(createConnectorConfigurationsElement());
	addElement(createSolrConnectorConfigurationsElement());
	addElement(createSolrConfigurationElement());
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

	builder.description("Provides global MongoDB persistence configurations that can be reused in tenants.");

	return builder.build();
    }

    /**
     * Create element configuration for MongoDB settings.
     * 
     * @return
     */
    protected ElementNode createMongoConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.MongoDBConfiguration.getRole().getName(),
		IInstanceManagementParser.MongoDbElements.MongoConfiguration.getLocalName(), "database",
		InstanceManagementRoleKeys.MongoDBConfiguration, this);

	builder.description("Global configuration for MongoDB data persistence.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
		.description("Unique id for referencing configuration.").makeIndex().makeRequired().build()));
	CommonDatastoreProvider.addMongoDbAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	return builder.build();
    }


    /**
     * Create RDB persistence configurations element.
     *
     * @return
     */
    protected ElementNode createRDBPersistenceConfigurationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.RDBConfigurations.getRole().getName(),
		IInstanceManagementParser.PersistenceConfigurationsElements.RDBConfigurations.getLocalName(),
		"database", InstanceManagementRoleKeys.RDBConfigurations, this);

	builder.description("Provides global RDB persistence configurations that can be reused in tenants.");

	return builder.build();
    }

    /**
     * Create element configuration for RDB settings.
     *
     * @return
     */
    protected ElementNode createRDBConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.RDBConfigurations.getRole().getName(),
		IInstanceManagementParser.RDBElements.RDBConfiguration.getLocalName(), "database",
		InstanceManagementRoleKeys.RDBConfiguration, this);

	builder.description("Global configuration for RDB data persistence.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
		.description("Unique id for referencing configuration.").makeIndex().makeRequired().build()));
	CommonDatastoreProvider.addRDBAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	return builder.build();
    }


    /**
     * Create InfluxDB persistence configurations element.
     * 
     * @return
     */
    protected ElementNode createInfluxDBPersistenceConfigurationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.InfluxDBConfigurations.getRole().getName(),
		IInstanceManagementParser.PersistenceConfigurationsElements.InfluxConfigurations.getLocalName(),
		"database", InstanceManagementRoleKeys.InfluxDBConfigurations, this);

	builder.description("Provides global InfluxDB persistence configurations that can be reused in tenants.");

	return builder.build();
    }

    /**
     * Create element configuration for InfluxDB settings.
     * 
     * @return
     */
    protected ElementNode createInfluxDBConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.InfluxDBConfiguration.getRole().getName(),
		IInstanceManagementParser.InfluxDbElements.InfluxConfiguration.getLocalName(), "database",
		InstanceManagementRoleKeys.InfluxDBConfiguration, this);

	builder.description("Global configuration for InfluxDB data persistence.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_BATCH);

	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
		.description("Unique id for referencing configuration.").makeIndex().makeRequired().build()));
	CommonDatastoreProvider.addInfluxDbAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY,
		ConfigurationModelProvider.ATTR_GROUP_BATCH);

	return builder.build();
    }

    /**
     * Create Apache Cassandra persistence configurations element.
     * 
     * @return
     */
    protected ElementNode createCassandraPersistenceConfigurationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.CassandraConfigurations.getRole().getName(),
		IInstanceManagementParser.PersistenceConfigurationsElements.CassandraConfigurations.getLocalName(),
		"database", InstanceManagementRoleKeys.CassandraConfigurations, this);

	builder.description(
		"Provides global Apache Cassandra persistence configurations that can be reused in tenants.");

	return builder.build();
    }

    /**
     * Create element configuration for Apache Cassandra settings.
     * 
     * @return
     */
    protected ElementNode createCassandraConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.CassandraConfiguration.getRole().getName(),
		IInstanceManagementParser.CassandraElements.CassandraConfiguration.getLocalName(), "database",
		InstanceManagementRoleKeys.CassandraConfiguration, this);

	builder.description("Global configuration for Apache Cassandra data persistence.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
		.description("Unique id for referencing configuration.").makeIndex().makeRequired().build()));
	CommonDatastoreProvider.addCassandraAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	return builder.build();
    }

    /**
     * Create connector configurations element.
     * 
     * @return
     */
    protected ElementNode createConnectorConfigurationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.ConnectorConfigurations.getRole().getName(),
		IInstanceManagementParser.TopLevelElements.ConnectorConfigurations.getLocalName(), "plug",
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
		IInstanceManagementParser.ConnectorConfigurationsElements.SolrConfigurations.getLocalName(),
		"search-plus", InstanceManagementRoleKeys.SolrConfigurations, this);

	builder.description("Provides Solr configurations that can be reused in tenants.");

	return builder.build();
    }

    /**
     * Create element which defines an Apache Solr configuration.
     * 
     * @return
     */
    protected ElementNode createSolrConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.SolrConfiguration.getRole().getName(),
		IInstanceManagementParser.SolrElements.SolrConfiguration.getLocalName(), "search-plus",
		InstanceManagementRoleKeys.SolrConfiguration, this);

	builder.description("Provides Solr configuration that may be referenced in Solr connectors.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
		.description("Unique id for referencing configuration.").makeIndex().makeRequired().build()));
	CommonConnectorModel.addSolrConnectivityAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	return builder.build();
    }
}