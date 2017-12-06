/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration;

import com.sitewhere.configuration.CommonCommunicationModel;
import com.sitewhere.configuration.model.AttributeNode;
import com.sitewhere.configuration.model.ConfigurationModel;
import com.sitewhere.configuration.model.ElementNode;
import com.sitewhere.configuration.model.ElementRoles;
import com.sitewhere.configuration.old.ICommandDestinationsParser;
import com.sitewhere.configuration.old.ICommandRoutingParser;
import com.sitewhere.configuration.old.IDeviceCommunicationParser;
import com.sitewhere.configuration.old.IDeviceServicesParser;
import com.sitewhere.configuration.parser.IBatchOperationsParser;
import com.sitewhere.configuration.parser.IEventSourcesParser;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;

/**
 * Configuration model for event sources microservice.
 * 
 * @author Derek
 */
public class EventSourcesModel extends ConfigurationModel {

    public EventSourcesModel() {
	// Event sources.
	addElement(createMqttEventSourceElement());
	addElement(createRabbitMqEventSourceElement());
	addElement(createAzureEventHubEventSourceElement());
	addElement(createActiveMQEventSourceElement());
	addElement(createActiveMQClientEventSourceElement());
	addElement(createHazelcastQueueEventSourceElement());
	addElement(createPollingRestEventSourceElement());
	addElement(createCoapServerEventSourceElement());

	// Socket event source.
	addElement(createReadAllSocketInteractionHandlerElement());
	addElement(createHttpSocketInteractionHandlerElement());
	addElement(createGroovySocketInteractionHandlerElement());
	addElement(createSocketEventSourceElement());

	// WebSocket event source.
	addElement(createWebSocketHeaderElement());
	addElement(createWebSocketEventSourceElement());

	// Binary event decoders.
	addElement(createProtobufEventDecoderElement());
	addElement(createJsonDeviceRequestDecoderElement());
	addElement(createJsonBatchEventDecoderElement());
	addElement(createGroovyEventDecoderElement());
	addElement(createCompositeEventDecoderElement());
	addElement(createCompositeEventDecoderChoicesElement());
	addElement(createDeviceSpecificationDecoderChoiceElement());
	addElement(createGroovyMetadataExtractorElement());

	// String event decoders.
	addElement(createGroovyStringEventDecoderElement());
	addElement(createEchoStringEventDecoderElement());

	// Device event deduplicators.
	addElement(createAlternateIdDeduplicatorElement());
	addElement(createGroovyEventDeduplicatorElement());

	// Device services.
	addElement(createDeviceServicesElement());
	addElement(createDefaultRegistrationManagerElement());
	addElement(createSymbolGeneratorManagerElement());
	addElement(createQRCodeSymbolGeneratorElement());
	addElement(createDefaultPresenceManagerElement());

	// Batch operations.
	addElement(createBatchOperationsElement());
	addElement(createBatchOperationManagerElement());

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

    /**
     * Create element configuration for MQTT event source.
     * 
     * @return
     */
    protected ElementNode createMqttEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MQTT Event Source",
		IEventSourcesParser.Elements.MqttEventSource.getLocalName(), "sign-in",
		EventSourcesElementRoles.EventSources_EventSource);

	builder.description("Listen for events on an MQTT topic.");
	addEventSourceAttributes(builder);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesElementRoles.EventSource_EventDecoder,
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	// Add common MQTT connectivity attributes.
	CommonCommunicationModel.addMqttConnectivityAttributes(builder);
	builder.attribute((new AttributeNode.Builder("MQTT topic", "topic", AttributeType.String)
		.description("MQTT topic event source uses for inbound messages.").build()));

