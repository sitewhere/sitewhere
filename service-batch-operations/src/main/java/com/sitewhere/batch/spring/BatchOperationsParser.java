/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.spring;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.batch.persistence.mongodb.BatchManagementMongoClient;
import com.sitewhere.batch.persistence.mongodb.MongoBatchManagement;
import com.sitewhere.configuration.parser.IBatchOperationsParser.Elements;
import com.sitewhere.spi.microservice.spring.BatchManagementBeans;
import com.sitewhere.spi.microservice.spring.InstanceManagementBeans;

/**
 * Parses elements related to batch operations.
 * 
 * @author Derek
 */
public class BatchOperationsParser extends AbstractBeanDefinitionParser {

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
		throw new RuntimeException("Unknown batch operations element: " + child.getLocalName());
	    }
	    switch (type) {
	    case DefaultMongoDatastore: {
		parseDefaultMongoDatastore(child, context);
		break;
	    }
	    case BatchOperationManager: {
		break;
	    }
	    case DefaultBatchOperationManager: {
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
	BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(BatchManagementMongoClient.class);
	client.addConstructorArgReference(InstanceManagementBeans.BEAN_MONGO_CONFIGURATION_DEFAULT);

	context.getRegistry().registerBeanDefinition(BatchManagementBeans.BEAN_MONGODB_CLIENT,
		client.getBeanDefinition());

	// Build batch management implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoBatchManagement.class);
	management.addPropertyReference("mongoClient", BatchManagementBeans.BEAN_MONGODB_CLIENT);

	context.getRegistry().registerBeanDefinition(BatchManagementBeans.BEAN_BATCH_MANAGEMENT,
		management.getBeanDefinition());
    }
}