/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;

/**
 * Common shared connector model information.
 * 
 * @author Derek
 */
public class CommonConnectorModel {

    /**
     * Adds common MQTT connectivity attributes.
     * 
     * @param builder
     */
    public static void addMqttConnectivityAttributes(ElementNode.Builder builder) {
	builder.attribute((new AttributeNode.Builder("Transport protocol", "protocol", AttributeType.String)
		.description("Protocol used for establishing MQTT connection").defaultValue("tcp")
		.choice("TCP (No Security)", "tcp").choice("TLS (Secure)", "tls").build()));
	builder.attribute((new AttributeNode.Builder("MQTT broker hostname", "hostname", AttributeType.String)
		.description("Hostname used for creating the MQTT broker connection.").defaultValue("localhost")
		.build()));
	builder.attribute((new AttributeNode.Builder("MQTT broker port", "port", AttributeType.Integer)
		.description("Port number used for creating the MQTT broker connection.").defaultValue("1883")
		.build()));
	builder.attribute((new AttributeNode.Builder("MQTT broker username", "username", AttributeType.String)
		.description("Optional username for authenticating the MQTT broker connection.").build()));
	builder.attribute((new AttributeNode.Builder("MQTT broker password", "password", AttributeType.String)
		.description("Optional password for authenticating the MQTT broker connection.").build()));
	builder.attribute((new AttributeNode.Builder("Trust store path", "trustStorePath", AttributeType.String)
		.description("Fully-qualified path to trust store for secured connections.").build()));
	builder.attribute((new AttributeNode.Builder("Trust store password", "trustStorePassword", AttributeType.String)
		.description("Password used to authenticate with trust store.").build()));
	builder.attribute((new AttributeNode.Builder("Keystore path", "keyStorePath", AttributeType.String)
		.description("Fully-qualified path to keystore for secured connections.").build()));
	builder.attribute((new AttributeNode.Builder("Keystore password", "keyStorePassword", AttributeType.String)
		.description("Password used to authenticate with keystore.").build()));
    }

    /**
     * Adds common Solr connectivity attributes.
     * 
     * @param builder
     */
    public static void adSolrConnectivityAttributes(ElementNode.Builder builder) {
	builder.attributeGroup("instance", "Solr Instance Information");
	builder.attribute((new AttributeNode.Builder("Solr server URL", "solrServerUrl", AttributeType.String)
		.description("URL used by Solr client to access server.").group("instance").build()));
    }
}
