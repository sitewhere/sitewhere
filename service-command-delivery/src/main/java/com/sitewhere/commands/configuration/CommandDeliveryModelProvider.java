/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration;

import com.sitewhere.configuration.CommonConnectorModel;
import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.parser.ICommandDeliveryParser;
import com.sitewhere.rest.model.configuration.AttributeGroup;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IAttributeGroup;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for command delivery microservice.
 * 
 * @author Derek
 */
public class CommandDeliveryModelProvider extends ConfigurationModelProvider {

    // Command destination settings attribute group.
    public static final IAttributeGroup ATTR_GROUP_CMDDEST = new AttributeGroup("cdst", "Command Destination Settings");

    // Router settings attribute group.
    public static final IAttributeGroup ATTR_GROUP_ROUTER = new AttributeGroup("rout", "Router Settings");

    // Command encoder attribute group.
    public static final IAttributeGroup ATTR_GROUP_CMDENC = new AttributeGroup("cenc", "Command Encoder Settings");

    // Command extractor attribute group.
    public static final IAttributeGroup ATTR_GROUP_CMDEXT = new AttributeGroup("cext", "Command Extractor Settings");

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/command-delivery";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return CommandDeliveryRoles.CommandDelivery;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	addElement(createCommandDeliveryElement());

	// Command routers.
	addElement(createGroovyCommandRouterElement());
	addElement(createDeviceTypeMappingRouterElement());
	addElement(createDeviceTypeMappingRouterMappingElement());

	// Command destinations.
	addElement(createMqttCommandDestinationElement());
	addElement(createCoapCommandDestinationElement());
	addElement(createTwilioCommandDestinationElement());

	// Binary command encoders.
	addElement(createProtobufCommandEncoderElement());
	addElement(createProtobufHybridCommandEncoderElement());
	addElement(createJsonCommandEncoderElement());
	addElement(createGroovyCommandEncoderElement());

	// String command encoders.
	addElement(createGroovyStringCommandEncoderElement());

