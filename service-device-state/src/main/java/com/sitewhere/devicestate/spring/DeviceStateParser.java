/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.spring;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.datastore.DatastoreConfigurationChoice;
import com.sitewhere.configuration.datastore.DatastoreConfigurationParser;
import com.sitewhere.configuration.parser.IDeviceStateManagementParser.Elements;
import com.sitewhere.devicestate.persistence.mongodb.DeviceStateManagementMongoClient;
import com.sitewhere.devicestate.persistence.mongodb.MongoDeviceStateManagement;
import com.sitewhere.devicestate.presence.DevicePresenceManager;
import com.sitewhere.spi.microservice.spring.DeviceStateManagementBeans;

/**
 * Parses elements related to device presence management.
 * 
 * @author Derek
 */
public class DeviceStateParser extends AbstractBeanDefinitionParser {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceStateParser.class);

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
	    case DeviceStateDatastore: {
		parseDeviceStateDatastore(child, context);
		break;
	    }
	    case PresenceManager: {
		parsePresenceManager(element, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse device state datastore element.
     * 
     * @param element
     * @param context
     */
    protected void parseDeviceStateDatastore(Element element, ParserContext context) {
	DatastoreConfigurationChoice config = DatastoreConfigurationParser.parseDeviceStateDatastoreChoice(element,
		context);
	switch (config.getType()) {
	case MongoDB: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder
		    .rootBeanDefinition(DeviceStateManagementMongoClient.class);
	    client.addConstructorArgValue(config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(DeviceStateManagementBeans.BEAN_MONGODB_CLIENT,
		    client.getBeanDefinition());
	    break;
	}
	case MongoDBReference: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder
		    .rootBeanDefinition(DeviceStateManagementMongoClient.class);
	    client.addConstructorArgReference((String) config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(DeviceStateManagementBeans.BEAN_MONGODB_CLIENT,
		    client.getBeanDefinition());
	    break;
	}
	default: {
	    throw new RuntimeException("Invalid datastore configured: " + config.getType());
	}
	}

	// Build device management implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoDeviceStateManagement.class);
	management.addPropertyReference("mongoClient", DeviceStateManagementBeans.BEAN_MONGODB_CLIENT);

	context.getRegistry().registerBeanDefinition(DeviceStateManagementBeans.BEAN_DEVICE_STATE_MANAGEMENT,
		management.getBeanDefinition());
    }

    /**
     * Parse configuration for presence manager.
     * 
     * @param element
     * @param context
     */
    protected void parsePresenceManager(Element element, ParserContext context) {
	BeanDefinitionBuilder presence = BeanDefinitionBuilder.rootBeanDefinition(DevicePresenceManager.class);

	Attr checkInterval = element.getAttributeNode("checkInterval");
	if (checkInterval != null) {
	    presence.addPropertyValue("presenceCheckInterval", checkInterval.getValue());
	}

	Attr presenceMissingInterval = element.getAttributeNode("presenceMissingInterval");
	if (presenceMissingInterval != null) {
	    presence.addPropertyValue("presenceMissingInterval", presenceMissingInterval.getValue());
	}

	context.getRegistry().registerBeanDefinition(DeviceStateManagementBeans.BEAN_PRESENCE_MANAGER,
		presence.getBeanDefinition());
    }
}