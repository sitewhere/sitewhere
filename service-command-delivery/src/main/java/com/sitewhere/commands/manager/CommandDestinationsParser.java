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
package com.sitewhere.commands.manager;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.commands.configuration.CommandDeliveryTenantConfiguration;
import com.sitewhere.commands.configuration.destinations.CommandDestinationGenericConfiguration;
import com.sitewhere.commands.configuration.destinations.coap.CoapConfiguration;
import com.sitewhere.commands.configuration.destinations.mqtt.MqttConfiguration;
import com.sitewhere.commands.configuration.extractors.ParameterExtractorGenericConfiguration;
import com.sitewhere.commands.configuration.extractors.coap.MetadataCoapParameterExtractorConfiguration;
import com.sitewhere.commands.configuration.extractors.mqtt.DefaultMqttParameterExtractorConfiguration;
import com.sitewhere.commands.destination.coap.CoapCommandDestination;
import com.sitewhere.commands.destination.coap.CoapParameters;
import com.sitewhere.commands.destination.coap.MetadataCoapParameterExtractor;
import com.sitewhere.commands.destination.mqtt.DefaultMqttParameterExtractor;
import com.sitewhere.commands.destination.mqtt.MqttCommandDestination;
import com.sitewhere.commands.destination.mqtt.MqttParameters;
import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Supports parsing command delivery configuration into command destination
 * components.
 */
public class CommandDestinationsParser {

    /** Static logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDestinationsParser.class);

    /** Type for CoAP destination */
    public static final String TYPE_COAP = "coap";

    /** Type for MQTT destination */
    public static final String TYPE_MQTT = "mqtt";

    /** Extractor type for CoAP metadata */
    public static final String EXTRACTOR_TYPE_COAP_METADATA = "coap-default";

    /** Extractor type for MQTT default */
    public static final String EXTRACTOR_TYPE_MQTT_DEFAULT = "mqtt-default";

    /**
     * Parse event source configurations in order to build event source instances.
     * 
     * @param component
     * @param configuration
     * @return
     * @throws SiteWhereException
     */
    public static List<ICommandDestination<?, ?>> parse(ITenantEngineLifecycleComponent component,
	    CommandDeliveryTenantConfiguration configuration) throws SiteWhereException {
	List<ICommandDestination<?, ?>> destinations = new ArrayList<>();
	for (CommandDestinationGenericConfiguration destConfig : configuration.getCommandDestinations()) {
	    switch (destConfig.getType()) {
	    case TYPE_COAP: {
		CoapCommandDestination dest = createCoapCommandDestination(component, destConfig);
		dest.setCommandDeliveryParameterExtractor(parseCoapParameterExtractor(component, destConfig));
		destinations.add(dest);
		break;
	    }
	    case TYPE_MQTT: {
		MqttCommandDestination dest = createMqttCommandDestination(component, destConfig);
		dest.setCommandDeliveryParameterExtractor(parseMqttParameterExtractor(component, destConfig));
		destinations.add(dest);
		break;
	    }
	    default: {
		throw new SiteWhereException(
			String.format("Unknown command destination type '%s' for destination with id '%s'",
				destConfig.getType(), destConfig.getId()));
	    }
	    }
	}
	return destinations;
    }

    /**
     * Parse configured parameter extractor for a CoAP command destination.
     * 
     * @param component
     * @param destConfig
     * @return
     * @throws SiteWhereException
     */
    public static ICommandDeliveryParameterExtractor<CoapParameters> parseCoapParameterExtractor(
	    ITenantEngineLifecycleComponent component, CommandDestinationGenericConfiguration destConfig)
	    throws SiteWhereException {
	ParameterExtractorGenericConfiguration extractorConfig = destConfig.getParameterExtractor();
	switch (extractorConfig.getType()) {
	case EXTRACTOR_TYPE_COAP_METADATA: {
	    MetadataCoapParameterExtractorConfiguration metaConfig = new MetadataCoapParameterExtractorConfiguration(
		    component);
	    metaConfig.apply(destConfig);
	    LOGGER.info(String.format("Creating CoAP metadata parameter extractor with configuration:\n%s\n\n",
		    MarshalUtils.marshalJsonAsPrettyString(metaConfig)));
	    return new MetadataCoapParameterExtractor(metaConfig);
	}
	default: {
	    throw new SiteWhereException(String.format("Unknown CoAP extractor type '%s' for destination with id '%s'",
		    extractorConfig.getType(), destConfig.getId()));
	}
	}
    }

    /**
     * Create CoAP command destination.
     * 
     * @param component
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static CoapCommandDestination createCoapCommandDestination(ITenantEngineLifecycleComponent component,
	    CommandDestinationGenericConfiguration destConfig) throws SiteWhereException {
	CoapConfiguration config = new CoapConfiguration(component);
	config.apply(destConfig);
	LOGGER.info(String.format("Creating CoAP command destination with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(config)));
	CoapCommandDestination destination = new CoapCommandDestination(config);
	return destination;
    }

    /**
     * Parse configured parameter extractor for an MQTT command destination.
     * 
     * @param component
     * @param destConfig
     * @return
     * @throws SiteWhereException
     */
    public static ICommandDeliveryParameterExtractor<MqttParameters> parseMqttParameterExtractor(
	    ITenantEngineLifecycleComponent component, CommandDestinationGenericConfiguration destConfig)
	    throws SiteWhereException {
	ParameterExtractorGenericConfiguration extractorConfig = destConfig.getParameterExtractor();
	switch (extractorConfig.getType()) {
	case EXTRACTOR_TYPE_MQTT_DEFAULT: {
	    DefaultMqttParameterExtractorConfiguration defaultConfig = new DefaultMqttParameterExtractorConfiguration(
		    component);
	    defaultConfig.apply(destConfig);
	    LOGGER.info(String.format("Creating MQTT default parameter extractor with configuration:\n%s\n\n",
		    MarshalUtils.marshalJsonAsPrettyString(defaultConfig)));
	    return new DefaultMqttParameterExtractor(defaultConfig);
	}
	default: {
	    throw new SiteWhereException(String.format("Unknown MQTT extractor type '%s' for destination with id '%s'",
		    extractorConfig.getType(), destConfig.getId()));
	}
	}
    }

    /**
     * Create MQTT command destination.
     * 
     * @param component
     * @param sourceConfig
     * @return
     * @throws SiteWhereException
     */
    protected static MqttCommandDestination createMqttCommandDestination(ITenantEngineLifecycleComponent component,
	    CommandDestinationGenericConfiguration destConfig) throws SiteWhereException {
	MqttConfiguration config = new MqttConfiguration(component);
	config.apply(destConfig);
	LOGGER.info(String.format("Creating MQTT command destination with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(config)));
	MqttCommandDestination destination = new MqttCommandDestination(config);
	return destination;
    }
}
