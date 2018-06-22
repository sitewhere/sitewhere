/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Component roles associated with instance management microservice.
 * 
 * @author Derek
 */
public enum InstanceManagementRoles implements IConfigurationRoleProvider {

    /** Root instance management role. */
    InstanceManagement(ConfigurationRole.build(InstanceManagementRoleKeys.InstanceManagement, "Instance Management",
	    false, false, false, new IRoleKey[] { InstanceManagementRoleKeys.PersistenceConfigurations,
		    InstanceManagementRoleKeys.ConnectorConfigurations },
	    new IRoleKey[0], true)),

    /** Persistence configurations. */
    PersistenceConfigurations(ConfigurationRole.build(InstanceManagementRoleKeys.PersistenceConfigurations,
	    "Persistence Configurations", false, false, false,
	    new IRoleKey[] { InstanceManagementRoleKeys.MongoDBConfigurations,
		    InstanceManagementRoleKeys.InfluxDBConfigurations,
		    InstanceManagementRoleKeys.CassandraConfigurations })),

    /** MongoDB persistence configurations. */
    MongoDBConfigurations(ConfigurationRole.build(InstanceManagementRoleKeys.MongoDBConfigurations,
	    "MongoDB Persistence Configurations", true, false, false,
	    new IRoleKey[] { InstanceManagementRoleKeys.MongoDBConfiguration })),

    /** MongoDB datastore configuration. */
    MongoDBConfiguration(ConfigurationRole.build(InstanceManagementRoleKeys.MongoDBConfiguration,
	    "MongoDB Configuration", true, true, true)),

    /** InfluxDB persistence configurations. */
    InfluxDBConfigurations(ConfigurationRole.build(InstanceManagementRoleKeys.InfluxDBConfigurations,
	    "InfluxDB Persistence Configurations", true, false, false,
	    new IRoleKey[] { InstanceManagementRoleKeys.InfluxDBConfiguration })),

    /** InfluxDB datastore configuration. */
    InfluxDBConfiguration(ConfigurationRole.build(InstanceManagementRoleKeys.InfluxDBConfiguration,
	    "InfluxDB Configuration", true, true, true)),

    /** Apache Cassandra persistence configurations. */
    CassandraConfigurations(ConfigurationRole.build(InstanceManagementRoleKeys.CassandraConfigurations,
	    "Apache Cassandra Persistence Configurations", true, false, false,
	    new IRoleKey[] { InstanceManagementRoleKeys.CassandraConfiguration })),

    /** Apache Cassandra datastore configuration. */
    CassandraConfiguration(ConfigurationRole.build(InstanceManagementRoleKeys.CassandraConfiguration,
	    "Apache Cassandra Configuration", true, true, true)),

    /** Connector configurations. */
    ConnectorConfigurations(
	    ConfigurationRole.build(InstanceManagementRoleKeys.ConnectorConfigurations, "Connector Configurations",
		    false, false, false, new IRoleKey[] { InstanceManagementRoleKeys.SolrConfigurations })),

    /** Solr connector configurations. */
    SolrConfigurations(
	    ConfigurationRole.build(InstanceManagementRoleKeys.SolrConfigurations, "Solr Connector Configurations",
		    true, false, false, new IRoleKey[] { InstanceManagementRoleKeys.SolrConfiguration })),

    /** Solr connector alternate configuration. */
    SolrConfiguration(ConfigurationRole.build(InstanceManagementRoleKeys.SolrConfiguration, "Solr Configuration", true,
	    true, true));

    private ConfigurationRole role;

    private InstanceManagementRoles(ConfigurationRole role) {
	this.role = role;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider
     * #getRole()
     */
    @Override
    public IConfigurationRole getRole() {
	return role;
    }
}