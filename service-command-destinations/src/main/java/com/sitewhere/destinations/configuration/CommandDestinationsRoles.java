/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.destinations.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for command destinations microservice.
 * 
 * @author Derek
 */
public enum CommandDestinationsRoles implements IConfigurationRoleProvider {

    /** Command destinations. */
    CommandDestinations(ConfigurationRole.build(CommandDestinationsRoleKeys.CommandDestinations, "Command Destinations",
	    false, false, false, new IRoleKey[] { CommandDestinationsRoleKeys.CommandDestination }, new IRoleKey[0],
	    true)),

    /** Command routing configuration. */
    CommandRouting(ConfigurationRole.build(CommandDestinationsRoleKeys.CommandRouting, "Command Routing", false, false,
	    false, new IRoleKey[] { CommandDestinationsRoleKeys.CommandRouter }, new IRoleKey[0], true)),

    /** Command router implementation. */
    CommandRouter(ConfigurationRole.build(CommandDestinationsRoleKeys.CommandRouter, "Command Router", false, false,
	    false, new IRoleKey[0], new IRoleKey[] { CommandDestinationsRoleKeys.SpecificationMappingRouter })),

    /** Specification mapping router. */
    SpecificationMappingRouter(ConfigurationRole.build(CommandDestinationsRoleKeys.SpecificationMappingRouter,
	    "Specification Mapping Router", false, false, false,
	    new IRoleKey[] { CommandDestinationsRoleKeys.SpecificationMappingRouterMapping })),

    /** Specification mapping router mapping. */
    SpecificationMappingRouterMapping(ConfigurationRole
	    .build(CommandDestinationsRoleKeys.SpecificationMappingRouterMapping, "Mappings", true, true, true)),

    /** Command destination. */
    CommandDestination(ConfigurationRole.build(CommandDestinationsRoleKeys.CommandDestination, "Command Destination",
	    true, true, true, new IRoleKey[] { CommandDestinationsRoleKeys.CommandEncoder,
		    CommandDestinationsRoleKeys.ParameterExtractor })),

    /** Command encoder. */
    CommandEncoder(ConfigurationRole.build(CommandDestinationsRoleKeys.CommandEncoder, "Command Encoder", false, false,
	    false, new IRoleKey[0], new IRoleKey[] { CommandDestinationsRoleKeys.BinaryCommandEncoder,
		    CommandDestinationsRoleKeys.StringCommandEncoder })),

    /** Binary command encoder. */
    BinaryCommandEncoder(ConfigurationRole.build(CommandDestinationsRoleKeys.BinaryCommandEncoder,
	    "Binary Command Encoder", false, false, false)),

    /** String command encoder. */
    StringCommandEncoder(ConfigurationRole.build(CommandDestinationsRoleKeys.StringCommandEncoder,
	    "String Command Encoder", false, false, false)),

    /** Parameter extractor. */
    ParameterExtractor(ConfigurationRole.build(CommandDestinationsRoleKeys.ParameterExtractor, "Parameter Extractor",
	    false, false, false, new IRoleKey[0],
	    new IRoleKey[] { CommandDestinationsRoleKeys.MqttParameterExtractor,
		    CommandDestinationsRoleKeys.SmsParameterExtractor,
		    CommandDestinationsRoleKeys.CoapParameterExtractor })),

    /** MQTT parameter extractor. */
    MqttParameterExtractor(ConfigurationRole.build(CommandDestinationsRoleKeys.MqttParameterExtractor,
	    "MQTT Parameter Extractor", false, false, false)),

    /** SMS parameter extractor. */
    SmsParameterExtractor(ConfigurationRole.build(CommandDestinationsRoleKeys.SmsParameterExtractor,
	    "SMS Parameter Extractor", false, false, false)),

    /** CoAP parameter extractor. */
    CoapParameterExtractor(ConfigurationRole.build(CommandDestinationsRoleKeys.CoapParameterExtractor,
	    "CoAP Parameter Extractor", false, false, false));

    private ConfigurationRole role;

    private CommandDestinationsRoles(ConfigurationRole role) {
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