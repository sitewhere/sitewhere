/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration;

import com.sitewhere.configuration.CommonConnectorModel;
import com.sitewhere.configuration.model.ConfigurationModelProvider;
import com.sitewhere.configuration.parser.IEventSourcesParser;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IAttributeGroup;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationRoleProvider;

/**
 * Configuration model provider for event sources microservice.
 * 
 * @author Derek
 */
public class EventSourcesModelProvider extends ConfigurationModelProvider {

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getDefaultXmlNamespace()
     */
    @Override
    public String getDefaultXmlNamespace() {
	return "http://sitewhere.io/schema/sitewhere/microservice/event-sources";
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#getRootRole()
     */
    @Override
    public IConfigurationRoleProvider getRootRole() {
	return EventSourcesRoles.EventSources;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeElements()
     */
    @Override
    public void initializeElements() {
	// Event sources.
	addElement(createEventSourcesElement());
	addElement(createMqttEventSourceElement());
	addElement(createRabbitMqEventSourceElement());
	addElement(createAzureEventHubEventSourceElement());
	addElement(createActiveMQBrokerEventSourceElement());
	addElement(createActiveMQClientEventSourceElement());
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

	// CoAP event decoders.
	addElement(createCoapJsonEventDecoderElement());

	// Device event deduplicators.
	addElement(createAlternateIdDeduplicatorElement());
	addElement(createGroovyEventDeduplicatorElement());
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.
     * IConfigurationModelProvider#initializeRoles()
     */
    @Override
    public void initializeRoles() {
	for (EventSourcesRoles role : EventSourcesRoles.values()) {
	    getRolesById().put(role.getRole().getKey().getId(), role.getRole());
	}
    }

    /**
     * Create event sources element.
     * 
     * @return
     */
    protected ElementNode createEventSourcesElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Event Sources", IEventSourcesParser.ROOT, "sign-in",
		EventSourcesRoleKeys.EventSources, this);

	builder.description(
		"Event sources are responsible for acquiring device event data from external devices or systems.");

	return builder.build();
    }

    /**
     * Add common event source attributes.
     * 
     * @param builder
     */
    public static void addEventSourceAttributes(ElementNode.Builder builder, IAttributeGroup group) {
	builder.attribute((new AttributeNode.Builder("Source id", "sourceId", AttributeType.String, group)
		.description("Unique id used for referencing this event source.").makeIndex().makeRequired().build()));
    }

    /**
     * Create element configuration for MQTT event source.
     * 
     * @return
     */
    protected ElementNode createMqttEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MQTT Event Source",
		IEventSourcesParser.Elements.MqttEventSource.getLocalName(), "sign-in",
		EventSourcesRoleKeys.EventSource, this);

