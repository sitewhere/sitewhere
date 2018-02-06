/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.parser;

/**
 * Enumerates elements used by event sources parser.
 * 
 * @author Derek
 */
public interface IEventSourcesParser {

    // Root element name.
    public static final String ROOT = "event-sources";

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Event source */
	EventSource("event-source"),

	/** ActiveMQ event source */
	ActiveMQEventSource("activemq-event-source"),

	/** ActiveMQ client event source */
	ActiveMQClientEventSource("activemq-client-event-source"),

	/** Azure EventHub event source */
	AzureEventHubEventSource("azure-eventhub-event-source"),

	/** CoAP server event source */
	CoapServerEventSource("coap-server-event-source"),

	/** Hazelcast queue event source */
	HazelcastQueueEventSource("hazelcast-queue-event-source"),

	/** MQTT event source */
	MqttEventSource("mqtt-event-source"),

	/** Polling REST source */
	PollingRestEventSource("polling-rest-event-source"),

	/** RabbitMQ event source */
	RabbitMqEventSource("rabbit-mq-event-source"),

	/** Socket event source */
	SocketEventSource("socket-event-source"),

	/** Web socket event source */
	WebSocketEventSource("web-socket-event-source");

	/** Event code */
	private String localName;

	private Elements(String localName) {
	    this.localName = localName;
	}

	public static Elements getByLocalName(String localName) {
	    for (Elements value : Elements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }

    /**
     * Expected binary decoder elements.
     * 
     * @author Derek
     */
    public static enum BinaryDecoders {

	/** SiteWhere Google Protocol Buffer decoder */
	ProtobufDecoder("protobuf-event-decoder"),

	/** SiteWhere JSON device request decoder */
	JsonDeviceRequestDecoder("json-device-request-decoder"),

	/** SiteWhere JSON batch decoder */
	@Deprecated
	JsonEventDecoder("json-event-decoder"),

	/** SiteWhere JSON batch decoder */
	JsonBatchEventDecoder("json-batch-event-decoder"),

	/** Uses Groovy script to parse events */
	GroovyEventDecoder("groovy-event-decoder"),

	/** Reference to externally defined event decoder */
	EventDecoder("event-decoder"),

	/** Decoder that defers to nested decoders based on criteria */
	CompositeDecoder("composite-decoder");

	/** Event code */
	private String localName;

	private BinaryDecoders(String localName) {
	    this.localName = localName;
	}

	public static BinaryDecoders getByLocalName(String localName) {
	    for (BinaryDecoders value : BinaryDecoders.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }

    /**
     * Expected String decoder elements.
     * 
     * @author Derek
     */
    public static enum StringDecoders {

	/** Echoes String payload to logger */
	EchoStringDecoder("echo-string-decoder"),

	/** Uses Groovy script to parse events */
	GroovyStringDecoder("groovy-string-event-decoder"),

	/** Reference to externally defined event decoder */
	EventDecoder("event-decoder");

	/** Event code */
	private String localName;

	private StringDecoders(String localName) {
	    this.localName = localName;
	}

	public static StringDecoders getByLocalName(String localName) {
	    for (StringDecoders value : StringDecoders.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }

    /**
     * Expected binary socket interaction handler elements.
     * 
     * @author Derek
     */
    public static enum BinarySocketInteractionHandlers {

	/**
	 * Reference to a socket interaction handler factory defined in a Spring bean
	 */
	InteractionHandlerFactoryReference("interaction-handler-factory"),

	/**
	 * Produces interaction handler that reads all data from the client socket
	 */
	ReadAllInteractionHandlerFactory("read-all-interaction-handler-factory"),

	/**
	 * Produces interaction handler that reads HTTP data from the client socket
	 */
	HttpInteractionHandlerFactory("http-interaction-handler-factory"),

	/** Produces interaction handler uses Groovy to interact with socket */
	GroovySocketInteractionHandlerFactory("groovy-interaction-handler-factory");

	/** Event code */
	private String localName;

	private BinarySocketInteractionHandlers(String localName) {
	    this.localName = localName;
	}

	public static BinarySocketInteractionHandlers getByLocalName(String localName) {
	    for (BinarySocketInteractionHandlers value : BinarySocketInteractionHandlers.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }

    public static enum CompositeDecoderMetadataExtractorElements {

	/** Extracts message metadata using a Groovy script */
	GroovyDeviceMetadataExtractor("groovy-device-metadata-extractor");

	/** Event code */
	private String localName;

	private CompositeDecoderMetadataExtractorElements(String localName) {
	    this.localName = localName;
	}

	public static CompositeDecoderMetadataExtractorElements getByLocalName(String localName) {
	    for (CompositeDecoderMetadataExtractorElements value : CompositeDecoderMetadataExtractorElements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }

    public static enum CompositeDecoderChoiceElements {

	/** Produces interaction handler uses Groovy to interact with socket */
	DeviceSpecificationDecoderChoice("device-specification-decoder-choice");

	/** Event code */
	private String localName;

	private CompositeDecoderChoiceElements(String localName) {
	    this.localName = localName;
	}

	public static CompositeDecoderChoiceElements getByLocalName(String localName) {
	    for (CompositeDecoderChoiceElements value : CompositeDecoderChoiceElements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }

    /**
     * Expected deduplicator elements.
     * 
     * @author Derek
     */
    public static enum Deduplicators {

	/** Uses alternate id to find duplicate events */
	AlternateIdDeduplicator("alternate-id-deduplicator"),

	/** Uses Groovy script to find duplicate events */
	GroovyEventDeduplicator("groovy-event-deduplicator");

	/** Event code */
	private String localName;

	private Deduplicators(String localName) {
	    this.localName = localName;
	}

	public static Deduplicators getByLocalName(String localName) {
	    for (Deduplicators value : Deduplicators.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }
}