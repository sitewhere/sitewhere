/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.configuration;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum InstanceManagementRoleKeys implements IRoleKey {

    /** Instance management */
    InstanceManagement("instance_mgmt"),

    /** Persistence configurations */
    PersistenceConfigurations("persist_confs"),

    /** Persistence configurations element */
    PersistenceConfigurationsElement("persist_conf_elm"),

    /** MongoDB persistence configurations */
    MongoDBConfigurations("mongo_confs"),

    /** MongoDB configuration element */
    MongoDBConfiguration("mongo_conf"),

    /** InfluxDB persistence configurations */
    InfluxDBConfigurations("influx_confs"),

    /** InfluxDB configuration element */
    InfluxDBConfiguration("influx_conf"),

    /** Connector configurations */
    ConnectorConfigurations("connector_confs"),

    /** Connector configuration element */
    ConnectorConfigurationsElement("conn_conf_elm"),

    /** Apache Solr connector configurations */
    SolrConfigurations("solr_confs"),

    /** Solr default configuration element */
    DefaultSolrConfiguration("def_solr_conf"),

    /** Solr alternate configuration element */
    AltSolrConfiguration("alt_solr_conf");

    private String id;

    private InstanceManagementRoleKeys(String id) {
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