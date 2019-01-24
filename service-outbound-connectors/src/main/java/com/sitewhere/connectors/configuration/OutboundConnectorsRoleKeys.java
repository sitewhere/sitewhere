/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.configuration;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum OutboundConnectorsRoleKeys implements IRoleKey {

    /** Outbound connectors */
    OutboundConnectors("out_conns"),

    /** Outbound connector */
    OutboundConnector("out_conn"),

    /** Groovy route builder */
    GroovyRouteBuilder("grvy_rte_bld"),

    /** Solr connector */
    SolrConnector("solr_conn"),

    /** MQTT connector */
    MqttConnector("mqtt_conn"),

    /** Route builder */
    RouteBuilder("route_bldr"),

    /** RabbitMQ connector */
    RabbitMqConnector("rbtmq_conn"),

    /** HTTP connector */
    HttpConnector("http_conn"),

    /** URI builder */
    UriBuilder("uri_builder"),

    /** Payload builder */
    PayloadBuilder("payload_builder"),

    /** Filtered connector */
    FilteredConnector("filter_conn"),

    /** Filters list */
    Filters("filters"),

    /** Single filter instance */
    OutboundFilter("filter");

    private String id;

    private OutboundConnectorsRoleKeys(String id) {
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