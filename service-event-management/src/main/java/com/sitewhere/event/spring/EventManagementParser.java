/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.spring;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.cassandra.CassandraClient;
import com.sitewhere.configuration.datastore.DatastoreConfiguration;
import com.sitewhere.configuration.datastore.DatastoreConfigurationParser;
import com.sitewhere.configuration.parser.IEventManagementParser.Elements;
import com.sitewhere.event.persistence.cassandra.CassandraDeviceEventManagement;
import com.sitewhere.event.persistence.influxdb.InfluxDbDeviceEventManagement;
import com.sitewhere.event.persistence.mongodb.DeviceEventManagementMongoClient;
import com.sitewhere.event.persistence.mongodb.MongoDeviceEventManagement;
import com.sitewhere.influxdb.InfluxDbClient;
import com.sitewhere.spi.microservice.spring.EventManagementBeans;

/**
 * Parses configuration data for the SiteWhere event management microservice.
 * 
 * @author Derek
 */
public class EventManagementParser extends AbstractBeanDefinitionParser {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	List<Element> dsChildren = DomUtils.getChildElements(element);
	for (Element child : dsChildren) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown event management element: " + child.getLocalName());
	    }
	    switch (type) {
	    case EventManagementDatastore: {
		parseEventManagementDatastore(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse device management datastore element.
     * 
     * @param element
     * @param context
     */
    protected void parseEventManagementDatastore(Element element, ParserContext context) {
	DatastoreConfiguration config = DatastoreConfigurationParser.parseEventManagementDatastore(element, context);
	switch (config.getType()) {
	case MongoDB: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder
		    .rootBeanDefinition(DeviceEventManagementMongoClient.class);
	    client.addConstructorArgValue(config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_MONGODB_CLIENT,
		    client.getBeanDefinition());

	    BeanDefinitionBuilder management = BeanDefinitionBuilder
		    .rootBeanDefinition(MongoDeviceEventManagement.class);
	    management.addPropertyReference("mongoClient", EventManagementBeans.BEAN_MONGODB_CLIENT);

	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_EVENT_MANAGEMENT,
		    management.getBeanDefinition());
	    break;
	}
	case MongoDBReference: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder
		    .rootBeanDefinition(DeviceEventManagementMongoClient.class);
	    client.addConstructorArgReference((String) config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_MONGODB_CLIENT,
		    client.getBeanDefinition());

	    BeanDefinitionBuilder management = BeanDefinitionBuilder
		    .rootBeanDefinition(MongoDeviceEventManagement.class);
	    management.addPropertyReference("mongoClient", EventManagementBeans.BEAN_MONGODB_CLIENT);

	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_EVENT_MANAGEMENT,
		    management.getBeanDefinition());
	    break;
	}
	case InfluxDB: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(InfluxDbClient.class);
	    client.addConstructorArgValue(config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_INFLUXDB_CLIENT,
		    client.getBeanDefinition());

	    BeanDefinitionBuilder management = BeanDefinitionBuilder
		    .rootBeanDefinition(InfluxDbDeviceEventManagement.class);
	    management.addPropertyReference("client", EventManagementBeans.BEAN_INFLUXDB_CLIENT);

	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_EVENT_MANAGEMENT,
		    management.getBeanDefinition());
	    break;
	}
	case InfluxDBReference: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(InfluxDbClient.class);
	    client.addConstructorArgReference((String) config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_INFLUXDB_CLIENT,
		    client.getBeanDefinition());

	    BeanDefinitionBuilder management = BeanDefinitionBuilder
		    .rootBeanDefinition(InfluxDbDeviceEventManagement.class);
	    management.addPropertyReference("client", EventManagementBeans.BEAN_INFLUXDB_CLIENT);

	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_EVENT_MANAGEMENT,
		    management.getBeanDefinition());
	    break;
	}
	case Cassandra: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(CassandraClient.class);
	    client.addConstructorArgValue(config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_CASSANDRA_CLIENT,
		    client.getBeanDefinition());

	    BeanDefinitionBuilder management = BeanDefinitionBuilder
		    .rootBeanDefinition(CassandraDeviceEventManagement.class);
	    management.addPropertyReference("client", EventManagementBeans.BEAN_CASSANDRA_CLIENT);

	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_EVENT_MANAGEMENT,
		    management.getBeanDefinition());
	    break;
	}
	case CassandraReference: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(CassandraClient.class);
	    client.addConstructorArgReference((String) config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_CASSANDRA_CLIENT,
		    client.getBeanDefinition());

	    BeanDefinitionBuilder management = BeanDefinitionBuilder
		    .rootBeanDefinition(CassandraDeviceEventManagement.class);
	    management.addPropertyReference("client", EventManagementBeans.BEAN_CASSANDRA_CLIENT);

	    context.getRegistry().registerBeanDefinition(EventManagementBeans.BEAN_EVENT_MANAGEMENT,
		    management.getBeanDefinition());
	    break;
	}
	default: {
	    throw new RuntimeException("Invalid datastore configured: " + config.getType());
	}
	}
    }
}