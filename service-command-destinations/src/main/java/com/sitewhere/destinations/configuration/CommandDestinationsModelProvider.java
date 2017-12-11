/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.destinations.configuration;

import com.sitewhere.configuration.CommonCommunicationModel;
import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.old.ICommandDestinationsParser;
import com.sitewhere.configuration.old.ICommandRoutingParser;
import com.sitewhere.configuration.old.IDeviceCommunicationParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for command destinations microservice.
 * 
 * @author Derek
 */
public class CommandDestinationsModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/command-destinations";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return CommandDestinationsRoles.CommandDestinations;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	// Command routing.
	addElement(createCommandRoutingElement());
	addElement(createGroovyCommandRouterElement());
	addElement(createSpecificationMappingRouterElement());
	addElement(createSpecificationMappingRouterMappingElement());

	// Command destinations.
	addElement(createCommandDestinationsElement());
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
	for (CommandDestinationsRoles role : CommandDestinationsRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /**
     * Create element configuration for command routing.
     * 
     * @return
     */
    protected ElementNode createCommandRoutingElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Device Command Routing",
		IDeviceCommunicationParser.Elements.CommandRouting.getLocalName(), "sitemap",
		CommandDestinationsRoleKeys.CommandRouting);

	builder.description("Determines how commands are routed to command destinations.");
	return builder.build();
    }

    /**
     * Create element configuration for specification mapping command router.
     * 
     * @return
     */
    protected ElementNode createSpecificationMappingRouterElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Specification Mapping Router",
		ICommandRoutingParser.Elements.SpecificationMappingRouter.getLocalName(), "sitemap",
		CommandDestinationsRoleKeys.SpecificationMappingRouter);

	builder.description("Routes commands based on a direct mapping from device specification token "
		+ "to a command desitination. Commands for specifications not in the mapping list are routed to "
		+ "the default destination.");
	builder.attribute((new AttributeNode.Builder("Default destination", "defaultDestination", AttributeType.String)
		.description("Identifier for default destination commands should be routed to if no mapping is found.")
		.build()));
	return builder.build();
    }

    /**
     * Create element configuration for specification mapping command router.
     * 
     * @return
     */
    protected ElementNode createGroovyCommandRouterElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Command Router",
		ICommandRoutingParser.Elements.GroovyCommandRouter.getLocalName(), "sitemap",
		CommandDestinationsRoleKeys.CommandRouter);

	builder.description("Routes commands to command destinations based on routing logic "
		+ "contained in a Groovy script. The script returns the id of the command "
		+ "destination to be used for delivering the command.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Path to Groovy script which executes routing logic.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create element configuration for specification mapping command router.
     * 
     * @return
     */
    protected ElementNode createSpecificationMappingRouterMappingElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Specification Mapping", "mapping", "arrows-h",
		CommandDestinationsRoleKeys.SpecificationMappingRouterMapping);

	builder.description("Maps a specification token to a command destination that should process it.");
	builder.attribute(
		(new AttributeNode.Builder("Specification", "specification", AttributeType.SpecificationReference)
			.description("Device specification for the mapping.").makeIndex().build()));
	builder.attribute((new AttributeNode.Builder("Destination id", "destination", AttributeType.String)
		.description("Unique id of command destination for the mapping.").build()));
	return builder.build();
    }

    /**
     * Create element configuration for command routing.
     * 
     * @return
     */
    protected ElementNode createCommandDestinationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Device Command Destinations",
		IDeviceCommunicationParser.Elements.CommandDestinations.getLocalName(), "sign-out",
		CommandDestinationsRoleKeys.CommandDestinations);

	builder.description("Command destinations provide the information SiteWhere needs "
		+ "to route commands to devices. This includes information about how to encode the "
		+ "command and how to deliver the command via the underlying transport.");
	return builder.build();
    }

    /**
     * Add attributes common to all command destinations.
     * 
     * @param builder
     */
    protected void addCommandDestinationAttributes(ElementNode.Builder builder) {
	builder.attribute((new AttributeNode.Builder("Destination id", "destinationId", AttributeType.String)
		.description("Unique identifier for command destination.").makeIndex().build()));
    }

    /**
     * Create element configuration for MQTT command destination.
     * 
     * @return
     */
    protected ElementNode createMqttCommandDestinationElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MQTT Command Destination",
		ICommandDestinationsParser.Elements.MqttCommandDestination.getLocalName(), "sign-out",
		CommandDestinationsRoleKeys.CommandDestination);

	builder.description("Sends commands to remote devices using the MQTT protocol. Commands are first encoded "
		+ "using a binary encoder, then a parameter extractor is used to determine the topic used "
		+ "to deliver the payload to the subscriber.");

	// Add common command destination attributes.
	addCommandDestinationAttributes(builder);

	// Only allow binary command encoders.
	builder.specializes(CommandDestinationsRoleKeys.CommandEncoder,
		CommandDestinationsRoleKeys.BinaryCommandEncoder);

	// Only allow MQTT parameter extractors
	builder.specializes(CommandDestinationsRoleKeys.ParameterExtractor,
		CommandDestinationsRoleKeys.MqttParameterExtractor);

	// Add common MQTT connectivity attributes.
	CommonCommunicationModel.addMqttConnectivityAttributes(builder);

	return builder.build();
    }

    /**
     * Create element configuration for MQTT command destination.
     * 
     * @return
     */
    protected ElementNode createCoapCommandDestinationElement() {
	ElementNode.Builder builder = new ElementNode.Builder("CoAP Command Destination",
		ICommandDestinationsParser.Elements.CoapCommandDestination.getLocalName(), "sign-out",
		CommandDestinationsRoleKeys.CommandDestination);

	builder.description("Sends commands to remote devices using the CoAP protocol. Commands are first encoded "
		+ "using a binary encoder, then a parameter extractor is used to determine the connection "
		+ "information to make a client request to the device.");

	// Add common command destination attributes.
	addCommandDestinationAttributes(builder);

	// Only allow binary command encoders.
	builder.specializes(CommandDestinationsRoleKeys.CommandEncoder,
		CommandDestinationsRoleKeys.BinaryCommandEncoder);

	// Only allow MQTT parameter extractors
	builder.specializes(CommandDestinationsRoleKeys.ParameterExtractor,
		CommandDestinationsRoleKeys.CoapParameterExtractor);

	return builder.build();
    }

    /**
     * Create element configuration for Twilio command destination.
     * 
     * @return
     */
    protected ElementNode createTwilioCommandDestinationElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Twilio Command Destination",
		ICommandDestinationsParser.Elements.TwilioCommandDestination.getLocalName(), "phone",
		CommandDestinationsRoleKeys.CommandDestination);

	builder.description("Destination that delivers commands via Twilio SMS messages.");

	// Add common command destination attributes.
	addCommandDestinationAttributes(builder);

	// Only allow String command encoders.
	builder.specializes(CommandDestinationsRoleKeys.CommandEncoder,
		CommandDestinationsRoleKeys.StringCommandEncoder);

	// Only allow SMS parameter extractors
	builder.specializes(CommandDestinationsRoleKeys.ParameterExtractor,
		CommandDestinationsRoleKeys.SmsParameterExtractor);

	builder.attribute((new AttributeNode.Builder("Account SID", "accountSid", AttributeType.String)
		.description("Twilio account SID.").build()));
	builder.attribute((new AttributeNode.Builder("Authorization token", "authToken", AttributeType.String)
		.description("Twilio authorization token.").build()));
	builder.attribute((new AttributeNode.Builder("From phone number", "fromPhoneNumber", AttributeType.String)
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
		ICommandDestinationsParser.BinaryCommandEncoders.ProtobufEncoder.getLocalName(), "cogs",
		CommandDestinationsRoleKeys.BinaryCommandEncoder);

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
		ICommandDestinationsParser.BinaryCommandEncoders.JavaHybridProtobufEncoder.getLocalName(), "cogs",
		CommandDestinationsRoleKeys.BinaryCommandEncoder);

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
		ICommandDestinationsParser.BinaryCommandEncoders.JsonCommandEncoder.getLocalName(), "cogs",
		CommandDestinationsRoleKeys.BinaryCommandEncoder);

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
		ICommandDestinationsParser.BinaryCommandEncoders.GroovyCommandEncoder.getLocalName(), "cogs",
		CommandDestinationsRoleKeys.BinaryCommandEncoder);

	builder.description("Command encoder that encodes both system and custom commands using a groovy "
		+ "script for the encoding logic.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
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
		ICommandDestinationsParser.StringCommandEncoders.GroovyStringCommandEncoder.getLocalName(), "cogs",
		CommandDestinationsRoleKeys.StringCommandEncoder);

	builder.description("Command encoder that encodes both system and custom commands using a groovy "
		+ "script for the encoding logic. The script is expected to return a String which will be used "
		+ "as the command payload.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
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
		"hardware-id-topic-extractor", "cogs", CommandDestinationsRoleKeys.MqttParameterExtractor);

	builder.description("Calculates MQTT topic for publishing commands by substituting the device "
		+ "hardware id into parameterized strings. The resulting values are used by the command "
		+ "destination to send the encoded command payload to the device.");
	builder.attribute(
		(new AttributeNode.Builder("Command topic expression", "commandTopicExpr", AttributeType.String)
			.description("Expression for building topic name to which custom commands are sent. "
				+ "Add a '%s' where the hardware id should be inserted.")
			.defaultValue("SiteWhere/commands/%s").build()));
	builder.attribute((new AttributeNode.Builder("System topic expression", "systemTopicExpr", AttributeType.String)
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
		"metadata-coap-parameter-extractor", "cogs", CommandDestinationsRoleKeys.CoapParameterExtractor);

	builder.description("Extracts CoAP connection information from metadata associated with a device.");
	builder.attribute((new AttributeNode.Builder("Hostname metadata", "hostnameMetadataField", AttributeType.String)
		.description("Metadata field that holds hostname information for the CoAP connection.")
		.defaultValue("hostname").build()));
	builder.attribute((new AttributeNode.Builder("Port metadata", "portMetadataField", AttributeType.String)
		.description("Metadata field that holds port information for the CoAP connection.").defaultValue("port")
		.build()));
	builder.attribute((new AttributeNode.Builder("URL metadata", "urlMetadataField", AttributeType.String)
		.description(
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
		"groovy-sms-parameter-extractor", "cogs", CommandDestinationsRoleKeys.SmsParameterExtractor);

	builder.description("Uses a Groovy script to extract SMS parameter information for delivering a command.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Path to Groovy script which encodes commands.").makeRequired().build()));

	return builder.build();
    }
}