	// Parameter extractors.
	addElement(createHardwareIdParameterExtractorElement());
	addElement(createCoapMetadataParameterExtractorElement());
	addElement(createGroovySmsParameterExtractorElement());
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (CommandDeliveryRoles role : CommandDeliveryRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /**
     * Create element configuration for command delivery.
     * 
     * @return
     */
    protected ElementNode createCommandDeliveryElement() {
	ElementNode.Builder builder = new ElementNode.Builder(CommandDeliveryRoles.CommandDelivery.getRole().getName(),
		ICommandDeliveryParser.ROOT, "sitemap", CommandDeliveryRoleKeys.CommandDelivery, this);

	builder.description("Determines how commands are delivered to command destinations.");
	return builder.build();
    }

    /**
     * Create element configuration for device type mapping command router.
     * 
     * @return
     */
    protected ElementNode createDeviceTypeMappingRouterElement() {
	ElementNode.Builder builder = new ElementNode.Builder(
		CommandDeliveryRoles.DeviceTypeMappingRouter.getRole().getName(),
		ICommandDeliveryParser.Elements.DeviceTypeMappingRouter.getLocalName(), "sitemap",
		CommandDeliveryRoleKeys.DeviceTypeMappingRouter, this);

	builder.description("Routes commands based on a direct mapping from device type to a command desitination. "
		+ "Commands for device types not in the mapping list are routed to " + "the default destination.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_ROUTER);

	builder.attribute((new AttributeNode.Builder("Default destination", "defaultDestination", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_ROUTER).description(
			"Identifier for default destination commands should be routed to if no mapping is found.")
			.build()));
	return builder.build();
    }

    /**
     * Create element configuration for specification mapping command router.
     * 
     * @return
     */
    protected ElementNode createDeviceTypeMappingRouterMappingElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Device Type Mapping", "mapping", "sitemap",
		CommandDeliveryRoleKeys.DeviceTypeMappingRouterMapping, this);

	builder.description("Maps a device type to a command destination that should process it.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_ROUTER);

	builder.attribute((new AttributeNode.Builder("Device type", "deviceType", AttributeType.DeviceTypeReference,
		CommandDeliveryModelProvider.ATTR_GROUP_ROUTER).description("Device specification for the mapping.")
			.makeIndex().build()));
	builder.attribute((new AttributeNode.Builder("Destination id", "destination", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_ROUTER)
			.description("Unique id of command destination for the mapping.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create element configuration for specification mapping command router.
     * 
     * @return
     */
    protected ElementNode createGroovyCommandRouterElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Command Router",
		ICommandDeliveryParser.Elements.GroovyCommandRouter.getLocalName(), "sitemap",
		CommandDeliveryRoleKeys.CommandRouter, this);

	builder.description("Routes commands to command destinations based on routing logic "
		+ "contained in a Groovy script. The script returns the id of the command "
		+ "destination to be used for delivering the command.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_ROUTER);

	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_ROUTER)
			.description("Path to Groovy script which executes routing logic.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Add attributes common to all command destinations.
     * 
     * @param builder
     * @param group
     */
    protected void addCommandDestinationAttributes(ElementNode.Builder builder, IAttributeGroup group) {
	builder.attribute((new AttributeNode.Builder("Destination id", "destinationId", AttributeType.String, group)
		.description("Unique identifier for command destination.").makeIndex().build()));
    }

    /**
     * Create element configuration for MQTT command destination.
     * 
     * @return
     */
    protected ElementNode createMqttCommandDestinationElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MQTT Command Destination",
		ICommandDeliveryParser.Elements.MqttCommandDestination.getLocalName(), "sign-out-alt",
		CommandDeliveryRoleKeys.CommandDestination, this);

	builder.description("Sends commands to remote devices using the MQTT protocol. Commands are first encoded "
		+ "using a binary encoder, then a parameter extractor is used to determine the topic used "
		+ "to deliver the payload to the subscriber.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_AUTHENTICATION);

	// Add common command destination attributes.
	addCommandDestinationAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	// Only allow binary command encoders.
	builder.specializes(CommandDeliveryRoleKeys.CommandEncoder, CommandDeliveryRoleKeys.BinaryCommandEncoder);

	// Only allow MQTT parameter extractors
	builder.specializes(CommandDeliveryRoleKeys.ParameterExtractor, CommandDeliveryRoleKeys.MqttParameterExtractor);

	// Add common MQTT connectivity attributes.
	CommonConnectorModel.addMqttCommonAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY,
		ConfigurationModelProvider.ATTR_GROUP_AUTHENTICATION);

	return builder.build();
    }

    /**
     * Create element configuration for MQTT command destination.
     * 
     * @return
     */
    protected ElementNode createCoapCommandDestinationElement() {
	ElementNode.Builder builder = new ElementNode.Builder("CoAP Command Destination",
		ICommandDeliveryParser.Elements.CoapCommandDestination.getLocalName(), "sign-out-alt",
		CommandDeliveryRoleKeys.CommandDestination, this);

	builder.description("Sends commands to remote devices using the CoAP protocol. Commands are first encoded "
		+ "using a binary encoder, then a parameter extractor is used to determine the connection "
		+ "information to make a client request to the device.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_CMDDEST);

	// Add common command destination attributes.
	addCommandDestinationAttributes(builder, CommandDeliveryModelProvider.ATTR_GROUP_CMDDEST);

	// Only allow binary command encoders.
	builder.specializes(CommandDeliveryRoleKeys.CommandEncoder, CommandDeliveryRoleKeys.BinaryCommandEncoder);

	// Only allow MQTT parameter extractors
	builder.specializes(CommandDeliveryRoleKeys.ParameterExtractor, CommandDeliveryRoleKeys.CoapParameterExtractor);

	return builder.build();
    }

    /**
     * Create element configuration for Twilio command destination.
     * 
     * @return
     */
    protected ElementNode createTwilioCommandDestinationElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Twilio Command Destination",
		ICommandDeliveryParser.Elements.TwilioCommandDestination.getLocalName(), "phone",
		CommandDeliveryRoleKeys.CommandDestination, this);

	builder.description("Destination that delivers commands via Twilio SMS messages.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_CMDDEST);

	// Add common command destination attributes.
	addCommandDestinationAttributes(builder, CommandDeliveryModelProvider.ATTR_GROUP_CMDDEST);

	// Only allow String command encoders.
	builder.specializes(CommandDeliveryRoleKeys.CommandEncoder, CommandDeliveryRoleKeys.StringCommandEncoder);

	// Only allow SMS parameter extractors
	builder.specializes(CommandDeliveryRoleKeys.ParameterExtractor, CommandDeliveryRoleKeys.SmsParameterExtractor);

	builder.attribute((new AttributeNode.Builder("Account SID", "accountSid", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDDEST).description("Twilio account SID.").build()));
	builder.attribute((new AttributeNode.Builder("Authorization token", "authToken", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDDEST).description("Twilio authorization token.").build()));
	builder.attribute((new AttributeNode.Builder("From phone number", "fromPhoneNumber", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDDEST)
			.description("Twilio phone number that originates message.").build()));

	return builder.build();
    }

    /**
     * Create element configuration for GPB command encoder.
     * 
     * @return
     */
    protected ElementNode createProtobufCommandEncoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Google Protocol Buffers Command Encoder",
		ICommandDeliveryParser.BinaryCommandEncoders.ProtobufEncoder.getLocalName(), "cogs",
		CommandDeliveryRoleKeys.BinaryCommandEncoder, this);

	builder.description("Encodes a command using the default Google Protocol Buffers representation. "
		+ "The proto file for the representation can be found in the <strong>code generation</strong> "
		+ "page for the device specification.");

	return builder.build();
    }

    /**
     * Create element configuration for Java/protobuf hybrid command encoder.
     * 
     * @return
     */
    protected ElementNode createProtobufHybridCommandEncoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Java/Protobuf Hybrid Command Encoder",
		ICommandDeliveryParser.BinaryCommandEncoders.JavaHybridProtobufEncoder.getLocalName(), "cogs",
		CommandDeliveryRoleKeys.BinaryCommandEncoder, this);

