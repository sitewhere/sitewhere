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
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.configuration.parser.IInstanceManagementParser.PersistenceConfigurationsElements;
import com.sitewhere.configuration.parser.IInstanceManagementParser.TopLevelElements;

/**
 * Parses configuration data for the instance global configuration.
 * 
 * @author Derek
 */
public class InstanceManagementParser extends AbstractBeanDefinitionParser {

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
	    TopLevelElements type = TopLevelElements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown top level instance management element: " + child.getLocalName());
	    }
	    switch (type) {
	    case PersistenceConfigurations: {
		parsePersistenceConfigurations(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse a MongoDB datasource configuration and create beans needed to realize
     * it.
     * 
     * @param element
     * @param context
     */
    protected void parsePersistenceConfigurations(Element element, ParserContext context) {
	List<Element> dsChildren = DomUtils.getChildElements(element);
	for (Element child : dsChildren) {
	    PersistenceConfigurationsElements type = PersistenceConfigurationsElements
		    .getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown persistence configurations element: " + child.getLocalName());
	    }
	    switch (type) {
	    case MongoConfigurations: {
		parseMongoConfigurations(child, context);
		break;
	    }
	    }
	}
    }

    /**
     * Parse a MongoDB datasource configuration and create beans needed to realize
     * it.
     * 
     * @param element
     * @param context
     */
    protected void parseMongoConfigurations(Element element, ParserContext context) {
	(new MongoConfigurationParser()).parse(element, context);
    }
}