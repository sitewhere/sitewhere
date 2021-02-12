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