	builder.description("Command encoder that encodes system commands using protocol buffers but encodes "
		+ "custom commands using serialized Java objects. This allows Java clients to use the commands "
		+ "directly rather than having to recompile stubs based on a proto.");

	return builder.build();
    }

    /**
     * Create element configuration for JSON command encoder.
     * 
     * @return
     */
    protected ElementNode createJsonCommandEncoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("JSON Command Encoder",
		ICommandDeliveryParser.BinaryCommandEncoders.JsonCommandEncoder.getLocalName(), "cogs",
		CommandDeliveryRoleKeys.BinaryCommandEncoder, this);

	builder.description(
		"Command encoder that encodes both system and custom commands as JSON for " + "simplified client use.");

	return builder.build();
    }

    /**
     * Create element configuration for Groovy command encoder.
     * 
     * @return
     */
    protected ElementNode createGroovyCommandEncoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Command Encoder",
		ICommandDeliveryParser.BinaryCommandEncoders.GroovyCommandEncoder.getLocalName(), "cogs",
		CommandDeliveryRoleKeys.BinaryCommandEncoder, this);

	builder.description("Command encoder that encodes both system and custom commands using a groovy "
		+ "script for the encoding logic.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_CMDENC);

	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDENC)
			.description("Path to Groovy script which encodes commands.").makeRequired().build()));

	return builder.build();
    }

    /**
     * Create element configuration for Groovy String command encoder.
     * 
     * @return
     */
    protected ElementNode createGroovyStringCommandEncoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy String Command Encoder",
		ICommandDeliveryParser.StringCommandEncoders.GroovyStringCommandEncoder.getLocalName(), "cogs",
		CommandDeliveryRoleKeys.StringCommandEncoder, this);

	builder.description("Command encoder that encodes both system and custom commands using a groovy "
		+ "script for the encoding logic. The script is expected to return a String which will be used "
		+ "as the command payload.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_CMDENC);

	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDENC)
			.description("Path to Groovy script which encodes commands.").makeRequired().build()));

	return builder.build();
    }

    /**
     * Create element configuration for hardware id parameter extractor.
     * 
     * @return
     */
    protected ElementNode createHardwareIdParameterExtractorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Hardware Id Topic Extractor",
		"hardware-id-topic-extractor", "cogs", CommandDeliveryRoleKeys.MqttParameterExtractor, this);

	builder.description("Calculates MQTT topic for publishing commands by substituting the device "
		+ "hardware id into parameterized strings. The resulting values are used by the command "
		+ "destination to send the encoded command payload to the device.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_CMDEXT);

	builder.attribute((new AttributeNode.Builder("Command topic expression", "commandTopicExpr",
		AttributeType.String, CommandDeliveryModelProvider.ATTR_GROUP_CMDEXT)
			.description("Expression for building topic name to which custom commands are sent. "
				+ "Add a '%s' where the hardware id should be inserted.")
			.defaultValue("SiteWhere/commands/%s").build()));
	builder.attribute((new AttributeNode.Builder("System topic expression", "systemTopicExpr", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDEXT)
			.description("Expression for building topic name to which system commands are sent. "
				+ "Add a '%s' where the hardware id should be inserted.")
			.defaultValue("SiteWhere/system/%s").build()));

	return builder.build();
    }

    /**
     * Create element configuration for CoAP metadata parameter extractor.
     * 
     * @return
     */
    protected ElementNode createCoapMetadataParameterExtractorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("CoAP Device Metadata Extractor",
		"metadata-coap-parameter-extractor", "cogs", CommandDeliveryRoleKeys.CoapParameterExtractor, this);

	builder.description("Extracts CoAP connection information from metadata associated with a device.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_CMDEXT);

	builder.attribute((new AttributeNode.Builder("Hostname metadata", "hostnameMetadataField", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDEXT)
			.description("Metadata field that holds hostname information for the CoAP connection.")
			.defaultValue("hostname").build()));
	builder.attribute((new AttributeNode.Builder("Port metadata", "portMetadataField", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDEXT)
			.description("Metadata field that holds port information for the CoAP connection.")
			.defaultValue("port").build()));
	builder.attribute((new AttributeNode.Builder("URL metadata", "urlMetadataField", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDEXT).description(
			"Metadata field that holds information about the relative URL for the CoAP client request.")
			.build()));

	return builder.build();
    }

    /**
     * Create element configuration for CoAP metadata parameter extractor.
     * 
     * @return
     */
    protected ElementNode createGroovySmsParameterExtractorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy SMS Parameter Extractor",
		"groovy-sms-parameter-extractor", "cogs", CommandDeliveryRoleKeys.SmsParameterExtractor, this);

	builder.description("Uses a Groovy script to extract SMS parameter information for delivering a command.");
	builder.attributeGroup(CommandDeliveryModelProvider.ATTR_GROUP_CMDEXT);

	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String,
		CommandDeliveryModelProvider.ATTR_GROUP_CMDEXT)
			.description("Path to Groovy script which encodes commands.").makeRequired().build()));

	return builder.build();
    }
}