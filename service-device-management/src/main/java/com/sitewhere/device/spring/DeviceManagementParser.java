/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.spring;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.device.persistence.mongodb.DeviceManagementMongoClient;
import com.sitewhere.device.persistence.mongodb.MongoDeviceManagement;
import com.sitewhere.microservice.spi.spring.DeviceManagementBeans;
import com.sitewhere.microservice.spi.spring.InstanceGlobalBeans;
import com.sitewhere.spring.parser.IUserManagementParser.Elements;

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
	    case DefaultMongoDatastore: {
		parseDefaultMongoDatastore(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse the default MongoDB datastore element.
     * 
     * @param element
     * @param context
     */
    protected void parseDefaultMongoDatastore(Element element, ParserContext context) {
	// Build MongoDB client using default global configuration.
	BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(DeviceManagementMongoClient.class);
	client.addConstructorArgReference(InstanceGlobalBeans.BEAN_MONGO_CONFIGURATION_DEFAULT);

	context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_MONGODB_CLIENT,
		client.getBeanDefinition());

	// Build device mangement implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoDeviceManagement.class);
	management.addPropertyReference("mongoClient", DeviceManagementBeans.BEAN_MONGODB_CLIENT);

	context.getRegistry().registerBeanDefinition(DeviceManagementBeans.BEAN_DEVICE_MANAGEMENT,
		management.getBeanDefinition());
    }
}