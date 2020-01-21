/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.manager;

import java.util.ArrayList;
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
import com.sitewhere.sources.spi.IInboundEventSource;
import com.sitewhere.spi.SiteWhereException;

public class EventSourcesParser {

    /** Static logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSourcesParser.class);

    /** Type for MQTT event source */
    public static final String TYPE_MQTT = "mqtt";

    /**
     * Parse event source configurations in order to build event source instances.
     * 
     * @param configuration
     * @return
     * @throws SiteWhereException
     */
    public static List<IInboundEventSource<?>> parse(EventSourcesTenantConfiguration configuration)
	    throws SiteWhereException {
	List<IInboundEventSource<?>> sources = new ArrayList<>();
	for (EventSourceGenericConfiguration sourceConfig : configuration.getEventSources()) {
	    switch (sourceConfig.getType()) {
	    case "mqtt": {
		sources.add(createMqttEventSource(sourceConfig));
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
     * Create an MQTT event source.
     * 
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IInboundEventSource<?> createMqttEventSource(EventSourceGenericConfiguration sourceConfig)
	    throws SiteWhereException {
	MqttEventSourceConfiguration mqttConfig = new MqttEventSourceConfiguration();
	mqttConfig.apply(sourceConfig);
	LOGGER.info(String.format("Creating MQTT event source with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(mqttConfig)));
	MqttInboundEventReceiver receiver = new MqttInboundEventReceiver(mqttConfig);
	BinaryInboundEventSource source = new BinaryInboundEventSource();
	source.getInboundEventReceivers().add(receiver);
	source.setDeviceEventDecoder(parseBinaryDecoder(sourceConfig));
	return source;
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
