/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration;

import com.sitewhere.configuration.ConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRole;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;
import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

/**
 * Configuration roles available for command delivery microservice.
 * 
 * @author Derek
 */
public enum CommandDeliveryRoles implements IConfigurationRoleProvider {

    /** Command delivery. */
    CommandDelivery(ConfigurationRole.build(CommandDeliveryRoleKeys.CommandDelivery, "Command Delivery", false, false,
	    false, new IRoleKey[] { CommandDeliveryRoleKeys.CommandRouter, CommandDeliveryRoleKeys.CommandDestination },
	    new IRoleKey[0], true)),

    /** Command router implementation. */
    CommandRouter(ConfigurationRole.build(CommandDeliveryRoleKeys.CommandRouter, "Command Router", true, false, false,
	    new IRoleKey[0], new IRoleKey[] { CommandDeliveryRoleKeys.SpecificationMappingRouter })),

    /** Specification mapping router. */
    SpecificationMappingRouter(
	    ConfigurationRole.build(CommandDeliveryRoleKeys.SpecificationMappingRouter, "Specification Mapping Router",
		    false, false, false, new IRoleKey[] { CommandDeliveryRoleKeys.SpecificationMappingRouterMapping })),

    /** Specification mapping router mapping. */
    SpecificationMappingRouterMapping(ConfigurationRole.build(CommandDeliveryRoleKeys.SpecificationMappingRouterMapping,
	    "Mappings", true, true, true)),

    /** Command destination. */
    CommandDestination(ConfigurationRole.build(CommandDeliveryRoleKeys.CommandDestination, "Command Destination", true,
	    true, true,
	    new IRoleKey[] { CommandDeliveryRoleKeys.CommandEncoder, CommandDeliveryRoleKeys.ParameterExtractor })),

    /** Command encoder. */
    CommandEncoder(ConfigurationRole.build(CommandDeliveryRoleKeys.CommandEncoder, "Command Encoder", false, false,
	    false, new IRoleKey[0], new IRoleKey[] { CommandDeliveryRoleKeys.BinaryCommandEncoder,
		    CommandDeliveryRoleKeys.StringCommandEncoder })),

    /** Binary command encoder. */
    BinaryCommandEncoder(ConfigurationRole.build(CommandDeliveryRoleKeys.BinaryCommandEncoder, "Binary Command Encoder",
	    false, false, false)),

    /** String command encoder. */
    StringCommandEncoder(ConfigurationRole.build(CommandDeliveryRoleKeys.StringCommandEncoder, "String Command Encoder",
	    false, false, false)),

    /** Parameter extractor. */
    ParameterExtractor(ConfigurationRole.build(CommandDeliveryRoleKeys.ParameterExtractor, "Parameter Extractor", false,
	    false, false, new IRoleKey[0], new IRoleKey[] { CommandDeliveryRoleKeys.MqttParameterExtractor,
		    CommandDeliveryRoleKeys.SmsParameterExtractor, CommandDeliveryRoleKeys.CoapParameterExtractor })),

    /** MQTT parameter extractor. */
    MqttParameterExtractor(ConfigurationRole.build(CommandDeliveryRoleKeys.MqttParameterExtractor,
	    "MQTT Parameter Extractor", false, false, false)),

    /** SMS parameter extractor. */
    SmsParameterExtractor(ConfigurationRole.build(CommandDeliveryRoleKeys.SmsParameterExtractor,
	    "SMS Parameter Extractor", false, false, false)),

    /** CoAP parameter extractor. */
    CoapParameterExtractor(ConfigurationRole.build(CommandDeliveryRoleKeys.CoapParameterExtractor,
	    "CoAP Parameter Extractor", false, false, false));

    private ConfigurationRole role;

    private CommandDeliveryRoles(ConfigurationRole role) {
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