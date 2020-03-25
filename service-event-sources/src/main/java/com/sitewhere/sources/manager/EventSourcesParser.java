/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.sources.BinaryInboundEventSource;
import com.sitewhere.sources.activemq.ActiveMqBrokerEventReceiver;
import com.sitewhere.sources.activemq.ActiveMqClientEventReceiver;
import com.sitewhere.sources.azure.EventHubInboundEventReceiver;
import com.sitewhere.sources.coap.CoapServerEventReceiver;
import com.sitewhere.sources.coap.CoapServerEventSource;
import com.sitewhere.sources.configuration.EventSourceGenericConfiguration;
import com.sitewhere.sources.configuration.EventSourcesTenantConfiguration;
import com.sitewhere.sources.configuration.eventsource.activemq.ActiveMqBrokerConfiguration;
import com.sitewhere.sources.configuration.eventsource.activemq.ActiveMqClientConfiguration;
import com.sitewhere.sources.configuration.eventsource.azure.EventHubConfiguration;
import com.sitewhere.sources.configuration.eventsource.coap.CoapServerConfiguration;
import com.sitewhere.sources.configuration.eventsource.mqtt.MqttConfiguration;
import com.sitewhere.sources.configuration.eventsource.rabbitmq.RabbitMqConfiguration;
import com.sitewhere.sources.decoder.json.JsonDeviceRequestDecoder;
import com.sitewhere.sources.decoder.protobuf.ProtobufDeviceEventDecoder;
import com.sitewhere.sources.mqtt.MqttInboundEventReceiver;
import com.sitewhere.sources.rabbitmq.RabbitMqInboundEventReceiver;
import com.sitewhere.sources.spi.IDeviceEventDecoder;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.IInboundEventSource;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Supports parsing event sources configuration into event source components.
 */
public class EventSourcesParser {

    /** Static logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSourcesParser.class);

    /** Type for ActiveMQ broker */
    public static final String TYPE_ACTIVEMQ_BROKER = "activemq-broker";

    /** Type for ActiveMQ client */
    public static final String TYPE_ACTIVEMQ_CLIENT = "activemq-client";

    /** Type for CoAP server event source */
    public static final String TYPE_COAP = "coap";

    /** Type for Azure Event Hub event source */
    public static final String TYPE_EVENT_HUB = "eventhub";

    /** Type for MQTT event source */
    public static final String TYPE_MQTT = "mqtt";

    /** Type for RabbitMQ event source */
    public static final String TYPE_RABBITMQ = "rabbitmq";

    /**
     * Parse event source configurations in order to build event source instances.
     * 
     * @param component
     * @param configuration
     * @return
     * @throws SiteWhereException
     */
    public static List<IInboundEventSource<?>> parse(ITenantEngineLifecycleComponent component,
	    EventSourcesTenantConfiguration configuration) throws SiteWhereException {
	List<IInboundEventSource<?>> sources = new ArrayList<>();
	for (EventSourceGenericConfiguration sourceConfig : configuration.getEventSources()) {
	    switch (sourceConfig.getType()) {
	    case TYPE_ACTIVEMQ_BROKER: {
		sources.add(createActiveMqBrokerEventSource(component, sourceConfig));
		break;
	    }
	    case TYPE_ACTIVEMQ_CLIENT: {
		sources.add(createActiveMqClientEventSource(component, sourceConfig));
		break;
	    }
	    case TYPE_COAP: {
		sources.add(createCoapEventSource(component, sourceConfig));
		break;
	    }
	    case TYPE_EVENT_HUB: {
		sources.add(createEventHubEventSource(component, sourceConfig));
		break;
	    }
	    case TYPE_MQTT: {
		sources.add(createMqttEventSource(component, sourceConfig));
		break;
	    }
	    case TYPE_RABBITMQ: {
		sources.add(createRabbitMqEventSource(component, sourceConfig));
		break;
	    }
	    default: {
		throw new SiteWhereException(String.format("Unknown event source type '%s' for source with id '%s'",
			sourceConfig.getType(), sourceConfig.getId()));
	    }
	    }
	}
	return sources;
    }

    /**
     * Create a binary event source based on the given internals.
     * 
     * @param sourceConfig
     * @param receivers
     * @return
     * @throws SiteWhereException
     */
    protected static IInboundEventSource<?> binaryEventSourceFor(EventSourceGenericConfiguration sourceConfig,
	    List<IInboundEventReceiver<byte[]>> receivers) throws SiteWhereException {
	BinaryInboundEventSource source = new BinaryInboundEventSource();
	source.getInboundEventReceivers().addAll(receivers);
	source.setDeviceEventDecoder(parseBinaryDecoder(sourceConfig));
	source.setSourceId(sourceConfig.getId());
	return source;
    }

