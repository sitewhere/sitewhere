/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spring;

import com.sitewhere.configuration.datastore.DatastoreConfigurationParser;
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import com.sitewhere.configuration.parser.IInstanceManagementParser.RDBElements;
import com.sitewhere.spi.microservice.spring.InstanceManagementBeans;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.List;


/**
 * Parses data for global RDB configurations that may be used by tenants.
 * 
 * @author Derek
 */
public class RDBConfigurationsParser extends AbstractBeanDefinitionParser {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal(org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
		RDBElements type = RDBElements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown RDB configuration element: " + child.getLocalName());
	    }
	    switch (type) {
	    case RDBConfiguration: {
			parseRDBConfiguration(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse a RDB configuration element.
     * 
     * @param element
     * @param context
     */
	protected void parseRDBConfiguration(Element element, ParserContext context) {
		BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(RDBConfiguration.class);

		Attr id = element.getAttributeNode("id");
		if (id == null) {
			throw new RuntimeException("No id specified for RDB configuration.");
		}
		DatastoreConfigurationParser.parseRDBAttributes(element, context, configuration);

		// Register bean using id as part of name.
		String beanName = InstanceManagementBeans.BEAN_RDB_CONFIGURATION_BASE + id.getValue();
		context.getRegistry().registerBeanDefinition(beanName, configuration.getBeanDefinition());
	}
}