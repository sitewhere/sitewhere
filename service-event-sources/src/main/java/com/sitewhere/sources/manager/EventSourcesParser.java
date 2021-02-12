/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.sources.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.sources.BinaryInboundEventSource;
import com.sitewhere.sources.activemq.ActiveMqBrokerEventReceiver;
import com.sitewhere.sources.activemq.ActiveMqClientEventReceiver;
import com.sitewhere.sources.azure.EventHubInboundEventReceiver;
import com.sitewhere.sources.coap.CoapServerEventReceiver;
import com.sitewhere.sources.coap.CoapServerEventSource;
import com.sitewhere.sources.configuration.DecoderGenericConfiguration;
import com.sitewhere.sources.configuration.EventSourceGenericConfiguration;
import com.sitewhere.sources.configuration.EventSourcesTenantConfiguration;
import com.sitewhere.sources.configuration.eventsource.activemq.ActiveMqBrokerConfiguration;
import com.sitewhere.sources.configuration.eventsource.activemq.ActiveMqClientConfiguration;
import com.sitewhere.sources.configuration.eventsource.azure.EventHubConfiguration;
import com.sitewhere.sources.configuration.eventsource.coap.CoapServerConfiguration;
import com.sitewhere.sources.configuration.eventsource.mqtt.MqttConfiguration;
import com.sitewhere.sources.configuration.eventsource.rabbitmq.RabbitMqConfiguration;
import com.sitewhere.sources.decoder.ScriptedEventDecoder;
import com.sitewhere.sources.decoder.ScriptedEventDecoderConfiguration;
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

    /** Decoder for JSON payloads */
    public static final String DECODER_JSON = "json";

    /** Decoder for protocol buffer payloads */
    public static final String DECODER_PROTOBUF = "protobuf";

    /** Decoder for scripted payload processing */
    public static final String DECODER_SCRIPTED = "scripted";

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
	DecoderGenericConfiguration decoder = sourceConfig.getDecoder();
	String type = decoder.getType();
	if (type == null) {
	    throw new SiteWhereException("Event source decoder does not specify type.");
	}
	JsonNode config = decoder.getConfiguration();
	if (config == null) {
	    throw new SiteWhereException("Event source decoder does not specify configuration.");
	}
	switch (type) {
	case DECODER_JSON: {
	    return new JsonDeviceRequestDecoder();
	}
	case DECODER_PROTOBUF: {
	    return new ProtobufDeviceEventDecoder();
	}
	case DECODER_SCRIPTED: {
	    ScriptedEventDecoder scripted = new ScriptedEventDecoder();
	    try {
		ScriptedEventDecoderConfiguration scriptConfig = MarshalUtils.unmarshalJsonNode(config,
			ScriptedEventDecoderConfiguration.class);
		scripted.setScriptId(scriptConfig.getScriptId());
		return scripted;
	    } catch (JsonProcessingException e) {
		throw new SiteWhereException("Invalid JSON configuration provider for scripted event decoder.", e);
	    }
	}
	default: {
	    throw new SiteWhereException(String.format("Unknown decoder type '%s' for source with id '%s'",
		    sourceConfig.getDecoder(), sourceConfig.getId()));
	}
	}
    }
}