	return builder.build();
    }

    /**
     * Create element configuration for MQTT event source.
     * 
     * @return
     */
    protected ElementNode createRabbitMqEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("RabbitMQ Event Source",
		IEventSourcesParser.Elements.RabbitMqEventSource.getLocalName(), "sign-in",
		EventSourcesElementRoles.EventSources_EventSource);

	builder.description("Listen for events on an RabbitMQ queue.");
	addEventSourceAttributes(builder);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesElementRoles.EventSource_EventDecoder,
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Connection URI", "connectionUri", AttributeType.String)
		.defaultValue("amqp://localhost").description("URI that specifies RabbitMQ connectivity settings.")
		.build()));
	builder.attribute((new AttributeNode.Builder("Queue name", "queueName", AttributeType.String)
		.defaultValue("sitewhere.input").description("Name of queue that will be consumed.").build()));
	builder.attribute(
		(new AttributeNode.Builder("Durable queue", "durable", AttributeType.Boolean).defaultValue("false")
			.description("Indicates if queue should survive broker restart. If queue exists, this "
				+ "should agree with the existing configuration.")
			.build()));
	builder.attribute(
		(new AttributeNode.Builder("Consumer threads", "numConsumers", AttributeType.Integer).defaultValue("5")
			.description("Number of thread used by consumers to pull data from the queue.").build()));

	return builder.build();
    }

    /**
     * Add common event source attributes.
     * 
     * @param builder
     */
    public static void addEventSourceAttributes(ElementNode.Builder builder) {
	builder.attribute((new AttributeNode.Builder("Source id", "sourceId", AttributeType.String)
		.description("Unique id used for referencing this event source.").makeIndex().makeRequired().build()));
    }

    /**
     * Create element configuration for Azure EventHub event source.
     * 
     * @return
     */
    protected ElementNode createAzureEventHubEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Azure EventHub Event Source",
		IEventSourcesParser.Elements.AzureEventHubEventSource.getLocalName(), "cloud",
		EventSourcesElementRoles.EventSources_EventSource);

	builder.description(
		"Event source that pulls binary information from an Azure EventHub endpoint and decodes it.");
	addEventSourceAttributes(builder);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesElementRoles.EventSource_EventDecoder,
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Target FQN", "targetFqn", AttributeType.String)
		.description("EventHub targetFqn address.").build()));
	builder.attribute((new AttributeNode.Builder("Namespace", "namespace", AttributeType.String)
		.description("EventHub namespace.").build()));
	builder.attribute((new AttributeNode.Builder("Entity path", "entityPath", AttributeType.String)
		.description("EventHub entityPath.").build()));
	builder.attribute((new AttributeNode.Builder("Partition count", "partitionCount", AttributeType.Integer)
		.description("EventHub partition count.").build()));
	builder.attribute((new AttributeNode.Builder("Zookeeper state store", "zkStateStore", AttributeType.String)
		.description("Zookeeper store url for EventHub state persistence.").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String)
		.description("Username for EventHub connection.").build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String)
		.description("Password for EventHub connection.").build()));

	return builder.build();
    }

    /**
     * Create element configuration for ActiveMQ event source.
     * 
     * @return
     */
    protected ElementNode createActiveMQEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("ActiveMQ Event Source",
		IEventSourcesParser.Elements.ActiveMQEventSource.getLocalName(), "sign-in",
		EventSourcesElementRoles.EventSources_EventSource);

	builder.description("Event source that pulls binary information from an ActiveMQ queue and decodes it.");
	addEventSourceAttributes(builder);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesElementRoles.EventSource_EventDecoder,
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Transport URI", "transportUri", AttributeType.String)
		.description("URI used to configure the trasport for the embedded ActiveMQ broker.").makeRequired()
		.build()));
	builder.attribute((new AttributeNode.Builder("Data directory", "dataDirectory", AttributeType.String)
		.description("Data directory used to store persistent message queues.").build()));
	builder.attribute((new AttributeNode.Builder("Queue name", "queueName", AttributeType.String)
		.description("Name of JMS queue for consumers to pull messages from.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Number of consumers", "numConsumers", AttributeType.Integer)
		.description("Number of consumers used to read data from the queue into SiteWhere.").build()));

	return builder.build();
    }

    /**
     * Create element configuration for ActiveMQ client event source.
     * 
     * @return
     */
    protected ElementNode createActiveMQClientEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("ActiveMQ Client Event Source",
		IEventSourcesParser.Elements.ActiveMQClientEventSource.getLocalName(), "sign-in",
		EventSourcesElementRoles.EventSources_EventSource);

	builder.description("Event source that uses ActiveMQ consumers to ingest "
		+ "messages from a remote broker and decodes them.");
	addEventSourceAttributes(builder);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesElementRoles.EventSource_EventDecoder,
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Remote URI", "remoteUri", AttributeType.String)
		.description("URI used to connect to remote message broker.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Queue name", "queueName", AttributeType.String)
		.description("Name of JMS queue for consumers to pull messages from.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Number of consumers", "numConsumers", AttributeType.Integer)
		.description("Number of consumers used to read data from the queue into SiteWhere.").build()));

	return builder.build();
    }

    /**
     * Create read-all socket interaction handler factory.
     * 
     * @return
     */
    protected ElementNode createReadAllSocketInteractionHandlerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Read-All Socket Interaction Handler Factory",
		IEventSourcesParser.BinarySocketInteractionHandlers.ReadAllInteractionHandlerFactory.getLocalName(),
		"cog", EventSourcesElementRoles.EventSources_SocketInteractionHandlerFactory);

	builder.description("Interaction handler that reads all content from the client socket and delivers it "
		+ "to the decoder as a byte array.");

	return builder.build();
    }

    /**
     * Create HTTP socket interaction handler factory.
     * 
     * @return
     */
    protected ElementNode createHttpSocketInteractionHandlerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("HTTP Socket Interaction Handler Factory",
		IEventSourcesParser.BinarySocketInteractionHandlers.HttpInteractionHandlerFactory.getLocalName(), "cog",
		EventSourcesElementRoles.EventSources_SocketInteractionHandlerFactory);

	builder.description("Interaction handler that reads HTTP content from the client socket and delivers "
		+ "the wrapped entity to the decoder as binary data. This interaction handler "
		+ "always returns a 200 OK response on the socket.");

	return builder.build();
    }

    /**
     * Create Groovy socket interaction handler factory.
     * 
     * @return
     */
    protected ElementNode createGroovySocketInteractionHandlerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Socket Interaction Handler Factory",
		IEventSourcesParser.BinarySocketInteractionHandlers.GroovySocketInteractionHandlerFactory
			.getLocalName(),
		"cog", EventSourcesElementRoles.EventSources_SocketInteractionHandlerFactory);

	builder.description("Interaction handler uses a Groovy script to handle socket interactions.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Path to Groovy script which handles socket interactions.").makeRequired().build()));

	return builder.build();
    }

    /**
     * Create element configuration for socket event source.
     * 
     * @return
     */
    protected ElementNode createSocketEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Socket Event Source",
		IEventSourcesParser.Elements.SocketEventSource.getLocalName(), "plug",
		EventSourcesElementRoles.EventSources_SocketEventSource);

	builder.description("Event source that pulls binary information from connections to a TCP/IP server socket.");
	addEventSourceAttributes(builder);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesElementRoles.EventSource_EventDecoder,
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Port", "port", AttributeType.Integer)
		.description("Port on which the server socket will listen.").defaultValue("8484").makeRequired()
		.build()));
	builder.attribute((new AttributeNode.Builder("Number of threads", "numThreads", AttributeType.Integer)
		.description("Number of threads used to handle client connections to the server socket.")
		.defaultValue("5").build()));

	return builder.build();
    }

    /**
     * Create element configuration for WebSocket header.
     * 
     * @return
     */
    protected ElementNode createWebSocketHeaderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("WebSocket Header", "header", "cog",
		EventSourcesElementRoles.EventSources_WebSocketHeader);

	builder.description("Header that is passed to the web socket for configuration.");

	builder.attribute((new AttributeNode.Builder("Header name", "name", AttributeType.String)
		.description("Header name.").makeRequired().makeIndex().build()));
	builder.attribute((new AttributeNode.Builder("Header value", "value", AttributeType.String)
		.description("Header value.").makeRequired().build()));

	return builder.build();
    }

    /**
     * Create element configuration for WebSocket event source.
     * 
     * @return
     */
    protected ElementNode createWebSocketEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("WebSocket Event Source",
		IEventSourcesParser.Elements.WebSocketEventSource.getLocalName(), "plug",
		EventSourcesElementRoles.EventSources_WebSocketEventSource);

	builder.description("Event source that pulls data from a web socket. Note that the event decoder needs "
		+ "to correspond to the payload type chosen.");
	addEventSourceAttributes(builder);

	builder.attribute((new AttributeNode.Builder("Web socket URL", "webSocketUrl", AttributeType.String)
		.description("URL of the web socket to connect to.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Payload type", "payloadType", AttributeType.String)
		.description("Chooses whether payload is processed as binary or string.").choice("binary")
		.choice("string").defaultValue("binary").build()));

	return builder.build();
    }

    /**
     * Create element configuration for Hazelcast queue event source.
     * 
     * @return
     */
    protected ElementNode createHazelcastQueueEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Hazelcast Queue Event Source",
		IEventSourcesParser.Elements.HazelcastQueueEventSource.getLocalName(), "sign-in",
		EventSourcesElementRoles.EventSources_EventSource);

	builder.description("Event source that pulls decoded events from a Hazelcast queue. Primarily used to "
		+ "allow one instance of SiteWhere to decode events and feed them to multiple subordinate instances for processing.");
	addEventSourceAttributes(builder);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesElementRoles.EventSource_EventDecoder,
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	return builder.build();
    }

    /**
     * Create element configuration for CoAP server event source.
     * 
     * @return
     */
    protected ElementNode createCoapServerEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("CoAP Server Event Source",
		IEventSourcesParser.Elements.CoapServerEventSource.getLocalName(), "sign-in",
		EventSourcesElementRoles.EventSources_EventSource);

	builder.description("Event source that acts as a CoAP server, allowing events to be created "
		+ "by posting data to well-known system URLs.");
	addEventSourceAttributes(builder);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesElementRoles.EventSource_EventDecoder,
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Hostname", "hostname", AttributeType.String)
		.description("Host name used when binding server socket.").defaultValue("localhost").build()));
	builder.attribute((new AttributeNode.Builder("Port", "port", AttributeType.Integer)
		.description("Port used when binding server socket.").defaultValue("5683").build()));

	return builder.build();
    }

    /**
     * Create element configuration for Hazelcast queue event source.
     * 
     * @return
     */
    protected ElementNode createPollingRestEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Polling REST Event Source",
		IEventSourcesParser.Elements.PollingRestEventSource.getLocalName(), "sign-in",
		EventSourcesElementRoles.EventSources_EventSource);

	builder.description("Event source that polls a REST service at a given interval to generate payloads. "
		+ "A groovy script is used to make the REST call(s) and parse the responses "
		+ "into payloads to be decoded.");
	addEventSourceAttributes(builder);
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Path to Groovy script which makes REST calls and parses responses.").makeRequired()
		.build()));
	builder.attribute((new AttributeNode.Builder("Base REST url", "baseUrl", AttributeType.String)
		.description(
			"Base URL for REST calls. All calls in the Groovy script are made " + "relative to this URL.")
		.makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Polling interval (ms)", "pollIntervalMs", AttributeType.Integer)
		.description("Time interval (in milliseconds) to wait between script executions.").makeRequired()
		.defaultValue("10000").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String)
		.description("Username used for basic authentication.").build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String)
		.description("Password used for basic authentication.").build()));

	// Only accept binary event decoders.
	builder.specializes(EventSourcesElementRoles.EventSource_EventDecoder,
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	return builder.build();
    }

    /**
     * Create element configuration for protobuf event decoder.
     * 
     * @return
     */
    protected ElementNode createProtobufEventDecoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Google Protocol Buffers Event Decoder",
		IEventSourcesParser.BinaryDecoders.ProtobufDecoder.getLocalName(), "cogs",
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

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
    protected ElementNode createJsonDeviceRequestDecoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("JSON Device Request Decoder",
		IEventSourcesParser.BinaryDecoders.JsonDeviceRequestDecoder.getLocalName(), "cogs",
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	builder.description("Event decoder that takes binary messages from an underlying transport "
		+ "and parses them as the JSON representation of a request from a device.");
	return builder.build();
    }

    /**
     * Create element configuration for JSON event decoder.
     * 
     * @return
     */
    protected ElementNode createJsonBatchEventDecoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("JSON Batch Event Decoder",
		IEventSourcesParser.BinaryDecoders.JsonBatchEventDecoder.getLocalName(), "cogs",
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	builder.description("Event decoder that takes binary messages from an underlying transport "
		+ "and parses them as the JSON representation of a batch of device events.");
	return builder.build();
    }

    /**
     * Create element configuration for Groovy event decoder.
     * 
     * @return
     */
    protected ElementNode createGroovyEventDecoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Binary Event Decoder",
		IEventSourcesParser.BinaryDecoders.GroovyEventDecoder.getLocalName(), "cogs",
		EventSourcesElementRoles.EventSource_BinaryEventDecoder);

	builder.description("Decoder that uses a Groovy script to parse a binary payload into decoded events.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Relative path to script used for decoding payload.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create element configuration for Groovy string event decoder.
     * 
     * @return
     */
    protected ElementNode createGroovyStringEventDecoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy String Event Decoder",
		IEventSourcesParser.StringDecoders.GroovyStringDecoder.getLocalName(), "cogs",
		EventSourcesElementRoles.EventSource_StringEventDecoder);

	builder.description("Decoder that uses a Groovy script to parse a String payload into decoded events.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Relative path to script used for decoding payload.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create element configuration for echo string event decoder.
     * 
     * @return
     */
    protected ElementNode createEchoStringEventDecoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Echo String Event Decoder",
		IEventSourcesParser.StringDecoders.EchoStringDecoder.getLocalName(), "cogs",
		EventSourcesElementRoles.EventSource_StringEventDecoder);

	builder.description(
		"Decoder for event receivers with String payloads that simply echoes the payload to the log.");
	return builder.build();
    }

    /**
     * Create element configuration for composite event decoder.
     * 
     * @return
     */
    protected ElementNode createCompositeEventDecoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Composite Event Decoder (Binary)",
		IEventSourcesParser.BinaryDecoders.CompositeDecoder.getLocalName(), "cogs",
		EventSourcesElementRoles.EventSource_CompositeEventDecoder);

	builder.description("Decoder that extracts device metadata from the binary payload, then delegates "
		+ "further decoding to a list of sub-decoders, which may be invoked if criteria are met.");
	return builder.build();
    }

    /**
     * Create element configuration for composite event decoder.
     * 
     * @return
     */
    protected ElementNode createCompositeEventDecoderChoicesElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Composite Event Decoder Choices", "choices", "cogs",
		EventSourcesElementRoles.CompositeEventDecoder_DecoderChoices);

	builder.description("List of decoder choices avaliable for parsing the payload. The first choice to "
		+ "match based on the given criteria is used to decode the payload.");
	return builder.build();
    }

    /**
     * Create element configuration for Groovy metadata extractor.
     * 
     * @return
     */
    protected ElementNode createDeviceSpecificationDecoderChoiceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Match Device Specification",
		IEventSourcesParser.CompositeDecoderChoiceElements.DeviceSpecificationDecoderChoice.getLocalName(),
		"cogs", EventSourcesElementRoles.CompositeEventDecoder_DecoderChoice);

	builder.description("Composite event decoder choice that applies when the device specification from the "
		+ " extracted metadata matches the specified value. This allows payload processing to be directly "
		+ " based on the specification assigned in SiteWhere device management.");
	builder.attribute(
		(new AttributeNode.Builder("Specification token", "token", AttributeType.SpecificationReference)
			.description("Specification token to match.").makeIndex().makeRequired().build()));
	return builder.build();
    }

    /**
     * Create element configuration for Groovy metadata extractor.
     * 
     * @return
     */
    protected ElementNode createGroovyMetadataExtractorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Metadata Extractor (Binary)",
		IEventSourcesParser.CompositeDecoderMetadataExtractorElements.GroovyDeviceMetadataExtractor
			.getLocalName(),
		"cogs", EventSourcesElementRoles.CompositeEventDecoder_MetadataExtractor);

	builder.description("Metadata extractor that uses a Groovy script to parse a binary payload and extract "
		+ " information such as the unique hardware id and payload. This data will be forwarded to the list "
		+ " of nested decoder choices for further processing.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Relative path to script used for extracting metadata.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create element configuration for alternate id deduplicator.
     * 
     * @return
     */
    protected ElementNode createAlternateIdDeduplicatorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Alternate Id Deduplicator",
		IEventSourcesParser.Deduplicators.AlternateIdDeduplicator.getLocalName(), "cogs",
		EventSourcesElementRoles.EventSource_EventDeduplicator);

	builder.description("Deduplicator that uses the event alternate id to test for duplicates.");
	return builder.build();
    }

    /**
     * Create element configuration for Groovy event deduplicator.
     * 
     * @return
     */
    protected ElementNode createGroovyEventDeduplicatorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Groovy Event Deduplicator",
		IEventSourcesParser.Deduplicators.GroovyEventDeduplicator.getLocalName(), "cogs",
		EventSourcesElementRoles.EventSource_EventDeduplicator);

	builder.description("Deduplicator that uses a Groovy script to check for duplicate events.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Relative path to script used for testing for duplicates.").makeRequired().build()));
	return builder.build();
    }

    /**
     * Create element configuration for device registration.
     * 
     * @return
     */
    protected ElementNode createDeviceServicesElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Device Interaction Services",
		IDeviceCommunicationParser.Elements.DeviceServices.getLocalName(), "star",
		ElementRoles.DeviceCommunication_DeviceServices);

	builder.description("Manages services that control various types of device interactions. "
		+ "This includes how new devices are registered with the system, how symbols such "
		+ "as QR-codes are associated with SiteWhere entities, and how device presence "
		+ "calculations are performed.");
	return builder.build();
    }

    /**
     * Create element configuration for default registration manager.
     * 
     * @return
     */
    protected ElementNode createDefaultRegistrationManagerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Registration Manager",
		IDeviceServicesParser.Elements.DefaultRegistrationManager.getLocalName(), "key",
		ElementRoles.DeviceServices_RegistrationManager);

	builder.description("Provides device registration management functionality.");
	builder.attribute((new AttributeNode.Builder("Allow registration of new devices", "allowNewDevices",
		AttributeType.Boolean)
			.description("Indicates whether new devices should be allowed to register with the system")
			.defaultValue("true").build()));
	builder.attribute(
		(new AttributeNode.Builder("Automatically assign site", "autoAssignSite", AttributeType.Boolean)
			.description("Indicates if a site should automatically be assigned if no site token is "
				+ "passed in registration request.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Site token", "autoAssignSiteToken", AttributeType.String)
		.description("Site token used for registering new devices if auto-assign is enabled "
			+ "and no site token is passed.")
		.build()));
	return builder.build();
    }

    /**
     * Create element configuration for device registration.
     * 
     * @return
     */
    protected ElementNode createSymbolGeneratorManagerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Symbol Generator Manager",
		IDeviceServicesParser.Elements.SymbolGeneratorManager.getLocalName(), "qrcode",
		ElementRoles.DeviceServices_SymbolGeneratorManager);

	builder.description("Manages how symbols such as QR-Codes are generated for devices "
		+ "and other SiteWhere entities. Generated symbol images are made available via "
		+ "the REST services.");
	return builder.build();
    }

    /**
     * Create element configuration for default registration manager.
     * 
     * @return
     */
    protected ElementNode createQRCodeSymbolGeneratorElement() {
	ElementNode.Builder builder = new ElementNode.Builder("QR-Code Symbol Generator",
		IDeviceServicesParser.SymbolGenerators.QRCodeSymbolGenerator.getLocalName(), "qrcode",
		ElementRoles.SymbolGeneratorManager_SymbolGenerator);

	builder.description("Generates QR-Codes for devices and other SiteWhere entities. The generated "
		+ "images are available via the REST services.");
	builder.attribute((new AttributeNode.Builder("Unique generator id", "id", AttributeType.String)
		.description("Each symbol generator must have a unique id").build()));
	builder.attribute((new AttributeNode.Builder("Generator name", "name", AttributeType.String).makeIndex()
		.description("Name shown in user interface for symbol generator.").build()));
	builder.attribute((new AttributeNode.Builder("QR-Code image width", "width", AttributeType.Integer)
		.description("Width of QR-code image in pixels.").defaultValue("200").build()));
	builder.attribute((new AttributeNode.Builder("QR-Code image height", "height", AttributeType.Integer)
		.description("Height of QR-code image in pixels.").defaultValue("200").build()));
	builder.attribute((new AttributeNode.Builder("Background color", "backgroundColor", AttributeType.String)
		.description("Background color of QR-Code image in AARRGGBB format.").defaultValue("FFFFFFFF")
		.build()));
	builder.attribute((new AttributeNode.Builder("Foreground color", "foregroundColor", AttributeType.String)
		.description("Foreground color of QR-Code image in AARRGGBB format.").defaultValue("FF333333")
		.build()));
	return builder.build();
    }

    /**
     * Create element configuration for default presence manager.
     * 
     * @return
     */
    protected ElementNode createDefaultPresenceManagerElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Default Presence Manager",
		IDeviceServicesParser.Elements.DefaultPresenceManager.getLocalName(), "bullseye",
		ElementRoles.DeviceServices_PresenceManager);

	builder.description("Determines device presence information by monitoring the last interaction date"
		+ "for the device and firing an event if too much time has elapsed.");
	builder.attribute((new AttributeNode.Builder("Check interval", "checkInterval", AttributeType.String)
		.description("Time duration (ISO8601 or \"1h 10m 30s\" format) that indicates amount of time to "
			+ "to wait between performing presence checks.")
		.defaultValue("10m").build()));
	builder.attribute(
		(new AttributeNode.Builder("Presence missing interval", "presenceMissingInterval", AttributeType.String)
			.description("Time duration (ISO8601 or \"2d 5h 10m\" format) that indicates amount of time to "
				+ "since last interaction with a device to consider it non-present.")
			.defaultValue("8h").build()));
	return builder.build();
    }

    /**
     * Create element configuration for batch operations.
     * 
     * @return
     */
    protected ElementNode createBatchOperationsElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Batch Operation Management",
		IDeviceCommunicationParser.Elements.BatchOperations.getLocalName(), "server",
		ElementRoles.DeviceCommunication_BatchOperations);

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
	ElementNode.Builder builder = new ElementNode.Builder("Batch Operation Manager",
		IBatchOperationsParser.Elements.DefaultBatchOperationManager.getLocalName(), "server",
		ElementRoles.BatchOperations_BatchOperationManager);

	builder.description("Manages how batch operations are processed.");
	builder.attribute((new AttributeNode.Builder("Throttle delay (ms)", "throttleDelayMs", AttributeType.Integer)
		.description("Number of milliseconds to wait between processing elements in a "
			+ "batch operation. This throttles the output to prevent overloading the system.")
		.defaultValue("0").build()));
	return builder.build();
    }

    /**
     * Create element configuration for command routing.
     * 
     * @return
     */
    protected ElementNode createCommandRoutingElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Device Command Routing",
		IDeviceCommunicationParser.Elements.CommandRouting.getLocalName(), "sitemap",
		ElementRoles.DeviceCommunication_CommandRouting);

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
		ElementRoles.CommandRouting_SpecificationMappingRouter);

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
		ElementRoles.CommandRouting_CommandRouter);

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
		ElementRoles.CommandRouting_SpecificationMappingRouter_Mapping);

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
		ElementRoles.DeviceCommunication_CommandDestinations);

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
		ElementRoles.CommandDestinations_CommandDestination);

	builder.description("Sends commands to remote devices using the MQTT protocol. Commands are first encoded "
		+ "using a binary encoder, then a parameter extractor is used to determine the topic used "
		+ "to deliver the payload to the subscriber.");

	// Add common command destination attributes.
	addCommandDestinationAttributes(builder);

	// Only allow binary command encoders.
	builder.specializes(ElementRoles.CommandDestinations_CommandEncoder,
		ElementRoles.CommandDestinations_BinaryCommandEncoder);

	// Only allow MQTT parameter extractors
	builder.specializes(ElementRoles.CommandDestinations_ParameterExtractor,
		ElementRoles.CommandDestinations_MqttParameterExtractor);

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
		ElementRoles.CommandDestinations_CommandDestination);

	builder.description("Sends commands to remote devices using the CoAP protocol. Commands are first encoded "
		+ "using a binary encoder, then a parameter extractor is used to determine the connection "
		+ "information to make a client request to the device.");

	// Add common command destination attributes.
	addCommandDestinationAttributes(builder);

	// Only allow binary command encoders.
	builder.specializes(ElementRoles.CommandDestinations_CommandEncoder,
		ElementRoles.CommandDestinations_BinaryCommandEncoder);

	// Only allow MQTT parameter extractors
	builder.specializes(ElementRoles.CommandDestinations_ParameterExtractor,
		ElementRoles.CommandDestinations_CoapParameterExtractor);

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
		ElementRoles.CommandDestinations_CommandDestination);

	builder.description("Destination that delivers commands via Twilio SMS messages.");

	// Add common command destination attributes.
	addCommandDestinationAttributes(builder);

	// Only allow String command encoders.
	builder.specializes(ElementRoles.CommandDestinations_CommandEncoder,
		ElementRoles.CommandDestinations_StringCommandEncoder);

	// Only allow SMS parameter extractors
	builder.specializes(ElementRoles.CommandDestinations_ParameterExtractor,
		ElementRoles.CommandDestinations_SmsParameterExtractor);

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
		ElementRoles.CommandDestinations_BinaryCommandEncoder);

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
		ElementRoles.CommandDestinations_BinaryCommandEncoder);

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
		ElementRoles.CommandDestinations_BinaryCommandEncoder);

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
		ElementRoles.CommandDestinations_BinaryCommandEncoder);

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
		ElementRoles.CommandDestinations_StringCommandEncoder);

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
		"hardware-id-topic-extractor", "cogs", ElementRoles.CommandDestinations_MqttParameterExtractor);

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
		"metadata-coap-parameter-extractor", "cogs", ElementRoles.CommandDestinations_CoapParameterExtractor);

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
		"groovy-sms-parameter-extractor", "cogs", ElementRoles.CommandDestinations_SmsParameterExtractor);

	builder.description("Uses a Groovy script to extract SMS parameter information for delivering a command.");
	builder.attribute((new AttributeNode.Builder("Script path", "scriptPath", AttributeType.String)
		.description("Path to Groovy script which encodes commands.").makeRequired().build()));

	return builder.build();
    }
}