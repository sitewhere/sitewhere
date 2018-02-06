/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.datastore;

import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.instance.influxdb.InfluxConfiguration;
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.configuration.parser.IDatastoreCommonParser.DeviceManagementDatastoreElements;
import com.sitewhere.configuration.parser.IDatastoreCommonParser.EventManagementDatastoreElements;
import com.sitewhere.spi.microservice.spring.InstanceManagementBeans;

/**
 * Parses common datastore configuration elements.
 * 
 * @author Derek
 */
public class DatastoreConfigurationParser {

    /**
     * Parse potential configuration options for a device management datastore and
     * return configuration details.
     * 
     * @param element
     * @param context
     * @return
     */
    public static DatastoreConfiguration parseDeviceManagementDatastore(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    DeviceManagementDatastoreElements type = DeviceManagementDatastoreElements
		    .getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown datastore element: " + child.getLocalName());
	    }
	    switch (type) {
	    case MongoDBDatastore: {
		return parseMongoDbDatastore(child, context);
	    }
	    case MongoDBReference: {
		return parseMongoDbReference(child, context);
	    }
	    }
	}
	return null;
    }

    /**
     * Parse potential configuration options for a event management datastore and
     * return configuration details.
     * 
     * @param element
     * @param context
     * @return
     */
    public static DatastoreConfiguration parseEventManagementDatastore(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    EventManagementDatastoreElements type = EventManagementDatastoreElements
		    .getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown datastore element: " + child.getLocalName());
	    }
	    switch (type) {
	    case MongoDBDatastore: {
		return parseMongoDbDatastore(child, context);
	    }
	    case MongoDBReference: {
		return parseMongoDbReference(child, context);
	    }
	    case InfluxDBDatastore: {
		return parseInfluxDbDatastore(child, context);
	    }
	    case InfluxDBReference: {
		return parseInfluxDbReference(child, context);
	    }
	    }
	}
	return null;
    }

    /**
     * Parse configuration for a MongoDB datastore.
     * 
     * @param element
     * @param context
     * @return
     */
    protected static DatastoreConfiguration parseMongoDbDatastore(Element element, ParserContext context) {
	BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(MongoConfiguration.class);
	parseMongoAttributes(element, context, configuration);
	return new DatastoreConfiguration(DatastoreConfigurationType.MongoDB, configuration.getBeanDefinition());
    }

    /**
     * Parse configuration reference for a MongoDB datastore.
     * 
     * @param element
     * @param context
     * @return
     */
    protected static DatastoreConfiguration parseMongoDbReference(Element element, ParserContext context) {
	Attr id = element.getAttributeNode("id");
	if (id == null) {
	    throw new RuntimeException("No id specified for MongoDB configuration.");
	}
	String reference = InstanceManagementBeans.BEAN_MONGO_CONFIGURATION_BASE + id.getValue();
	return new DatastoreConfiguration(DatastoreConfigurationType.MongoDBReference, reference);
    }

    /**
     * Common parser logic for MongoDB attributes.
     * 
     * @param element
     * @param context
     * @param client
     */
    public static void parseMongoAttributes(Element element, ParserContext context, BeanDefinitionBuilder client) {
	Attr hostname = element.getAttributeNode("hostname");
	if (hostname != null) {
	    client.addPropertyValue("hostname", hostname.getValue());
	}
	Attr port = element.getAttributeNode("port");
	if (port != null) {
	    client.addPropertyValue("port", port.getValue());
	}
	Attr databaseName = element.getAttributeNode("databaseName");
	if (databaseName != null) {
	    client.addPropertyValue("databaseName", databaseName.getValue());
	}

	// Determine if username and password are supplied.
	Attr username = element.getAttributeNode("username");
	Attr password = element.getAttributeNode("password");
	if ((username != null) && ((password == null))) {
	    throw new RuntimeException("If username is specified for MongoDB, password must be specified as well.");
	}
	if ((username == null) && ((password != null))) {
	    throw new RuntimeException("If password is specified for MongoDB, username must be specified as well.");
	}
	if ((username != null) && (password != null)) {
	    client.addPropertyValue("username", username.getValue());
	    client.addPropertyValue("password", password.getValue());
	}

	Attr authDatabaseName = element.getAttributeNode("authDatabaseName");
	if (authDatabaseName != null) {
	    client.addPropertyValue("authDatabaseName", authDatabaseName.getValue());
	}

	// Set replica set name if specified.
	Attr replicaSetName = element.getAttributeNode("replicaSetName");
	if (replicaSetName != null) {
	    client.addPropertyValue("replicaSetName", replicaSetName.getValue());
	}

	// Determine if replication set should be created if does not exist.
	Attr autoConfigureReplication = element.getAttributeNode("autoConfigureReplication");
	if (autoConfigureReplication != null) {
	    client.addPropertyValue("autoConfigureReplication", autoConfigureReplication.getValue());
	}
    }

    /**
     * Parse configuration for a InfluxDB datastore.
     * 
     * @param element
     * @param context
     * @return
     */
    protected static DatastoreConfiguration parseInfluxDbDatastore(Element element, ParserContext context) {
	BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(InfluxConfiguration.class);
	parseInfluxAttributes(element, context, configuration);
	return new DatastoreConfiguration(DatastoreConfigurationType.InfluxDB, configuration.getBeanDefinition());
    }

    /**
     * Parse configuration reference for a InfluxDB datastore.
     * 
     * @param element
     * @param context
     * @return
     */
    protected static DatastoreConfiguration parseInfluxDbReference(Element element, ParserContext context) {
	Attr id = element.getAttributeNode("id");
	if (id == null) {
	    throw new RuntimeException("No id specified for InfluxDB configuration.");
	}
	String reference = InstanceManagementBeans.BEAN_INFLUX_CONFIGURATION_BASE + id.getValue();
	return new DatastoreConfiguration(DatastoreConfigurationType.InfluxDBReference, reference);
    }

    /**
     * Parse common InfluxDB configuration attributes.
     * 
     * @param element
     * @param context
     */
    public static void parseInfluxAttributes(Element element, ParserContext context,
	    BeanDefinitionBuilder configuration) {
	Attr hostname = element.getAttributeNode("hostname");
	if (hostname != null) {
	    configuration.addPropertyValue("hostname", hostname.getValue());
	}
	Attr port = element.getAttributeNode("port");
	if (port != null) {
	    configuration.addPropertyValue("port", port.getValue());
	}
	Attr username = element.getAttributeNode("username");
	if (username != null) {
	    configuration.addPropertyValue("username", username.getValue());
	}
	Attr password = element.getAttributeNode("password");
	if (password != null) {
	    configuration.addPropertyValue("password", password.getValue());
	}
	Attr database = element.getAttributeNode("database");
	if (database != null) {
	    configuration.addPropertyValue("database", database.getValue());
	}
	Attr retention = element.getAttributeNode("retention");
	if (retention != null) {
	    configuration.addPropertyValue("retention", retention.getValue());
	}
	Attr enableBatch = element.getAttributeNode("enableBatch");
	if (enableBatch != null) {
	    configuration.addPropertyValue("enableBatch", enableBatch.getValue());
	}
	Attr batchChunkSize = element.getAttributeNode("batchChunkSize");
	if (retention != null) {
	    configuration.addPropertyValue("batchChunkSize", batchChunkSize.getValue());
	}
	Attr batchIntervalMs = element.getAttributeNode("batchIntervalMs");
	if (retention != null) {
	    configuration.addPropertyValue("batchIntervalMs", batchIntervalMs.getValue());
	}
	Attr logLevel = element.getAttributeNode("logLevel");
	if (logLevel != null) {
	    configuration.addPropertyValue("logLevel", logLevel.getValue());
	}
    }
}