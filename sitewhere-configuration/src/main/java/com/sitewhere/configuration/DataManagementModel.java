/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

import com.sitewhere.configuration.model.AttributeNode;
import com.sitewhere.configuration.model.ConfigurationModel;
import com.sitewhere.configuration.model.ElementNode;
import com.sitewhere.configuration.model.ElementRoles;
import com.sitewhere.configuration.old.ITenantDatastoreParser;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;

/**
 * Configuration model for data management elements.
 * 
 * @author Derek
 */
public class DataManagementModel extends ConfigurationModel {

    public DataManagementModel() {
	// Datastore implementations.
	addElement(createMongoTenantDatastoreElement());
	addElement(createMongoInfluxDbTenantDatastoreElement());
	addElement(createHBaseTenantDatastoreElement());
    }

    /**
     * Create element configuration for MonogoDB tenant datastore.
     * 
     * @return
     */
    protected ElementNode createMongoTenantDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MongoDB Tenant Datastore",
		ITenantDatastoreParser.Elements.MongoTenantDatastore.getLocalName(), "database",
		ElementRoles.DataManagement_Datastore);

	builder.description("Store tenant data using a MongoDB database. Note that the "
		+ "global datastore must be configured to use MongoDB if this tenant datastore is to "
		+ "be used. Most core MongoDB settings are configured at the global level.");
	builder.attributeGroup("bulk", "Bulk Insert for Events");
	builder.attribute((new AttributeNode.Builder("Use bulk inserts", "useBulkEventInserts", AttributeType.Boolean)
		.description("Use the MongoDB bulk insert API to add " + "events in groups and improve performance.")
		.group("bulk").build()));
	builder.attribute((new AttributeNode.Builder("Bulk insert max chunk size", "bulkInsertMaxChunkSize",
		AttributeType.Integer).description(
			"Maximum number of records to send " + "in a single bulk insert (if bulk inserts are enabled).")
			.group("bulk").build()));
	return builder.build();
    }

    /**
     * Create element configuration for MonogoDB/InfluxDB tenant datastore.
     * 
     * @return
     */
    protected ElementNode createMongoInfluxDbTenantDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder("MongoDB/InfluxDB Tenant Datastore",
		ITenantDatastoreParser.Elements.MongoInfluxDbTenantDatastore.getLocalName(), "database",
		ElementRoles.DataManagement_Datastore);

	builder.description("Store tenant master data using a MongoDB database and store tenant event "
		+ "data in InfluxDB. Note that the global datastore must be configured to "
		+ "use MongoDB if this tenant datastore is to be used. Most core "
		+ "MongoDB settings are configured at the global level.");
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
		.description("Log level for debugging InfluxDB interactions.").group("conn").choice("none")
		.choice("basic").choice("headers").choice("full").defaultValue("none").build()));

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
	return builder.build();
    }

    /**
     * Create element configuration for HBase tenant datastore.
     * 
     * @return
     */
    protected ElementNode createHBaseTenantDatastoreElement() {
	ElementNode.Builder builder = new ElementNode.Builder("HBase Tenant Datastore",
		ITenantDatastoreParser.Elements.HBaseTenantDatastore.getLocalName(), "database",
		ElementRoles.DataManagement_Datastore);
	builder.description("Store tenant data using tables in an HBase instance. Note that the "
		+ "global datastore must be configured to use HBase if this tenant datastore is to "
		+ "be used. Most core HBase settings are configured at the global level.");
	return builder.build();
    }
}