/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spring;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.datastore.DatastoreConfigurationParser;
import com.sitewhere.configuration.instance.influxdb.InfluxConfiguration;
import com.sitewhere.configuration.parser.IInstanceManagementParser.InfluxDbElements;
import com.sitewhere.spi.microservice.spring.InstanceManagementBeans;

/**
 * Parses data for global InfluxDB configurations that may be used by tenants.
 * 
 * @author Derek
 */
public class InfluxConfigurationsParser extends AbstractBeanDefinitionParser {

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
	    InfluxDbElements type = InfluxDbElements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown InfluxDB configuration element: " + child.getLocalName());
	    }
	    switch (type) {
	    case InfluxConfiguration: {
		parseInfluxConfiguration(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse an InfluxDB configuration element.
     * 
     * @param element
     * @param context
     */
    protected void parseInfluxConfiguration(Element element, ParserContext context) {
	BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(InfluxConfiguration.class);
	DatastoreConfigurationParser.parseInfluxAttributes(element, context, configuration);

	Attr id = element.getAttributeNode("id");
	if (id == null) {
	    throw new RuntimeException("No id specified for InfluxDB configuation.");
	}

	// Register bean using id as part of name.
	String beanName = InstanceManagementBeans.BEAN_INFLUX_CONFIGURATION_BASE + id.getValue();
	context.getRegistry().registerBeanDefinition(beanName, configuration.getBeanDefinition());
    }
}