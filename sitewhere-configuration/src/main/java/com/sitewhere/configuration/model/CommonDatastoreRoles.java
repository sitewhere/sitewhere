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
	    new IRoleKey[] { CommonDatastoreRoleKeys.DeviceManagementDatastore,
		    CommonDatastoreRoleKeys.EventManagementDatastore },
	    new IRoleKey[0])),

    /** Device management datastore */
    DeviceManagementDatastore(
	    ConfigurationRole.build(CommonDatastoreRoleKeys.DeviceManagementDatastore, "Datastore", false, false, false,
		    new IRoleKey[] { CommonDatastoreRoleKeys.DeviceManagementDatastoreElement }, new IRoleKey[0])),

    /** Elements that can be added to a device management datastore */
    DeviceManagementDatastoreElement(ConfigurationRole.build(CommonDatastoreRoleKeys.DeviceManagementDatastoreElement,
	    "Datastore", false, false, false, new IRoleKey[0],
	    new IRoleKey[] { CommonDatastoreRoleKeys.MongoDBDatastore, CommonDatastoreRoleKeys.MongoDBReference,
                CommonDatastoreRoleKeys.RDBDatastore, CommonDatastoreRoleKeys.RDBReference })),

    /** Event management datastore */
    EventManagementDatastore(
	    ConfigurationRole.build(CommonDatastoreRoleKeys.EventManagementDatastore, "Datastore", false, false, false,
		    new IRoleKey[] { CommonDatastoreRoleKeys.EventManagementDatastoreElement }, new IRoleKey[0])),

    /** Elements that can be added to a event management datastore */
    EventManagementDatastoreElement(ConfigurationRole.build(CommonDatastoreRoleKeys.EventManagementDatastoreElement,
	    "Datastore", false, false, false, new IRoleKey[0],
	    new IRoleKey[] { CommonDatastoreRoleKeys.MongoDBDatastore, CommonDatastoreRoleKeys.MongoDBReference,
		    CommonDatastoreRoleKeys.InfluxDBDatastore, CommonDatastoreRoleKeys.InfluxDBReference,
		    CommonDatastoreRoleKeys.CassandraDatastore, CommonDatastoreRoleKeys.CassandraReference })),

    /** Device state management datastore */
    DeviceStateManagementDatastore(ConfigurationRole.build(CommonDatastoreRoleKeys.DeviceStateManagementDatastore,
	    "Datastore", false, false, false,
	    new IRoleKey[] { CommonDatastoreRoleKeys.DeviceStateManagementDatastoreElement }, new IRoleKey[0])),

    /** Elements that can be added to a device state management datastore */
    DeviceStateManagementDatastoreElement(ConfigurationRole.build(
	    CommonDatastoreRoleKeys.DeviceStateManagementDatastoreElement, "Datastore", false, false, false,
	    new IRoleKey[0],
	    new IRoleKey[] { CommonDatastoreRoleKeys.MongoDBDatastore, CommonDatastoreRoleKeys.MongoDBReference })),

    /** MongoDB datastore */
    MongoDBDatastore(ConfigurationRole.build(CommonDatastoreRoleKeys.MongoDBDatastore, "MongoDB Datastore", false,
	    false, false)),

    /** MongoDB global reference */
    MongoDBReference(ConfigurationRole.build(CommonDatastoreRoleKeys.MongoDBReference, "MongoDB Global Reference",
	    false, false, false)),


    //-------------------
    /** RDB datastore */
    RDBDatastore(ConfigurationRole.build(CommonDatastoreRoleKeys.RDBDatastore, "RDB Datastore", false,
            false, false)),

    /** MongoDB global reference */
    RDBReference(ConfigurationRole.build(CommonDatastoreRoleKeys.RDBReference, "RDB Global Reference",
            false, false, false)),
    //-------------------


    /** InfluxDB datastore */
    InfluxDBDatastore(ConfigurationRole.build(CommonDatastoreRoleKeys.InfluxDBDatastore, "InfluxDB Datastore", false,
	    false, false)),

    /** InfluxDB global reference */
    InfluxDBReference(ConfigurationRole.build(CommonDatastoreRoleKeys.InfluxDBReference, "InfluxDB Global Reference",
	    false, false, false)),

    /** Cassandra datastore */
    CassandraDatastore(ConfigurationRole.build(CommonDatastoreRoleKeys.CassandraDatastore, "Cassandra Datastore", false,
	    false, false)),

    /** Cassandra global reference */
    CassandraReference(ConfigurationRole.build(CommonDatastoreRoleKeys.CassandraReference, "Cassandra Global Reference",
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