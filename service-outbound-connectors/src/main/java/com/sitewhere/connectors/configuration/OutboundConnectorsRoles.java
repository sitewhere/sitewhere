/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.configuration.model.CommonConnectorRoleKeys;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for outbound connectors microservice.
 * 
 * @author Derek
 */
public enum OutboundConnectorsRoles implements IConfigurationRoleProvider {

    /** Root outbound connectors role. */
    OutboundConnectors(
	    ConfigurationRole.build(OutboundConnectorsRoleKeys.OutboundConnectors, "Outbound Connectors", false, false,
		    false, new IRoleKey[] { OutboundConnectorsRoleKeys.OutboundConnector }, new IRoleKey[0], true)),

    /** Outbound connector. */
    OutboundConnector(ConfigurationRole.build(OutboundConnectorsRoleKeys.OutboundConnector, "Outbound Connector", true,
	    true, true, new IRoleKey[0],
	    new IRoleKey[] { OutboundConnectorsRoleKeys.FilteredConnector, OutboundConnectorsRoleKeys.MqttConnector,
		    OutboundConnectorsRoleKeys.SolrConnector, OutboundConnectorsRoleKeys.RabbitMqConnector })),

    /** Filtered connector. */
    FilteredConnector(ConfigurationRole.build(OutboundConnectorsRoleKeys.FilteredConnector, "Filtered Connector", true,
	    true, true, new IRoleKey[] { OutboundConnectorsRoleKeys.Filters })),

    /** Processor filter criteria. */
    Filters(ConfigurationRole.build(OutboundConnectorsRoleKeys.Filters, "Filters", true, false, false,
	    new IRoleKey[] { OutboundConnectorsRoleKeys.OutboundFilter })),

    /** Processor filter. */
    OutboundFilter(
	    ConfigurationRole.build(OutboundConnectorsRoleKeys.OutboundFilter, "Outbound Filter", true, true, true)),

    /** Solr outbound connector. */
    SolrConnector(ConfigurationRole.build(OutboundConnectorsRoleKeys.SolrConnector, "Solr Connector", true, true, true,
	    new IRoleKey[] { CommonConnectorRoleKeys.SolrConfigurationChoice, OutboundConnectorsRoleKeys.Filters })),

    /** MQTT outbound connector. */
    MqttConnector(ConfigurationRole.build(OutboundConnectorsRoleKeys.MqttConnector, "MQTT Connector", true, true, true,
	    new IRoleKey[] { OutboundConnectorsRoleKeys.Filters, OutboundConnectorsRoleKeys.RouteBuilder })),

    /** MQTT outbound connector. Route builder. */
    RouteBuilder(ConfigurationRole.build(OutboundConnectorsRoleKeys.RouteBuilder, "Route Builder", true, false, false)),

    /** RabbitMQ outbound connector. */
    RabbitMqConnector(ConfigurationRole.build(OutboundConnectorsRoleKeys.RabbitMqConnector, "RabbitMQ Connector", true,
	    true, true,
	    new IRoleKey[] { OutboundConnectorsRoleKeys.Filters, OutboundConnectorsRoleKeys.RouteBuilder }));

    private ConfigurationRole role;

    private OutboundConnectorsRoles(ConfigurationRole role) {
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