	builder.description("Listen for events on an MQTT topic.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_AUTHENTICATION);
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_PERFORMANCE);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.BinaryEventDecoder);

	// Add common MQTT connectivity attributes.
	builder.attribute((new AttributeNode.Builder("MQTT topic", "topic", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("MQTT topic event source uses for inbound messages.").build()));
	CommonConnectorModel.addMqttCommonAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY,
		ConfigurationModelProvider.ATTR_GROUP_AUTHENTICATION);
	builder.attribute((new AttributeNode.Builder("Processing threads", "numThreads", AttributeType.Integer,
		ATTR_GROUP_PERFORMANCE).defaultValue("5")
			.description("Number of threads used for processing MQTT payloads.").build()));

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
		EventSourcesRoleKeys.EventSource, this);

	builder.description("Listen for events on an RabbitMQ queue.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Connection URI", "connectionUri", AttributeType.String,
		ATTR_GROUP_CONNECTIVITY).defaultValue("amqp://localhost")
			.description("URI that specifies RabbitMQ connectivity settings.").build()));
	builder.attribute((new AttributeNode.Builder("Queue name", "queueName", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).defaultValue("sitewhere.input")
			.description("Name of queue that will be consumed.").build()));
	builder.attribute((new AttributeNode.Builder("Durable queue", "durable", AttributeType.Boolean,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).defaultValue("false")
			.description("Indicates if queue should survive broker restart. If queue exists, this "
				+ "should agree with the existing configuration.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Consumer threads", "numConsumers", AttributeType.Integer,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).defaultValue("5")
			.description("Number of thread used by consumers to pull data from the queue.").build()));

	return builder.build();
    }

    /**
     * Create element configuration for Azure EventHub event source.
     * 
     * @return
     */
    protected ElementNode createAzureEventHubEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Azure EventHub Event Source",
		IEventSourcesParser.Elements.AzureEventHubEventSource.getLocalName(), "cloud",
		EventSourcesRoleKeys.EventSource, this);

	builder.description(
		"Event source that pulls binary information from an Azure EventHub endpoint and decodes it.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Target FQN", "targetFqn", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("EventHub targetFqn address.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Namespace", "namespace", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("EventHub namespace.").build()));
	builder.attribute((new AttributeNode.Builder("Entity path", "entityPath", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("EventHub entityPath.").build()));
	builder.attribute((new AttributeNode.Builder("Partition count", "partitionCount", AttributeType.Integer,
		ATTR_GROUP_CONNECTIVITY).description("EventHub partition count.").build()));
	builder.attribute((new AttributeNode.Builder("Zookeeper state store", "zkStateStore", AttributeType.String,
		ATTR_GROUP_CONNECTIVITY).description("Zookeeper store url for EventHub state persistence.").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("Username for EventHub connection.")
			.build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("Password for EventHub connection.")
			.build()));

	return builder.build();
    }

    /**
     * Create element configuration for ActiveMQ event source.
     * 
     * @return
     */
    protected ElementNode createActiveMQBrokerEventSourceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("ActiveMQ Broker Event Source",
		IEventSourcesParser.Elements.ActiveMQEventSource.getLocalName(), "sign-in",
		EventSourcesRoleKeys.EventSource, this);

	builder.description("Event source that starts an ActiveMQ broker and listens on the transport "
		+ "via an ActiveMQ queue and decodes it.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Transport URI", "transportUri", AttributeType.String,
		ATTR_GROUP_CONNECTIVITY)
			.description("URI used to configure the transport for the embedded ActiveMQ broker.")
			.makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Queue name", "queueName", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("Name of JMS queue for consumers to pull messages from.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Number of consumers", "numConsumers", AttributeType.Integer,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
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
		EventSourcesRoleKeys.EventSource, this);

	builder.description("Event source that uses ActiveMQ consumers to ingest "
		+ "messages from a remote broker and decodes them.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Remote URI", "remoteUri", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("URI used to connect to remote message broker.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Queue name", "queueName", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("Name of JMS queue for consumers to pull messages from.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Number of consumers", "numConsumers", AttributeType.Integer,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
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
		"cog", EventSourcesRoleKeys.SocketInteractionHandlerFactory, this);

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
		EventSourcesRoleKeys.SocketInteractionHandlerFactory, this);

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
		"cog", EventSourcesRoleKeys.SocketInteractionHandlerFactory, this);

	builder.description("Interaction handler uses a Groovy script to handle socket interactions.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	builder.attribute((new AttributeNode.Builder("Script Id", "scriptId", AttributeType.Script,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Script which handles socket interactions.")
			.makeRequired().build()));

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
		EventSourcesRoleKeys.SocketEventSource, this);

	builder.description("Event source that pulls binary information from connections to a TCP/IP server socket.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	// Only accept binary event decoders.
	builder.specializes(EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.BinaryEventDecoder);

	builder.attribute((new AttributeNode.Builder("Port", "port", AttributeType.Integer,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("Port on which the server socket will listen.").defaultValue("8484").makeRequired()
			.build()));
	builder.attribute((new AttributeNode.Builder("Number of threads", "numThreads", AttributeType.Integer,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
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
		EventSourcesRoleKeys.WebSocketHeader, this);

	builder.description("Header that is passed to the web socket for configuration.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	builder.attribute((new AttributeNode.Builder("Header name", "name", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Header name.").makeRequired().makeIndex()
			.build()));
	builder.attribute((new AttributeNode.Builder("Header value", "value", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Header value.").makeRequired().build()));

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
		EventSourcesRoleKeys.WebSocketEventSource, this);

	builder.description("Event source that pulls data from a web socket. Note that the event decoder needs "
		+ "to correspond to the payload type chosen.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Web socket URL", "webSocketUrl", AttributeType.String,
		ATTR_GROUP_CONNECTIVITY).description("URL of the web socket to connect to.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Payload type", "payloadType", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("Chooses whether payload is processed as binary or string.")
			.choice("Binary", "binary").choice("String", "string").defaultValue("binary").build()));

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
		EventSourcesRoleKeys.CoapServerEventSource, this);

	builder.description("Event source that acts as a CoAP server, allowing events to be created "
		+ "by posting data to well-known system URLs.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	// Only accept CoAP event decoders.
	builder.specializes(EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.CoapEventDecoder);

	builder.attribute((new AttributeNode.Builder("Port", "port", AttributeType.Integer,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description("Port used when binding server socket.")
			.defaultValue("5683").build()));

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
		EventSourcesRoleKeys.EventSource, this);

	builder.description("Event source that polls a REST service at a given interval to generate payloads. "
		+ "A groovy script is used to make the REST call(s) and parse the responses "
		+ "into payloads to be decoded.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY);

	builder.attribute((new AttributeNode.Builder("Script Id", "scriptId", AttributeType.Script,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("Script which makes REST calls and parses responses.").makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Base REST url", "baseUrl", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY).description(
			"Base URL for REST calls. All calls in the Groovy script are made " + "relative to this URL.")
			.makeRequired().build()));
	builder.attribute((new AttributeNode.Builder("Polling interval (ms)", "pollIntervalMs", AttributeType.Integer,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("Time interval (in milliseconds) to wait between script executions.")
			.makeRequired().defaultValue("10000").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("Username used for basic authentication.").build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String,
		ConfigurationModelProvider.ATTR_GROUP_CONNECTIVITY)
			.description("Password used for basic authentication.").build()));

	// Only accept binary event decoders.
	builder.specializes(EventSourcesRoleKeys.EventDecoder, EventSourcesRoleKeys.BinaryEventDecoder);

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
		EventSourcesRoleKeys.BinaryEventDecoder, this);

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
		EventSourcesRoleKeys.BinaryEventDecoder, this);

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
		EventSourcesRoleKeys.BinaryEventDecoder, this);

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
		EventSourcesRoleKeys.BinaryEventDecoder, this);

	builder.description("Decoder that uses a Groovy script to parse a binary payload into decoded events.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	builder.attribute((new AttributeNode.Builder("Script Id", "scriptId", AttributeType.Script,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Script used for decoding payload.")
			.makeRequired().build()));
	return builder.build();
    }

    /**
     * Create element configuration for CoAP JSON event decoder.
     * 
     * @return
     */
    protected ElementNode createCoapJsonEventDecoderElement() {
	ElementNode.Builder builder = new ElementNode.Builder("CoAP JSON Decoder",
		IEventSourcesParser.CoapDecoders.CoapJsonDecoder.getLocalName(), "cogs",
		EventSourcesRoleKeys.CoapEventDecoder, this);

	builder.description("Event decoder that processes messages from a CoAP server event source "
		+ "which have a body in JSON format.");
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
		EventSourcesRoleKeys.StringEventDecoder, this);

	builder.description("Decoder that uses a Groovy script to parse a String payload into decoded events.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	builder.attribute((new AttributeNode.Builder("Script Id", "scriptId", AttributeType.Script,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Script used for decoding payload.")
			.makeRequired().build()));
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
		EventSourcesRoleKeys.StringEventDecoder, this);

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
		EventSourcesRoleKeys.CompositeEventDecoder, this);

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
		EventSourcesRoleKeys.CED_DecoderChoices, this);

	builder.description("List of decoder choices avaliable for parsing the payload. The first choice to "
		+ "match based on the given criteria is used to decode the payload.");
	return builder.build();
    }

    /**
     * Create element configuration for Groovy metadata extractor.
     * 
     * @return
     */
    // TODO: Update to device type.
    protected ElementNode createDeviceSpecificationDecoderChoiceElement() {
	ElementNode.Builder builder = new ElementNode.Builder("Match Device Specification",
		IEventSourcesParser.CompositeDecoderChoiceElements.DeviceSpecificationDecoderChoice.getLocalName(),
		"cogs", EventSourcesRoleKeys.CED_DecoderChoice, this);

	builder.description("Composite event decoder choice that applies when the device specification from the "
		+ " extracted metadata matches the specified value. This allows payload processing to be directly "
		+ " based on the specification assigned in SiteWhere device management.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	builder.attribute((new AttributeNode.Builder("Device type", "deviceTypeId", AttributeType.DeviceTypeReference,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Device type to match.").makeIndex()
			.makeRequired().build()));
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
		"cogs", EventSourcesRoleKeys.CED_MetadataExtractor, this);

	builder.description("Metadata extractor that uses a Groovy script to parse a binary payload and extract "
		+ " information such as the unique hardware id and payload. This data will be forwarded to the list "
		+ " of nested decoder choices for further processing.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	builder.attribute((new AttributeNode.Builder("Script Id", "scriptId", AttributeType.Script,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Script used for extracting metadata.")
			.makeRequired().build()));
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
		EventSourcesRoleKeys.EventDeduplicator, this);

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
		EventSourcesRoleKeys.EventDeduplicator, this);

	builder.description("Deduplicator that uses a Groovy script to check for duplicate events.");
	builder.attributeGroup(ConfigurationModelProvider.ATTR_GROUP_GENERAL);
	addEventSourceAttributes(builder, ConfigurationModelProvider.ATTR_GROUP_GENERAL);

	builder.attribute((new AttributeNode.Builder("Script Id", "scriptId", AttributeType.Script,
		ConfigurationModelProvider.ATTR_GROUP_GENERAL).description("Script used for testing for duplicates.")
			.makeRequired().build()));
	return builder.build();
    }
}