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

	// InfluxDB persistence configurations.
	addElement(createInfluDBPersistenceConfigurationsElement());
	addElement(createInfluxConfigurationElement());

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

	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String)
		.description("Unique id for referencing configuration.").makeIndex().makeRequired().build()));
	CommonDatastoreProvider.addMongoDbAttributes(builder);

	return builder.build();
    }

    /**
     * Create InfluxDB persistence configurations element.
     * 
     * @return
     */
    protected ElementNode createInfluDBPersistenceConfigurationsElement() {
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
    protected ElementNode createInfluxConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.InfluxDBConfiguration.getRole().getName(),
		IInstanceManagementParser.InfluxDbElements.InfluxConfiguration.getLocalName(), "database",
		InstanceManagementRoleKeys.InfluxDBConfiguration, this);

	builder.description("Global configuration for InfluxDB data persistence.");

	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String)
		.description("Unique id for referencing configuration.").makeIndex().makeRequired().build()));
	CommonDatastoreModel.addInfluxDbAttributes(builder);

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
     * Create element which defines the default Apache Solr configuration.
     * 
     * @return
     */
    protected ElementNode createDefaultSolrConfigurationElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		InstanceManagementRoles.DefaultSolrConfiguration.getRole().getName(),
		IInstanceManagementParser.SolrElements.DefaultSolrConfiguration.getLocalName(), "search-plus",
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
		IInstanceManagementParser.SolrElements.AlternateSolrConfiguration.getLocalName(), "search-plus",
		InstanceManagementRoleKeys.AltSolrConfiguration, this);

	builder.description("Provides alternate Solr configuration for tenants.");
	builder.attribute((new AttributeNode.Builder("Id", "id", AttributeType.String)
		.description("Unique id for referencing configuration.").makeIndex().makeRequired().build()));
	CommonConnectorModel.adSolrConnectivityAttributes(builder);

	return builder.build();
    }
}