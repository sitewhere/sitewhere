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
import com.sitewhere.spi.microservice.configuration.model.IAttributeGroup;

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
     * @param connectivity
     * @param authentication
     */
    public static void addMqttCommonAttributes(ElementNode.Builder builder, IAttributeGroup connectivity,
	    IAttributeGroup authentication) {
	builder.attribute(
		(new AttributeNode.Builder("Transport protocol", "protocol", AttributeType.String, connectivity)
			.description("Protocol used for establishing MQTT connection").defaultValue("tcp")
			.choice("TCP (No Security)", "tcp").choice("TLS (Secure)", "tls").build()));
	builder.attribute(
		(new AttributeNode.Builder("MQTT broker hostname", "hostname", AttributeType.String, connectivity)
			.description("Hostname used for creating the MQTT broker connection.").defaultValue("localhost")
			.build()));
	builder.attribute((new AttributeNode.Builder("MQTT broker port", "port", AttributeType.Integer, connectivity)
		.description("Port number used for creating the MQTT broker connection.").defaultValue("1883")
		.build()));
	builder.attribute((new AttributeNode.Builder("MQTT client id", "clientId", AttributeType.String, connectivity)
		.description("Client id used for MQTT client connection.").build()));
	builder.attribute(
		(new AttributeNode.Builder("Use clean session", "cleanSession", AttributeType.Boolean, connectivity)
			.description("Indicates if 'clean session' flag should be set.").defaultValue("true").build()));
	builder.attribute((new AttributeNode.Builder("Quality of service", "qos", AttributeType.String, connectivity)
		.description("Quality of service for connection").defaultValue("AT_LEAST_ONCE")
		.choice("At Most Once (QoS 0)", "AT_MOST_ONCE").choice("At Least Once (QoS 1)", "AT_LEAST_ONCE")
		.choice("Exactly Once (QoS 2)", "EXACTLY_ONCE").build()));

	builder.attribute(
		(new AttributeNode.Builder("MQTT broker username", "username", AttributeType.String, authentication)
			.description("Optional username for authenticating the MQTT broker connection.").build()));
	builder.attribute(
		(new AttributeNode.Builder("MQTT broker password", "password", AttributeType.String, authentication)
			.description("Optional password for authenticating the MQTT broker connection.").build()));
	builder.attribute(
		(new AttributeNode.Builder("Trust store path", "trustStorePath", AttributeType.String, authentication)
			.description("Fully-qualified path to trust store for secured connections.").build()));
	builder.attribute((new AttributeNode.Builder("Trust store password", "trustStorePassword", AttributeType.String,
		authentication).description("Password used to authenticate with trust store.").build()));
	builder.attribute(
		(new AttributeNode.Builder("Keystore path", "keyStorePath", AttributeType.String, authentication)
			.description("Fully-qualified path to keystore for secured connections.").build()));
	builder.attribute((new AttributeNode.Builder("Keystore password", "keyStorePassword", AttributeType.String,
		authentication).description("Password used to authenticate with keystore.").build()));
    }

    /**
     * Adds common Solr connectivity attributes.
     * 
     * @param builder
     * @param connectivity
     */
    public static void adSolrConnectivityAttributes(ElementNode.Builder builder, IAttributeGroup connectivity) {
	builder.attribute(
		(new AttributeNode.Builder("Solr server URL", "solrServerUrl", AttributeType.String, connectivity)
			.description("URL used by Solr client to access server.").build()));
    }
}
