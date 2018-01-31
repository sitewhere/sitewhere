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
 * 
 * @author Derek
 */
public interface InstanceManagementBeans {

    /** Bean id base for MongoDB configurations */
    public static final String BEAN_MONGO_CONFIGURATION_BASE = "mongodb_";

    /** Bean id base for InfluxDB configurations */
    public static final String BEAN_INFLUX_CONFIGURATION_BASE = "influxdb_";

    /** Suffix used for default configuration */
    public static final String BEAN_SOLR_SUFFIX_DEFAULT = "_default";

    /** Bean id base for Solr configurations */
    public static final String BEAN_SOLR_CONFIGURATION_BASE = "solr_";

    /** Bean id for default Solr configuration */
    public static final String BEAN_SOLR_CONFIGURATION_DEFAULT = BEAN_SOLR_CONFIGURATION_BASE
	    + BEAN_SOLR_SUFFIX_DEFAULT;
}