    /**
     * Create an ActiveMQ broker event source.
     * 
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IInboundEventSource<?> createActiveMqBrokerEventSource(ITenantEngineLifecycleComponent component,
	    EventSourceGenericConfiguration sourceConfig) throws SiteWhereException {
	ActiveMqBrokerConfiguration config = new ActiveMqBrokerConfiguration(component);
	config.apply(sourceConfig);
	LOGGER.info(String.format("Creating ActiveMQ broker event source with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(config)));
	ActiveMqBrokerEventReceiver receiver = new ActiveMqBrokerEventReceiver(config);
	return binaryEventSourceFor(sourceConfig, Collections.singletonList(receiver));
    }

    /**
     * Create an ActiveMQ client event source.
     * 
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IInboundEventSource<?> createActiveMqClientEventSource(ITenantEngineLifecycleComponent component,
	    EventSourceGenericConfiguration sourceConfig) throws SiteWhereException {
	ActiveMqClientConfiguration config = new ActiveMqClientConfiguration(component);
	config.apply(sourceConfig);
	LOGGER.info(String.format("Creating ActiveMQ client event source with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(config)));
	ActiveMqClientEventReceiver receiver = new ActiveMqClientEventReceiver(config);
	return binaryEventSourceFor(sourceConfig, Collections.singletonList(receiver));
    }

    /**
     * Create a CoAP server event source.
     * 
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IInboundEventSource<?> createCoapEventSource(ITenantEngineLifecycleComponent component,
	    EventSourceGenericConfiguration sourceConfig) throws SiteWhereException {
	CoapServerConfiguration config = new CoapServerConfiguration(component);
	config.apply(sourceConfig);
	LOGGER.info(String.format("Creating CoAP server event source with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(config)));
	CoapServerEventReceiver receiver = new CoapServerEventReceiver(config);
	CoapServerEventSource source = new CoapServerEventSource();
	source.getInboundEventReceivers().add(receiver);
	return source;
    }

    /**
     * Create an Azure Event Hub event source.
     * 
     * @param component
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IInboundEventSource<?> createEventHubEventSource(ITenantEngineLifecycleComponent component,
	    EventSourceGenericConfiguration sourceConfig) throws SiteWhereException {
	EventHubConfiguration config = new EventHubConfiguration(component);
	config.apply(sourceConfig);
	LOGGER.info(String.format("Creating Azure Event Hub event source with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(config)));
	EventHubInboundEventReceiver receiver = new EventHubInboundEventReceiver(config);
	return binaryEventSourceFor(sourceConfig, Collections.singletonList(receiver));
    }

    /**
     * Create an MQTT event source.
     * 
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IInboundEventSource<?> createMqttEventSource(ITenantEngineLifecycleComponent component,
	    EventSourceGenericConfiguration sourceConfig) throws SiteWhereException {
	MqttConfiguration mqttConfig = new MqttConfiguration(component);
	mqttConfig.apply(sourceConfig);
	LOGGER.info(String.format("Creating MQTT event source with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(mqttConfig)));
	MqttInboundEventReceiver receiver = new MqttInboundEventReceiver(mqttConfig);
	return binaryEventSourceFor(sourceConfig, Collections.singletonList(receiver));
    }

    /**
     * Create a RabbitMQ event source.
     * 
     * @param component
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IInboundEventSource<?> createRabbitMqEventSource(ITenantEngineLifecycleComponent component,
	    EventSourceGenericConfiguration sourceConfig) throws SiteWhereException {
	RabbitMqConfiguration config = new RabbitMqConfiguration(component);
	config.apply(sourceConfig);
	LOGGER.info(String.format("Creating RabbitMQ event source with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(config)));
	RabbitMqInboundEventReceiver receiver = new RabbitMqInboundEventReceiver(config);
	return binaryEventSourceFor(sourceConfig, Collections.singletonList(receiver));
    }

    /**
     * Parse decoder type and return a binary decoder instance.
     * 
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IDeviceEventDecoder<byte[]> parseBinaryDecoder(EventSourceGenericConfiguration sourceConfig)
	    throws SiteWhereException {
	JsonNode decoder = sourceConfig.getDecoder();
	JsonNode type = decoder.findValue("type");
	if (type == null) {
	    throw new SiteWhereException("Event source decoder does not specify type.");
	}
	switch (type.asText()) {
	case "json": {
	    return new JsonDeviceRequestDecoder();
	}
	case "protobuf": {
	    return new ProtobufDeviceEventDecoder();
	}
	default: {
	    throw new SiteWhereException(String.format("Unknown decoder type '%s' for source with id '%s'",
		    sourceConfig.getDecoder(), sourceConfig.getId()));
	}
	}
    }
}
