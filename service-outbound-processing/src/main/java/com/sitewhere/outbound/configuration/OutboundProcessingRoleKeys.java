/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.configuration;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum OutboundProcessingRoleKeys implements IRoleKey {

    /** Outbound processing */
    OutboundProcessing("out_prc"),

    /** Outbound event processors */
    OutboundEventProcessor("evt_prc"),

    /** Command delivery event processor */
    CommandDeliveryEventProcessor("cmd_dlv_prc"),

    /** Zone test event processor */
    ZoneTestEventProcessor("zon_tst_prc"),

    /** Zone test event processor element */
    ZoneTestElement("zon_tst_elm"),

    /** Groovy route builder */
    GroovyRouteBuilder("grvy_rte_bld"),

    /** MQTT event processor */
    MqttEventProcessor("mqtt_proc"),

    /** Route builder */
    RouteBuilder("route_bldr"),

    /** RabbitMQ event processor */
    RabbitMqEventProcessor("rbtmq_proc"),

    /** Filtered event processor */
    FilteredEventProcessor("filter_proc"),

    /** Filters list */
    Filters("filters"), OutboundFilters("ob_filters");

    private String id;

    private OutboundProcessingRoleKeys(String id) {
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