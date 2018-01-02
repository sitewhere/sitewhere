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
	    false, false, false, new IRoleKey[] { InstanceManagementRoleKeys.PersistenceConfigurations },
	    new IRoleKey[0], true)),

    /** Persistence configurations. */
    PersistenceConfigurations(ConfigurationRole.build(InstanceManagementRoleKeys.PersistenceConfigurations,
	    "Persistence Configurations", false, false, false,
	    new IRoleKey[] { InstanceManagementRoleKeys.PersistenceConfigurationsElement })),

    /** Persistence configurations element. */
    PersistenceConfigurationsElement(ConfigurationRole.build(
	    InstanceManagementRoleKeys.PersistenceConfigurationsElement, "Persistence Configuration", true, true, true,
	    new IRoleKey[0], new IRoleKey[] { InstanceManagementRoleKeys.MongoDBConfigurations })),

    /** MongoDB persistence configurations. */
    MongoDBConfigurations(ConfigurationRole.build(InstanceManagementRoleKeys.MongoDBConfigurations,
	    "MongoDB Persistence Configuration", true, false, false,
	    new IRoleKey[] { InstanceManagementRoleKeys.MongoDBConfiguration })),

    /** MongoDB datastore configuration. */
    MongoDBConfiguration(ConfigurationRole.build(InstanceManagementRoleKeys.MongoDBConfiguration,
	    "MongoDB Configuration", false, false, false)),

    /** Connector configurations. */
    ConnectorConfigurations(
	    ConfigurationRole.build(InstanceManagementRoleKeys.ConnectorConfigurations, "Connector Configurations",
		    false, false, false, new IRoleKey[] { InstanceManagementRoleKeys.ConnectorConfigurationsElement })),

    /** Connector configurations element. */
    ConnectorConfigurationsElement(ConfigurationRole.build(InstanceManagementRoleKeys.ConnectorConfigurationsElement,
	    "Connector Configuration", true, true, true, new IRoleKey[0],
	    new IRoleKey[] { InstanceManagementRoleKeys.SolrConfigurations })),

    /** Solr connector configurations. */
    SolrConfigurations(ConfigurationRole.build(InstanceManagementRoleKeys.SolrConfigurations, "Solr Configuration",
	    true, false, false, new IRoleKey[] { InstanceManagementRoleKeys.SolrConfiguration })),

    /** Solr connector configuration. */
    SolrConfiguration(ConfigurationRole.build(InstanceManagementRoleKeys.SolrConfiguration, "Solr Configuration", false,
	    false, false));

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