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

import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.sources.BinaryInboundEventSource;
import com.sitewhere.sources.configuration.EventSourceGenericConfiguration;
import com.sitewhere.sources.configuration.EventSourcesTenantConfiguration;
import com.sitewhere.sources.configuration.eventsource.MqttEventSourceConfiguration;
import com.sitewhere.sources.decoder.json.JsonDeviceRequestDecoder;
import com.sitewhere.sources.decoder.protobuf.ProtobufDeviceEventDecoder;
import com.sitewhere.sources.mqtt.MqttInboundEventReceiver;
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

    /** Type for MQTT event source */
    public static final String TYPE_MQTT = "mqtt";

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
	    case TYPE_MQTT: {
		sources.add(createMqttEventSource(component, sourceConfig));
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
     * Create an MQTT event source.
     * 
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IInboundEventSource<?> createMqttEventSource(ITenantEngineLifecycleComponent component,
	    EventSourceGenericConfiguration sourceConfig) throws SiteWhereException {
	MqttEventSourceConfiguration mqttConfig = new MqttEventSourceConfiguration(component);
	mqttConfig.apply(sourceConfig);
	LOGGER.info(String.format("Creating MQTT event source with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(mqttConfig)));
	MqttInboundEventReceiver receiver = new MqttInboundEventReceiver(mqttConfig);
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
	switch (sourceConfig.getDecoder()) {
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
