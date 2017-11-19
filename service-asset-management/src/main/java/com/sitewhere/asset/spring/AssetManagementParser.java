/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.spring;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.asset.persistence.mongodb.AssetManagementMongoClient;
import com.sitewhere.asset.persistence.mongodb.MongoAssetManagement;
import com.sitewhere.spi.microservice.spring.AssetManagementBeans;
import com.sitewhere.spi.microservice.spring.InstanceGlobalBeans;
import com.sitewhere.spring.parser.IAssetManagementParser.Elements;

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
	    case DefaultMongoDatastore: {
		parseDefaultMongoDatastore(child, context);
		break;
	    }
	    case AssetModules: {
		(new AssetModulesParser()).parseInternal(child, context);
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
	BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(AssetManagementMongoClient.class);
	client.addConstructorArgReference(InstanceGlobalBeans.BEAN_MONGO_CONFIGURATION_DEFAULT);

	context.getRegistry().registerBeanDefinition(AssetManagementBeans.BEAN_MONGODB_CLIENT,
		client.getBeanDefinition());

	// Build asset management implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoAssetManagement.class);
	management.addPropertyReference("mongoClient", AssetManagementBeans.BEAN_MONGODB_CLIENT);

	context.getRegistry().registerBeanDefinition(AssetManagementBeans.BEAN_ASSET_MANAGEMENT,
		management.getBeanDefinition());
    }
}