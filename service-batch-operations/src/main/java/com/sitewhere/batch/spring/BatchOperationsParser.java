/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.spring;

import java.util.List;

import com.sitewhere.batch.persistence.rdb.BatchManagementRDBClient;
import com.sitewhere.batch.persistence.rdb.RDBBatchManagement;
import com.sitewhere.spi.microservice.spring.DeviceManagementBeans;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.batch.BatchOperationManager;
import com.sitewhere.batch.persistence.mongodb.BatchManagementMongoClient;
import com.sitewhere.batch.persistence.mongodb.MongoBatchManagement;
import com.sitewhere.configuration.datastore.DatastoreConfigurationChoice;
import com.sitewhere.configuration.datastore.DatastoreConfigurationParser;
import com.sitewhere.configuration.parser.IBatchOperationsParser.Elements;
import com.sitewhere.spi.microservice.spring.BatchManagementBeans;

/**
 * Parses elements related to batch operations.
 * 
 * @author Derek
 */
public class BatchOperationsParser extends AbstractBeanDefinitionParser {

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
	    case DeviceManagementDatastore: {
		parseBatchManagementDatastore(child, context);
		break;
	    }
	    case BatchOperationManager: {
		parseBatchOperationManager(child, context);
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
    protected void parseBatchManagementDatastore(Element element, ParserContext context) {
	DatastoreConfigurationChoice config = DatastoreConfigurationParser.parseDeviceManagementDatastoreChoice(element,
		context);

	BeanDefinitionBuilder management;
	switch (config.getType()) {
	case MongoDB: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(BatchManagementMongoClient.class);
	    client.addConstructorArgValue(config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(BatchManagementBeans.BEAN_MONGODB_CLIENT,
		    client.getBeanDefinition());
	    management = buildMongoDeviceManagament();
	    break;
	}
	case MongoDBReference: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(BatchManagementMongoClient.class);
	    client.addConstructorArgReference((String) config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(BatchManagementBeans.BEAN_MONGODB_CLIENT,
		    client.getBeanDefinition());
	    management = buildMongoDeviceManagament();
	    break;
	}
	case RDB: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(BatchManagementRDBClient.class);
	    client.addConstructorArgValue(config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(BatchManagementBeans.BEAN_RDB_CLIENT,
		    client.getBeanDefinition());

	    management = buildRDBDeviceManagament();
	    break;
	}
	case RDBReference: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(BatchManagementRDBClient.class);
	    client.addConstructorArgReference((String) config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(BatchManagementBeans.BEAN_RDB_CLIENT,
		    client.getBeanDefinition());

	    management = buildRDBDeviceManagament();
	    break;
	}
	default: {
	    throw new RuntimeException("Invalid datastore configured: " + config.getType());
	}
	}


	context.getRegistry().registerBeanDefinition(BatchManagementBeans.BEAN_BATCH_MANAGEMENT,
		management.getBeanDefinition());
    }

    /**
     * Parse batch operation manager.
     * 
     * @param element
     * @param context
     */
    protected void parseBatchOperationManager(Element element, ParserContext context) {
	// Build batch management implementation.
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(BatchOperationManager.class);

	Attr throttleDelayMs = element.getAttributeNode("throttleDelayMs");
	if (throttleDelayMs != null) {
	    manager.addPropertyValue("throttleDelayMs", throttleDelayMs.getValue());
	}

	context.getRegistry().registerBeanDefinition(BatchManagementBeans.BEAN_BATCH_OPERATION_MANAGER,
		manager.getBeanDefinition());
    }

    private BeanDefinitionBuilder buildMongoDeviceManagament() {
	// Build device management implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoBatchManagement.class);
	management.addPropertyReference("mongoClient", DeviceManagementBeans.BEAN_MONGODB_CLIENT);
	return management;
    }

    private BeanDefinitionBuilder buildRDBDeviceManagament() {
	// Build device management implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(RDBBatchManagement.class);
	management.addPropertyReference("dbClient", DeviceManagementBeans.BEAN_RDB_CLIENT);
	return management;
    }
}