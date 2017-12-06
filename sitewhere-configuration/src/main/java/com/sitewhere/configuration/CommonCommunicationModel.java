/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import com.sitewhere.configuration.model.AttributeNode;
import com.sitewhere.configuration.model.AttributeType;
import com.sitewhere.configuration.model.ElementNode;

/**
 * Common shared model characteristics.
 * 
 * @author Derek
 */
public class CommonCommunicationModel {

    /**
     * Adds common MQTT connectivity attributes.
     * 
     * @param builder
     */
    public static void addMqttConnectivityAttributes(ElementNode.Builder builder) {
	builder.attribute((new AttributeNode.Builder("Transport protocol", "protocol", AttributeType.String)
		.description("Protocol used for establishing MQTT connection").defaultValue("tcp").choice("tcp")
		.choice("tls").build()));
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
}
