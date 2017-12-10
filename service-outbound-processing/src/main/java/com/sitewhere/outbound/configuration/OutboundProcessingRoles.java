/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for outbound processing microservice.
 * 
 * @author Derek
 */
public enum OutboundProcessingRoles implements IConfigurationRoleProvider {

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

    /** Root event sources role. */
    OutboundProcessing(ConfigurationRole.build(OutboundProcessingRoleKeys.OutboundProcessing, "Outbound Processing",
	    false, false, false, new IRoleKey[] { OutboundProcessingRoleKeys.OutboundEventProcessor }, new IRoleKey[0],
	    true)),

    /** Event processor. */
    OutboundEventProcessor(ConfigurationRole.build(OutboundProcessingRoleKeys.OutboundEventProcessor,
	    "Event Processors", true, true, true, new IRoleKey[0],
	    new IRoleKey[] { OutboundProcessingRoleKeys.FilteredEventProcessor,
		    OutboundProcessingRoleKeys.ZoneTestEventProcessor, OutboundProcessingRoleKeys.MqttEventProcessor,
		    OutboundProcessingRoleKeys.RabbitMqEventProcessor })),

    /** Outbound processing chain. Filtered event processor. */
    FilteredEventProcessor(ConfigurationRole.build(OutboundProcessingRoleKeys.FilteredEventProcessor,
	    "Filtered Event Processor", true, true, true, new IRoleKey[] { OutboundProcessingRoleKeys.Filters })),

    /** Processor filter criteria. */
    Filters(ConfigurationRole.build(OutboundProcessingRoleKeys.Filters, "Filters", true, false, false,
	    new IRoleKey[] { OutboundProcessingRoleKeys.OutboundFilters })),

    /** Processor filters. */
    OutboundFilters(
	    ConfigurationRole.build(OutboundProcessingRoleKeys.OutboundFilters, "Outbound Filter", true, true, true)),

    /** Zone test event processor. */
    ZoneTestEventProcessor(ConfigurationRole.build(OutboundProcessingRoleKeys.ZoneTestEventProcessor,
	    "Zone Test Event Processor", true, true, true,
	    new IRoleKey[] { OutboundProcessingRoleKeys.ZoneTestElement, OutboundProcessingRoleKeys.OutboundFilters })),

    /** Zone test. */
    ZoneTest(ConfigurationRole.build(OutboundProcessingRoleKeys.ZoneTestElement, "Zone Test", true, true, true)),

    /** MQTT event processor. */
    MqttEventProcessor(ConfigurationRole.build(OutboundProcessingRoleKeys.MqttEventProcessor, "MQTT Event Processor",
	    true, true, true,
	    new IRoleKey[] { OutboundProcessingRoleKeys.Filters, OutboundProcessingRoleKeys.RouteBuilder })),

    /** MQTT event processsor. Route builder. */
    RouteBuilder(ConfigurationRole.build(OutboundProcessingRoleKeys.RouteBuilder, "Route Builder", true, false, false)),

    /** Outbound processing chain. RabbitMQ event processor. */
    RabbitMqEventProcessor(ConfigurationRole.build(OutboundProcessingRoleKeys.RabbitMqEventProcessor,
	    "RabbitMQ Event Processor", true, true, true,
	    new IRoleKey[] { OutboundProcessingRoleKeys.Filters, OutboundProcessingRoleKeys.RouteBuilder }));

    private ConfigurationRole role;

    private OutboundProcessingRoles(ConfigurationRole role) {
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