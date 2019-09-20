/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.spring;

import java.util.List;

import com.sitewhere.device.persistence.rdb.RDBDeviceManagement;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.configuration.datastore.DatastoreConfigurationChoice;
import com.sitewhere.configuration.datastore.DatastoreConfigurationParser;
import com.sitewhere.configuration.parser.IDeviceManagementParser.Elements;
import com.sitewhere.device.persistence.mongodb.DeviceManagementMongoClient;
import com.sitewhere.device.persistence.mongodb.MongoDeviceManagement;
import com.sitewhere.spi.microservice.spring.DeviceManagementBeans;

/**
 * Parses configuration data for the SiteWhere device management microservice.
 * 
 * @author Derek
 */
public class DeviceManagementParser extends AbstractBeanDefinitionParser {

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
		throw new RuntimeException("Unknown device management element: " + child.getLocalName());
	    }
	    switch (type) {
	    case DeviceManagementDatastore: {
		parseDeviceManagementDatastore(child, context);
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
	protected void parseDeviceManagementDatastore(Element element, ParserContext context) {
		DatastoreConfigurationChoice config = DatastoreConfigurationParser.parseDeviceManagementDatastoreChoice(element,
				context);
		BeanDefinitionBuilder management;
		switch (config.getType()) {
			case MongoDB: {
				BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(DeviceManagementMongoClient.class);
				client.addConstructorArgValue(config.getConfiguration());
				context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_MONGODB_CLIENT,
						client.getBeanDefinition());

				management = buildMongoDeviceManagament();
				context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_DEVICE_MANAGEMENT,
						management.getBeanDefinition());
				break;
			}
			case MongoDBReference: {
				BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(DeviceManagementMongoClient.class);
				client.addConstructorArgReference((String) config.getConfiguration());
				context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_MONGODB_CLIENT,
						client.getBeanDefinition());

				management = buildMongoDeviceManagament();
				context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_DEVICE_MANAGEMENT,
						management.getBeanDefinition());
				break;
			}
			case RDB: {
				BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(RDBDeviceManagement.class);
				client.addConstructorArgValue(config.getConfiguration());
				context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_RDB_CLIENT,
						client.getBeanDefinition());

				management = buildRDBDeviceManagament();
				break;
			}
			case RDBReference: {
				BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(RDBDeviceManagement.class);
				client.addConstructorArgReference((String) config.getConfiguration());
				context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_RDB_CLIENT,
						client.getBeanDefinition());

				management = buildRDBDeviceManagament();
				context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_DEVICE_MANAGEMENT,
						management.getBeanDefinition());
				break;
			}
			default: {
				throw new RuntimeException("Invalid datastore configured: " + config.getType());
			}
		}

		context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_DEVICE_MANAGEMENT,
				management.getBeanDefinition());

	}

	private BeanDefinitionBuilder buildMongoDeviceManagament() {
		// Build device management implementation.
		BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoDeviceManagement.class);
		management.addPropertyReference("mongoClient", DeviceManagementBeans.BEAN_MONGODB_CLIENT);
		return management;
	}

	private BeanDefinitionBuilder buildRDBDeviceManagament() {
		// Build device management implementation.
		BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoDeviceManagement.class);
		management.addPropertyReference("rdbClient", DeviceManagementBeans.BEAN_RDB_CLIENT);
		return management;
	}
}