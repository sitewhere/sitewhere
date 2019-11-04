/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.spring;

import java.util.List;

import com.sitewhere.asset.persistence.rdb.AssetManagementRDBClient;
import com.sitewhere.asset.persistence.rdb.RDBAssetManagement;
import com.sitewhere.spi.microservice.spring.DeviceManagementBeans;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.asset.persistence.mongodb.AssetManagementMongoClient;
import com.sitewhere.asset.persistence.mongodb.MongoAssetManagement;
import com.sitewhere.configuration.datastore.DatastoreConfigurationChoice;
import com.sitewhere.configuration.datastore.DatastoreConfigurationParser;
import com.sitewhere.configuration.parser.IAssetManagementParser.Elements;
import com.sitewhere.spi.microservice.spring.AssetManagementBeans;

/**
 * Parses configuration data for the SiteWhere asset management section.
 * 
 * @author Derek
 */
public class AssetManagementParser extends AbstractBeanDefinitionParser {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown asset management element: " + child.getLocalName());
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
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(AssetManagementMongoClient.class);
	    client.addConstructorArgValue(config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(AssetManagementBeans.BEAN_MONGODB_CLIENT,
		    client.getBeanDefinition());

	    management = buildMongoDeviceManagament();
	    break;
	}
	case MongoDBReference: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(AssetManagementMongoClient.class);
	    client.addConstructorArgReference((String) config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(AssetManagementBeans.BEAN_MONGODB_CLIENT,
		    client.getBeanDefinition());
	    management = buildMongoDeviceManagament();
	    break;
	}
	case RDB: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(AssetManagementRDBClient.class);
	    client.addConstructorArgValue(config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(AssetManagementBeans.BEAN_RDB_CLIENT,
		    client.getBeanDefinition());
	    management = buildRDBDeviceManagament();
	    break;
	}
	case RDBReference: {
	    BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(AssetManagementRDBClient.class);
	    client.addConstructorArgReference((String) config.getConfiguration());
	    context.getRegistry().registerBeanDefinition(AssetManagementBeans.BEAN_RDB_CLIENT,
		    client.getBeanDefinition());
	    management = buildRDBDeviceManagament();
	    break;
	}
	default: {
	    throw new RuntimeException("Invalid datastore configured: " + config.getType());
	}
	}

	context.getRegistry().registerBeanDefinition(AssetManagementBeans.BEAN_ASSET_MANAGEMENT,
		management.getBeanDefinition());
    }

    private BeanDefinitionBuilder buildMongoDeviceManagament() {
	// Build asset management implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoAssetManagement.class);
	management.addPropertyReference("mongoClient", AssetManagementBeans.BEAN_MONGODB_CLIENT);
	return management;
    }

    private BeanDefinitionBuilder buildRDBDeviceManagament() {
	// Build asset management implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(RDBAssetManagement.class);
	management.addPropertyReference("dbClient", AssetManagementBeans.BEAN_RDB_CLIENT);
	return management;
    }
}