/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.spring;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.spi.microservice.spring.InstanceGlobalBeans;
import com.sitewhere.spi.microservice.spring.TenantManagementBeans;
import com.sitewhere.spring.parser.ITenantManagementParser.Elements;
import com.sitewhere.tenant.persistence.mongodb.MongoTenantManagement;
import com.sitewhere.tenant.persistence.mongodb.TenantManagementMongoClient;

/**
 * Parses configuration data for the SiteWhere tenant management microservice.
 * 
 * @author Derek
 */
public class TenantManagementParser extends AbstractBeanDefinitionParser {

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
		throw new RuntimeException("Unknown tenant management element: " + child.getLocalName());
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
	BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(TenantManagementMongoClient.class);
	client.addConstructorArgReference(InstanceGlobalBeans.BEAN_MONGO_CONFIGURATION_DEFAULT);

	context.getRegistry().registerBeanDefinition(TenantManagementBeans.BEAN_MONGODB_CLIENT,
		client.getBeanDefinition());

	// Build tenant mangement implementation.
	BeanDefinitionBuilder management = BeanDefinitionBuilder.rootBeanDefinition(MongoTenantManagement.class);
	management.addPropertyReference("mongoClient", TenantManagementBeans.BEAN_MONGODB_CLIENT);

	context.getRegistry().registerBeanDefinition(TenantManagementBeans.BEAN_TENANT_MANAGEMENT,
		management.getBeanDefinition());
    }
}