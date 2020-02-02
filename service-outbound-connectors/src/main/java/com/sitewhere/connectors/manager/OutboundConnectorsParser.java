/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.connectors.configuration.OutboundConnectorGenericConfiguration;
import com.sitewhere.connectors.configuration.OutboundConnectorsTenantConfiguration;
import com.sitewhere.connectors.configuration.connector.MqttOutboundConnectorConfiguration;
import com.sitewhere.connectors.mqtt.MqttOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Parses outbound connector configuration into components.
 */
public class OutboundConnectorsParser {

    /** Static logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundConnectorsParser.class);

    /** Type for MQTT event source */
    public static final String TYPE_MQTT = "mqtt";

    /**
     * Parse outbound connector configurations in order to build components.
     * 
     * @param component
     * @param configuration
     * @return
     * @throws SiteWhereException
     */
    public static List<IOutboundConnector> parse(ITenantEngineLifecycleComponent component,
	    OutboundConnectorsTenantConfiguration configuration) throws SiteWhereException {
	List<IOutboundConnector> connectors = new ArrayList<>();
	for (OutboundConnectorGenericConfiguration connConfig : configuration.getOutboundConnectors()) {
	    switch (connConfig.getType()) {
	    case TYPE_MQTT: {
		connectors.add(createMqttOutboundConnector(component, connConfig));
		break;
	    }
	    default: {
		throw new SiteWhereException(
			String.format("Unknown outbound connector type '%s' for connector with id '%s'",
				connConfig.getType(), connConfig.getId()));
	    }
	    }
	}
	return connectors;
    }

    /**
     * Create an MQTT event source.
     * 
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static IOutboundConnector createMqttOutboundConnector(ITenantEngineLifecycleComponent component,
	    OutboundConnectorGenericConfiguration connConfig) throws SiteWhereException {
	MqttOutboundConnectorConfiguration mqttConfig = new MqttOutboundConnectorConfiguration(component);
	mqttConfig.apply(connConfig);
	LOGGER.info(String.format("Creating MQTT outbound connector with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(mqttConfig)));
	MqttOutboundConnector connector = new MqttOutboundConnector(mqttConfig);
	return connector;
    }
}
