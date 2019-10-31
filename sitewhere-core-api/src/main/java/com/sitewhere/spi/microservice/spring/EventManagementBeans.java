/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.spring;

/**
 * Spring bean names for event management configuration components.
 */
public class EventManagementBeans {

    /** Bean id for MongoDB client */
    public static final String BEAN_MONGODB_CLIENT = "mongoClient";

    /** Bean id for InfluxDB client */
    public static final String BEAN_INFLUXDB_CLIENT = "influxClient";

    /** Bean id for Apache Cassandra client */
    public static final String BEAN_CASSANDRA_CLIENT = "cassandraClient";

    /** Bean id for event management in server configuration */
    public static final String BEAN_EVENT_MANAGEMENT = "eventManagement";
}