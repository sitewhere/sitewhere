/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import com.sitewhere.spring.handler.BatchOperationsParser;
import com.sitewhere.spring.handler.CommandDestinationsParser;
import com.sitewhere.spring.handler.CommandRoutingParser;
import com.sitewhere.spring.handler.DeviceCommunicationParser;
import com.sitewhere.spring.handler.EventSourcesParser;
import com.sitewhere.spring.handler.InboundProcessingStrategyParser;
import com.sitewhere.spring.handler.RegistrationParser;
import com.sitewhere.spring.handler.TenantConfigurationParser;
import com.sitewhere.web.configuration.model.AttributeNode;
import com.sitewhere.web.configuration.model.AttributeType;
import com.sitewhere.web.configuration.model.ConfigurationModel;
import com.sitewhere.web.configuration.model.ElementNode;
import com.sitewhere.web.configuration.model.ElementRole;

/**
 * Configuration model for device communication elements.
 * 
 * @author Derek
 */
public class DeviceCommunicationModel extends ConfigurationModel {

	public DeviceCommunicationModel() {
		addElement(createDeviceCommunication());

		// Event sources.
		addElement(createEventSourcesElement());
		addElement(createMqttEventSourceElement());
		addElement(createAzureEventHubEventSourceElement());
		addElement(createActiveMQEventSourceElement());
		addElement(createHazelcastQueueEventSourceElement());

		// Socket event source.
		addElement(createReadAllSocketInteractionHandlerElement());
		addElement(createSocketEventSourceElement());

		// WebSocket event source.
		addElement(createWebSocketHeaderElement());
		addElement(createWebSocketEventSourceElement());

		// Binary event decoders.
		addElement(createProtobufEventDecoderElement());
		addElement(createJsonEventDecoderElement());
		addElement(createGroovyEventDecoderElement());
		addElement(createGroovyStringEventDecoderElement());
		addElement(createEchoStringEventDecoderElement());

		// Inbound processing strategy.
		addElement(createInboundProcessingStrategyElement());
		addElement(createBlockingQueueInboundStrategyElement());

		// Registration.
		addElement(createRegistrationElement());
		addElement(createDefaultRegistrationManagerElement());

		// Batch operations.
		addElement(createBatchOperationsElement());
		addElement(createBatchOperationManagerElement());

		// Command routing.
		addElement(createCommandRoutingElement());
		addElement(createSpecificationMappingRouterElement());
		addElement(createSpecificationMappingRouterMappingElement());

		// Command destinations.
		addElement(createCommandDestinationsElement());
		addElement(createMqttCommandDestinationElement());
		addElement(createProtobufCommandEncoderElement());
		addElement(createHardwareIdParameterExtractorElement());
	}

