/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.datastore;

/**
 * Enumerates datastore configuration types.
 * 
 * @author Derek
 */
public enum DatastoreConfigurationType {

    // Custom MongoDB configuration.
    MongoDB,

    // Reference to global MongoDB configuration.
    MongoDBReference,

    // Custom InfluxDB configuration.
    InfluxDB,

    // Reference to global InfluxDB configuration.
    InfluxDBReference,

    // Custom Cassandra configuration.
    Cassandra,

    // Reference to global Cassandra configuration.
    CassandraReference,

    // Custom MongoDB configuration.
    RDB,

    // Reference to global MongoDB configuration.
    RDBReference;
}