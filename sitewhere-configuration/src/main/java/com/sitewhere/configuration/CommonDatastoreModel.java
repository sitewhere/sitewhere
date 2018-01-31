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
 * Common shared datastore model characteristics.
 * 
 * @author Derek
 */
public class CommonDatastoreModel {

    /**
     * Adds InfluxDB configuration attributes.
     * 
     * @param builder
     */
    public static void addInfluxDbAttributes(ElementNode.Builder builder) {
	builder.attributeGroup("conn", "InfluxDB Connectivity");
	builder.attribute((new AttributeNode.Builder("Connection URL", "connectUrl", AttributeType.String)
		.description("Specifies URL used to connect to InfluxDB.").group("conn")
		.defaultValue("http://localhost:8086").build()));
	builder.attribute((new AttributeNode.Builder("Username", "username", AttributeType.String)
		.description("Username for InfluxDB authentication.").group("conn").defaultValue("root").build()));
	builder.attribute((new AttributeNode.Builder("Password", "password", AttributeType.String)
		.description("Password for InfluxDB authentication.").group("conn").defaultValue("root").build()));
	builder.attribute((new AttributeNode.Builder("Database", "database", AttributeType.String)
		.description("InfluxDB database name.").group("conn").defaultValue("sitewhere").build()));
	builder.attribute((new AttributeNode.Builder("Retention policy", "retention", AttributeType.String)
		.description("InfluxDB retention policy name.").group("conn").defaultValue("autogen").build()));
	builder.attribute((new AttributeNode.Builder("Log level", "logLevel", AttributeType.String)
		.description("Log level for debugging InfluxDB interactions.").group("conn").choice("None", "none")
		.choice("Basic", "basic").choice("Headers", "headers").choice("Full", "full").defaultValue("none")
		.build()));

	builder.attributeGroup("batch", "InfluxDB Batch Event Processing");
	builder.attribute((new AttributeNode.Builder("Enable batch processing", "enableBatch", AttributeType.Boolean)
		.description("Enable delivery of events in batches.").group("batch").defaultValue("true").build()));
	builder.attribute((new AttributeNode.Builder("Max batch chunk size", "batchChunkSize", AttributeType.Integer)
		.description("Maximum number of events to send in a batch.").group("batch").defaultValue("1000")
		.build()));
	builder.attribute(
		(new AttributeNode.Builder("Max batch send interval (ms)", "batchIntervalMs", AttributeType.Integer)
			.description("Maximum amount of time (in ms) to wait before sending a batch.").group("batch")
			.defaultValue("100").build()));
    }
}