	/**
	 * Create the container for device communication information.
	 * 
	 * @return
	 */
	protected ElementNode createDeviceCommunication() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Communication",
						TenantConfigurationParser.Elements.DeviceCommunication.getLocalName(), "exchange",
						ElementRole.DeviceCommunication);
		builder.description("Configure how information is received from devices, how data is queued "
				+ "for processing, and how commands are sent to devices.");
		return builder.build();
	}

	/**
	 * Create element configuration for event sources.
	 * 
	 * @return
	 */
	protected ElementNode createEventSourcesElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Event Sources",
						DeviceCommunicationParser.Elements.EventSources.getLocalName(), "sign-in",
						ElementRole.DeviceCommunication_EventSources);

		builder.description("Event sources are responsible for bringing data into SiteWhere. They "
				+ "listen for incoming messages, convert them to a unified format, then forward them "
				+ "to the inbound processing strategy implementation to be processed.");
		return builder.build();
	}

	/**
	 * Create element configuration for MQTT event source.
	 * 
	 * @return
	 */
	protected ElementNode createMqttEventSourceElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("MQTT Event Source",
						EventSourcesParser.Elements.MqttEventSource.getLocalName(), "sign-in",
						ElementRole.EventSources_EventSource);

		builder.description("Listen for events on an MQTT topic.");
		addEventSourceAttributes(builder);

		// Only accept binary event decoders.
		builder.specializes(ElementRole.EventSource_EventDecoder, ElementRole.EventSource_BinaryEventDecoder);

		// Add common MQTT connectivity attributes.
		addMqttConnectivityAttributes(builder);
		builder.attribute((new AttributeNode.Builder("MQTT topic", "topic", AttributeType.String).description("MQTT topic event source uses for inbound messages.").build()));

		return builder.build();
	}

	/**
	 * Add common event source attributes.
	 * 
	 * @param builder
	 */
	public static void addEventSourceAttributes(ElementNode.Builder builder) {
		builder.attribute((new AttributeNode.Builder("Source id", "sourceId", AttributeType.String).description(
				"Unique id used for referencing this event source.").makeIndex().makeRequired().build()));
	}

	/**
	 * Adds common MQTT connectivity attributes.
	 * 
	 * @param builder
	 */
	public static void addMqttConnectivityAttributes(ElementNode.Builder builder) {
		builder.attribute((new AttributeNode.Builder("Transport protocol", "protocol", AttributeType.String).description(
				"Protocol used for establishing MQTT connection").defaultValue("tcp").choice("tcp").choice(
				"tls").build()));
		builder.attribute((new AttributeNode.Builder("MQTT broker hostname", "hostname", AttributeType.String).description(
				"Hostname used for creating the MQTT broker connection.").defaultValue("localhost").build()));
		builder.attribute((new AttributeNode.Builder("MQTT broker port", "port", AttributeType.Integer).description(
				"Port number used for creating the MQTT broker connection.").defaultValue("1883").build()));
		builder.attribute((new AttributeNode.Builder("Trust store path", "trustStorePath",
				AttributeType.String).description("Fully-qualified path to trust store for secured connections.").build()));
		builder.attribute((new AttributeNode.Builder("Trust store password", "trustStorePassword",
				AttributeType.String).description("Password used to authenticate with trust store.").build()));
	}

	/**
	 * Create element configuration for Azure EventHub event source.
	 * 
	 * @return
	 */
	protected ElementNode createAzureEventHubEventSourceElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Azure EventHub Event Source",
						EventSourcesParser.Elements.AzureEventHubEventSource.getLocalName(), "cloud",
						ElementRole.EventSources_EventSource);

		builder.description("Event source that pulls binary information from an Azure EventHub endpoint and decodes it.");
		addEventSourceAttributes(builder);

		// Only accept binary event decoders.
		builder.specializes(ElementRole.EventSource_EventDecoder, ElementRole.EventSource_BinaryEventDecoder);

		builder.attribute((new AttributeNode.Builder("Target FQN", "targetFqn", AttributeType.String).description("EventHub targetFqn address.").build()));
		builder.attribute((new AttributeNode.Builder("Namespace", "namespace", AttributeType.String).description("EventHub namespace.").build()));
		builder.attribute((new AttributeNode.Builder("Entity path", "entityPath", AttributeType.String).description("EventHub entityPath.").build()));
		builder.attribute((new AttributeNode.Builder("Partition count", "partitionCount",
				AttributeType.Integer).description("EventHub partition count.").build()));
		builder.attribute((new AttributeNode.Builder("Zookeeper state store", "zkStateStore",
				AttributeType.String).description("Zookeeper store url for EventHub state persistence.").build()));
		builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String).description("Username for EventHub connection.").build()));
		builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String).description("Password for EventHub connection.").build()));

		return builder.build();
	}

	/**
	 * Create element configuration for ActiveMQ event source.
	 * 
	 * @return
	 */
	protected ElementNode createActiveMQEventSourceElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("ActiveMQ Event Source",
						EventSourcesParser.Elements.ActiveMQEventSource.getLocalName(), "sign-in",
						ElementRole.EventSources_EventSource);

		builder.description("Event source that pulls binary information from an ActiveMQ queue and decodes it.");
		addEventSourceAttributes(builder);

		// Only accept binary event decoders.
		builder.specializes(ElementRole.EventSource_EventDecoder, ElementRole.EventSource_BinaryEventDecoder);

		builder.attribute((new AttributeNode.Builder("Transport URI", "transportUri", AttributeType.String).description(
				"URI used to configure the trasport for the embedded ActiveMQ broker.").makeRequired().build()));
		builder.attribute((new AttributeNode.Builder("Data directory", "dataDirectory", AttributeType.String).description("Data directory used to store persistent message queues.").build()));
		builder.attribute((new AttributeNode.Builder("Queue name", "queueName", AttributeType.String).description(
				"Name of JMS queue for consumers to pull messages from.").makeRequired().build()));
		builder.attribute((new AttributeNode.Builder("Number of consumers", "numConsumers",
				AttributeType.Integer).description("Number of consumers used to read data from the queue into SiteWhere.").build()));

		return builder.build();
	}

	/**
	 * Create read-all socket interaction handler factory.
	 * 
	 * @return
	 */
	protected ElementNode createReadAllSocketInteractionHandlerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Read-All Socket Interaction Handler Factory",
						"read-all-interaction-handler-factory", "cog",
						ElementRole.EventSources_SocketInteractionHandlerFactory);

		builder.description("Interaction handler that reads all content from the client socket and delivers it "
				+ "to the decoder as a byte array.");

		return builder.build();
	}

	/**
	 * Create element configuration for socket event source.
	 * 
	 * @return
	 */
	protected ElementNode createSocketEventSourceElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Socket Event Source",
						EventSourcesParser.Elements.SocketEventSource.getLocalName(), "plug",
						ElementRole.EventSources_SocketEventSource);

		builder.description("Event source that pulls binary information from connections to a TCP/IP server socket.");
		addEventSourceAttributes(builder);

		// Only accept binary event decoders.
		builder.specializes(ElementRole.EventSource_EventDecoder, ElementRole.EventSource_BinaryEventDecoder);

		builder.attribute((new AttributeNode.Builder("Port", "port", AttributeType.Integer).description(
				"Port on which the server socket will listen.").defaultValue("8484").makeRequired().build()));
		builder.attribute((new AttributeNode.Builder("Number of threads", "numThreads", AttributeType.Integer).description(
				"Number of threads used to handle client connections to the server socket.").defaultValue("5").build()));

		return builder.build();
	}

	/**
	 * Create element configuration for WebSocket header.
	 * 
	 * @return
	 */
	protected ElementNode createWebSocketHeaderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("WebSocket Header", "header", "cog",
						ElementRole.EventSources_WebSocketHeader);

		builder.description("Header that is passed to the web socket for configuration.");

		builder.attribute((new AttributeNode.Builder("Header name", "name", AttributeType.String).description(
				"Header name.").makeRequired().makeIndex().build()));
		builder.attribute((new AttributeNode.Builder("Header value", "value", AttributeType.String).description(
				"Header value.").makeRequired().build()));

		return builder.build();
	}

	/**
	 * Create element configuration for WebSocket event source.
	 * 
	 * @return
	 */
	protected ElementNode createWebSocketEventSourceElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("WebSocket Event Source",
						EventSourcesParser.Elements.WebSocketEventSource.getLocalName(), "plug",
						ElementRole.EventSources_WebSocketEventSource);

		builder.description("Event source that pulls data from a web socket. Note that the event decoder needs "
				+ "to correspond to the payload type chosen.");
		addEventSourceAttributes(builder);

		builder.attribute((new AttributeNode.Builder("Web socket URL", "webSocketUrl", AttributeType.String).description(
				"URL of the web socket to connect to.").makeRequired().build()));
		builder.attribute((new AttributeNode.Builder("Payload type", "payloadType", AttributeType.String).description(
				"Chooses whether payload is processed as binary or string.").choice("binary").choice("string").defaultValue(
				"binary").build()));

		return builder.build();
	}

	/**
	 * Create element configuration for Hazelcast queue event source.
	 * 
	 * @return
	 */
	protected ElementNode createHazelcastQueueEventSourceElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Hazelcast Queue Event Source",
						EventSourcesParser.Elements.HazelcastQueueEventSource.getLocalName(), "sign-in",
						ElementRole.EventSources_EventSource);

		builder.description("Event source that pulls decoded events from a Hazelcast queue. Primarily used to "
				+ "allow one instance of SiteWhere to decode events and feed them to multiple subordinate instances for processing.");
		addEventSourceAttributes(builder);

		// Only accept binary event decoders.
		builder.specializes(ElementRole.EventSource_EventDecoder, ElementRole.EventSource_BinaryEventDecoder);

		return builder.build();
	}

	/**
	 * Create element configuration for protobuf event decoder.
	 * 
	 * @return
	 */
	protected ElementNode createProtobufEventDecoderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Google Protocol Buffers Event Decoder",
						EventSourcesParser.BinaryDecoders.ProtobufDecoder.getLocalName(), "cogs",
						ElementRole.EventSource_BinaryEventDecoder);

		builder.description("Event decoder that takes binary messages from an underlying transport "
				+ "and decodes them using the standard SiteWhere Google Protocol Buffers format. This is "
				+ "the default binary format used by the various SDKs.");
		return builder.build();
	}

	/**
	 * Create element configuration for JSON event decoder.
	 * 
	 * @return
	 */
	protected ElementNode createJsonEventDecoderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("JSON Event Decoder",
						EventSourcesParser.BinaryDecoders.JsonDecoder.getLocalName(), "cogs",
						ElementRole.EventSource_BinaryEventDecoder);

		builder.description("Event decoder that takes binary messages from an underlying transport "
				+ "and parses them as the JSON representation of a SiteWhere device event batch.");
		return builder.build();
	}

	/**
	 * Create element configuration for Groovy event decoder.
	 * 
	 * @return
	 */
	protected ElementNode createGroovyEventDecoderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Groovy Binary Event Decoder",
						EventSourcesParser.BinaryDecoders.GroovyEventDecoder.getLocalName(), "cogs",
						ElementRole.EventSource_BinaryEventDecoder);

		builder.description("Decoder that uses a Groovy script to parse a binary payload into decoded events.");
		builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String).description(
				"Relative path to script used for decoding payload.").makeRequired().build()));
		return builder.build();
	}

	/**
	 * Create element configuration for Groovy string event decoder.
	 * 
	 * @return
	 */
	protected ElementNode createGroovyStringEventDecoderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Groovy String Event Decoder",
						EventSourcesParser.StringDecoders.GroovyStringDecoder.getLocalName(), "cogs",
						ElementRole.EventSource_StringEventDecoder);

		builder.description("Decoder that uses a Groovy script to parse a String payload into decoded events.");
		builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String).description(
				"Relative path to script used for decoding payload.").makeRequired().build()));
		return builder.build();
	}

	/**
	 * Create element configuration for echo string event decoder.
	 * 
	 * @return
	 */
	protected ElementNode createEchoStringEventDecoderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Echo String Event Decoder",
						EventSourcesParser.StringDecoders.EchoStringDecoder.getLocalName(), "cogs",
						ElementRole.EventSource_StringEventDecoder);

		builder.description("Decoder for event receivers with String payloads that simply echoes the payload to the log.");
		return builder.build();
	}

	/**
	 * Create element configuration for event sources.
	 * 
	 * @return
	 */
	protected ElementNode createInboundProcessingStrategyElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Inbound Processing Strategy",
						DeviceCommunicationParser.Elements.InboundProcessingStrategy.getLocalName(), "cogs",
						ElementRole.DeviceCommunication_InboundProcessingStrategy);

		builder.description("The inbound processing strategy is responsible for moving events from event "
				+ "sources into the inbound processing chain. It is responsible for handling threading and "
				+ "reliably delivering events for processing.");
		return builder.build();
	}

	/**
	 * Create element configuration for MQTT event source.
	 * 
	 * @return
	 */
	protected ElementNode createBlockingQueueInboundStrategyElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder(
						"Blocking Queue Strategy",
						InboundProcessingStrategyParser.Elements.DefaultInboundProcessingStrategy.getLocalName(),
						"cogs", ElementRole.InboundProcessingStrategy_Strategy);

		builder.description("Send decoded messages into the processing pipeline by first adding them "
				+ "to a fixed-length queue, then using multiple threads to move events from the queue into "
				+ "the pipeline. The number of threads used very directly affects system performance since "
				+ "it determines how many events can be processed in parallel.");
		builder.attribute((new AttributeNode.Builder("Number of processing threads",
				"numEventProcessorThreads", AttributeType.Integer).description(
				"Number of threads used to process incoming events in parallel").defaultValue("100").build()));
		builder.attribute((new AttributeNode.Builder("Enable monitoring", "enableMonitoring",
				AttributeType.Boolean).description("Enable logging of monitoring statistics at an interval").build()));
		builder.attribute((new AttributeNode.Builder("Monitoring interval in seconds",
				"monitoringIntervalSec", AttributeType.Integer).description("Number of seconds to wait between logging monitoring statistics.").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for device registration.
	 * 
	 * @return
	 */
	protected ElementNode createRegistrationElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Registration Management",
						DeviceCommunicationParser.Elements.Registration.getLocalName(), "key",
						ElementRole.DeviceCommunication_Registration);

		builder.description("Manages how new devices are registered with the system.");
		return builder.build();
	}

	/**
	 * Create element configuration for default registration manager.
	 * 
	 * @return
	 */
	protected ElementNode createDefaultRegistrationManagerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Registration Manager",
						RegistrationParser.Elements.DefaultRegistrationManager.getLocalName(), "key",
						ElementRole.Registration_RegistrationManager);

		builder.description("Provides device registration management functionality.");
		builder.attribute((new AttributeNode.Builder("Allow registration of new devices", "allowNewDevices",
				AttributeType.Boolean).description(
				"Indicates whether new devices should be allowed to register with the system").defaultValue(
				"true").build()));
		builder.attribute((new AttributeNode.Builder("Automatically assign site", "autoAssignSite",
				AttributeType.Boolean).description("Indicates if a site should automatically be assigned if no site token is "
				+ "passed in registration request.").build()));
		builder.attribute((new AttributeNode.Builder("Site token", "autoAssignSiteToken",
				AttributeType.String).description("Site token used for registering new devices if auto-assign is enabled "
				+ "and no site token is passed.").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for batch operations.
	 * 
	 * @return
	 */
	protected ElementNode createBatchOperationsElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Batch Operation Management",
						DeviceCommunicationParser.Elements.BatchOperations.getLocalName(), "server",
						ElementRole.DeviceCommunication_BatchOperations);

		builder.description("Manages how batch operations are processed. Batch operations are "
				+ "actions that are executed asynchronously for many devices with the ability to monitor "
				+ "progress at both the batch and element level.");
		return builder.build();
	}

	/**
	 * Create element configuration for batch operation manager.
	 * 
	 * @return
	 */
	protected ElementNode createBatchOperationManagerElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Batch Operation Manager",
						BatchOperationsParser.Elements.DefaultBatchOperationManager.getLocalName(), "server",
						ElementRole.BatchOperations_BatchOperationManager);

		builder.description("Manages how batch operations are processed.");
		builder.attribute((new AttributeNode.Builder("Throttle delay (ms)", "throttleDelayMs",
				AttributeType.Integer).description(
				"Number of milliseconds to wait between processing elements in a "
						+ "batch operation. This throttles the output to prevent overloading the system.").defaultValue(
				"0").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for command routing.
	 * 
	 * @return
	 */
	protected ElementNode createCommandRoutingElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Command Routing",
						DeviceCommunicationParser.Elements.CommandRouting.getLocalName(),
						"sitemap fa-rotate-270", ElementRole.DeviceCommunication_CommandRouting);

		builder.description("Determines how commands are routed to command destinations.");
		return builder.build();
	}

	/**
	 * Create element configuration for specification mapping command router.
	 * 
	 * @return
	 */
	protected ElementNode createSpecificationMappingRouterElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Specification Mapping Router",
						CommandRoutingParser.Elements.SpecificationMappingRouter.getLocalName(),
						"sitemap fa-rotate-270", ElementRole.CommandRouting_SpecificationMappingRouter);

		builder.description("Routes commands based on a direct mapping from device specification token "
				+ "to a command desitination. Commands for specifications not in the mapping list are routed to "
				+ "the default destination.");
		builder.attribute((new AttributeNode.Builder("Default destination", "defaultDestination",
				AttributeType.String).description("Identifier for default destination commands should be routed to if no mapping is found.").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for specification mapping command router.
	 * 
	 * @return
	 */
	protected ElementNode createSpecificationMappingRouterMappingElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Specification Mapping", "mapping", "arrows-h",
						ElementRole.CommandRouting_SpecificationMappingRouter_Mapping);

		builder.description("Maps a specification token to a command destination that should process it.");
		builder.attribute((new AttributeNode.Builder("Specification", "specification",
				AttributeType.SpecificationReference).description("Device specification for the mapping.").makeIndex().build()));
		builder.attribute((new AttributeNode.Builder("Destination id", "destination", AttributeType.String).description("Unique id of command destination for the mapping.").build()));
		return builder.build();
	}

	/**
	 * Create element configuration for command routing.
	 * 
	 * @return
	 */
	protected ElementNode createCommandDestinationsElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Device Command Destinations",
						DeviceCommunicationParser.Elements.CommandDestinations.getLocalName(), "sign-out",
						ElementRole.DeviceCommunication_CommandDestinations);

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
		builder.attribute((new AttributeNode.Builder("Destination id", "destinationId", AttributeType.String).description(
				"Unique identifier for command destination.").makeIndex().build()));
	}

	/**
	 * Create element configuration for MQTT command destination.
	 * 
	 * @return
	 */
	protected ElementNode createMqttCommandDestinationElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("MQTT Command Destination",
						CommandDestinationsParser.Elements.MqttCommandDestination.getLocalName(), "sign-out",
						ElementRole.CommandDestinations_CommandDestination);

		builder.description("Sends commands to remote devices using the MQTT protocol. Commands are first encoded "
				+ "using a binary encoder, then a parameter extractor is used to determine the topic used "
				+ "to deliver the payload to the subscriber.");

		// Add common command destination attributes.
		addCommandDestinationAttributes(builder);

		// Add common MQTT connectivity attributes.
		addMqttConnectivityAttributes(builder);

		return builder.build();
	}

	/**
	 * Create element configuration for GPB command encoder.
	 * 
	 * @return
	 */
	protected ElementNode createProtobufCommandEncoderElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Google Protocol Buffers Command Encoder",
						CommandDestinationsParser.BinaryCommandEncoders.ProtobufEncoder.getLocalName(),
						"cogs", ElementRole.CommandDestinations_BinaryCommandEncoder);

		builder.description("Encodes a command using the default Google Protocol Buffers representation. "
				+ "The proto file for the representation can be found in the <strong>code generation</strong> "
				+ "page for the device specification.");

		return builder.build();
	}

	/**
	 * Create element configuration for hardware id parameter extractor.
	 * 
	 * @return
	 */
	protected ElementNode createHardwareIdParameterExtractorElement() {
		ElementNode.Builder builder =
				new ElementNode.Builder("Hardware Id Topic Extractor", "hardware-id-topic-extractor", "cogs",
						ElementRole.CommandDestinations_ParameterExtractor);

		builder.description("Calculates MQTT topic for publishing commands by substituting the device "
				+ "hardware id into parameterized strings. The resulting values are used by the command "
				+ "destination to send the encoded command payload to the device.");
		builder.attribute((new AttributeNode.Builder("Command topic expression", "commandTopicExpr",
				AttributeType.String).description("Expression for building topic name to which custom commands are sent. "
				+ "Add a '%s' where the hardware id should be inserted.").build()));
		builder.attribute((new AttributeNode.Builder("System topic expression", "systemTopicExpr",
				AttributeType.String).description("Expression for building topic name to which system commands are sent. "
				+ "Add a '%s' where the hardware id should be inserted.").build()));

		return builder.build();
	}
}