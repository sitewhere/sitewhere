/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Common datastore roles used by microservices.
 * 
 * @author Derek
 */
public enum CommonDatastoreRoles implements IConfigurationRoleProvider {

    /** Top-level common role. */
    Datastore(ConfigurationRole.build(CommonDatastoreRoleKeys.Datastore, "Datastore", false, false, false,
	    new IRoleKey[] { CommonDatastoreRoleKeys.DeviceManagementDatastore }, new IRoleKey[0])),

    /** Device management datastore */
    DeviceManagementDatastore(
	    ConfigurationRole.build(CommonDatastoreRoleKeys.DeviceManagementDatastore, "Datastore", false, false, false,
		    new IRoleKey[] { CommonDatastoreRoleKeys.DeviceManagementDatastoreElement }, new IRoleKey[0])),

    /** Elements that can be added to a device management datastore */
    DeviceManagementDatastoreElement(ConfigurationRole.build(CommonDatastoreRoleKeys.DeviceManagementDatastoreElement,
	    "Datastore", false, false, false, new IRoleKey[0],
	    new IRoleKey[] { CommonDatastoreRoleKeys.MongoDBDatastore, CommonDatastoreRoleKeys.MongoDBReference })),

    /** MongoDB datastore */
    MongoDBDatastore(ConfigurationRole.build(CommonDatastoreRoleKeys.MongoDBDatastore, "MongoDB Datastore", false,
	    false, false)),

    /** MongoDB global reference */
    MongoDBReference(ConfigurationRole.build(CommonDatastoreRoleKeys.MongoDBReference, "MongoDB Global Reference",
	    false, false, false));

    private ConfigurationRole role;

    private CommonDatastoreRoles(ConfigurationRole role) {
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