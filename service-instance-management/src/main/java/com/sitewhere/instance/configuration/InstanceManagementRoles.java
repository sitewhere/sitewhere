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
	    false, false, false, new IRoleKey[] { InstanceManagementRoleKeys.DatastoreConfiguration }, new IRoleKey[0],
	    true)),

    /** Datastore configuration. */
    DatastoreConfiguration(
	    ConfigurationRole.build(InstanceManagementRoleKeys.DatastoreConfiguration, "Datastore Configuration", false,
		    false, false, new IRoleKey[0], new IRoleKey[] { InstanceManagementRoleKeys.MongoDBConfiguration })),

    /** MongoDB datastore configuration. */
    MongoDBConfiguration(
	    ConfigurationRole.build(InstanceManagementRoleKeys.MongoDBConfiguration, "MongoDB Datastore Configuration",
		    true, false, false, new IRoleKey[] { InstanceManagementRoleKeys.DefaultMongoDBConfiguration,
			    InstanceManagementRoleKeys.AlternateMongoDBConfiguration })),

    /** Default MongoDB datastore configuration. */
    DefaultMongoDBConfiguration(ConfigurationRole.build(InstanceManagementRoleKeys.DefaultMongoDBConfiguration,
	    "Default MongoDB Configuration", false, false, false)),

    /** Alternate MongoDB datastore configuration. */
    AlternateMongoDBConfiguration(ConfigurationRole.build(InstanceManagementRoleKeys.AlternateMongoDBConfiguration,
	    "Alternate MongoDB Configuration", true, true, true));

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