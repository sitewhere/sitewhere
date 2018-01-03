/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for outbound connectors microservice.
 * 
 * @author Derek
 */
public enum OutboundConnectorsRoles implements IConfigurationRoleProvider {

    // /** Siddhi query. Callback. */
    // OutboundProcessingChain_SiddhiCallback(ConfigurationRole.build("Siddhi
    // Callbacks", true, true, true)),
    //
    // /** Siddhi event processsor. Siddhi query. */
    // OutboundProcessingChain_SiddhiQuery(ConfigurationRole.build("Siddhi Queries",
    // true, true, true,
    // new IConfigurationRoleProvider[] { OutboundProcessingChain_SiddhiCallback
    // })),
    //
    // /** Outbound processing chain. Siddhi event processor. */
    // OutboundProcessingChain_SiddhiEventProcessor(ConfigurationRole.build("Siddhi
    // Event Processor", true, true, true,
    // new IConfigurationRoleProvider[] { OutboundProcessingChain_SiddhiQuery,
    // OutboundProcessingChain_Filters })),
    //

    /** Root outbound connectors role. */
    OutboundConnectors(
	    ConfigurationRole.build(OutboundConnectorsRoleKeys.OutboundConnectors, "Outbound Connectors", false, false,
		    false, new IRoleKey[] { OutboundConnectorsRoleKeys.OutboundConnector }, new IRoleKey[0], true)),

    /** Outbound connector. */
    OutboundConnector(ConfigurationRole.build(OutboundConnectorsRoleKeys.OutboundConnector, "Outbound Connector", true,
	    true, true, new IRoleKey[0],
	    new IRoleKey[] { OutboundConnectorsRoleKeys.FilteredConnector,
		    OutboundConnectorsRoleKeys.ZoneTestEventProcessor, OutboundConnectorsRoleKeys.MqttConnector,
		    OutboundConnectorsRoleKeys.RabbitMqConnector })),

    /** Filtered connector. */
    FilteredConnector(ConfigurationRole.build(OutboundConnectorsRoleKeys.FilteredConnector, "Filtered Connector", true,
	    true, true, new IRoleKey[] { OutboundConnectorsRoleKeys.Filters })),

    /** Processor filter criteria. */
    Filters(ConfigurationRole.build(OutboundConnectorsRoleKeys.Filters, "Filters", true, false, false,
	    new IRoleKey[] { OutboundConnectorsRoleKeys.OutboundFilters })),

    /** Processor filters. */
    OutboundFilters(
	    ConfigurationRole.build(OutboundConnectorsRoleKeys.OutboundFilters, "Outbound Filter", true, true, true)),

    /** Zone test event processor. */
    ZoneTestEventProcessor(ConfigurationRole.build(OutboundConnectorsRoleKeys.ZoneTestEventProcessor,
	    "Zone Test Event Processor", true, true, true,
	    new IRoleKey[] { OutboundConnectorsRoleKeys.ZoneTestElement, OutboundConnectorsRoleKeys.OutboundFilters })),

    /** Zone test. */
    ZoneTest(ConfigurationRole.build(OutboundConnectorsRoleKeys.ZoneTestElement, "Zone Test", true, true, true)),

    /** MQTT event processor. */
    MqttEventProcessor(ConfigurationRole.build(OutboundConnectorsRoleKeys.MqttConnector, "MQTT Connector", true, true,
	    true, new IRoleKey[] { OutboundConnectorsRoleKeys.Filters, OutboundConnectorsRoleKeys.RouteBuilder })),

    /** MQTT event processsor. Route builder. */
    RouteBuilder(ConfigurationRole.build(OutboundConnectorsRoleKeys.RouteBuilder, "Route Builder", true, false, false)),

    /** Outbound processing chain. RabbitMQ event processor. */
    RabbitMqEventProcessor(ConfigurationRole.build(OutboundConnectorsRoleKeys.RabbitMqConnector, "RabbitMQ Connector",
	    true, true, true,
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