/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum CommonDatastoreRoleKeys implements IRoleKey {

    /** Datastore roles */
    Datastore("datastore"),

    /** Device management datastores */
    DeviceManagementDatastore("dev_mgmt_ds"),

    /** Device management datastore element */
    DeviceManagementDatastoreElement("dev_mgmt_ds_elm"),

    /** MongoDB datastore */
    MongoDBDatastore("mongo_ds"),

    /** MongoDB global reference */
    MongoDBReference("mongo_ref"),

    /** Event management datastores */
    EventManagementDatastore("evt_mgmt_ds"),

    /** Event management datastore element */
    EventManagementDatastoreElement("evt_mgmt_ds_elm"),

    /** InfluxDB datastore */
    InfluxDBDatastore("influx_ds"),

    /** InfluxDB global reference */
    InfluxDBReference("influx_ref");

    private String id;

    private CommonDatastoreRoleKeys(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IRoleKey#getId()
     */
    @Override
    public String getId() {
	return id;
    }
}