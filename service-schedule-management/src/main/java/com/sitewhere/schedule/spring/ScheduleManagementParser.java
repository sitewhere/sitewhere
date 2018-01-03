/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.spring;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.configuration.parser.IScheduleManagementParser.Elements;
import com.sitewhere.schedule.persistence.mongodb.MongoScheduleManagement;
import com.sitewhere.schedule.persistence.mongodb.ScheduleManagementMongoClient;
import com.sitewhere.spi.microservice.spring.InstanceManagementBeans;
import com.sitewhere.spi.microservice.spring.ScheduleManagementBeans;

/**
 * Parses elements related to schedule management.
 * 
 * @author Derek
 */
public class ScheduleManagementParser extends AbstractBeanDefinitionParser {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

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
		throw new RuntimeException("Unknown schedule management element: " + child.getLocalName());
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
	BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(ScheduleManagementMongoClient.class);
	client.addConstructorArgReference(InstanceManagementBeans.BEAN_MONGO_CONFIGURATION_DEFAULT);

	context.getRegistry().registerBeanDefinition(ScheduleManagementBeans.BEAN_MONGODB_CLIENT,
		client.getBeanDefinition());

	// Build schedule management implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoScheduleManagement.class);
	management.addPropertyReference("mongoClient", ScheduleManagementBeans.BEAN_MONGODB_CLIENT);

	context.getRegistry().registerBeanDefinition(ScheduleManagementBeans.BEAN_SCHEDULE_MANAGEMENT,
		management.getBeanDefinition());
    }
}