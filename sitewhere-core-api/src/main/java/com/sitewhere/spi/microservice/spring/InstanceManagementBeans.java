/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.spring;

/**
 * Spring bean names for global instance configuration components.
 */
public interface InstanceManagementBeans {

    /** Bean id base for MongoDB configurations */
    public static final String BEAN_MONGO_CONFIGURATION_BASE = "mongodb_";

    /** Bean id base for InfluxDB configurations */
    public static final String BEAN_INFLUX_CONFIGURATION_BASE = "influxdb_";

    /** Bean id base for Cassandra configurations */
    public static final String BEAN_CASSANDRA_CONFIGURATION_BASE = "cassandra_";

    /** Bean id base for Solr configurations */
    public static final String BEAN_SOLR_CONFIGURATION_BASE = "solr_";
}