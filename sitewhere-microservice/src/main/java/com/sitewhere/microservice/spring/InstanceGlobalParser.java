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

import com.sitewhere.spring.handler.IInstanceGlobalParser.Elements;

/**
 * Parses configuration data for the instance global configuration.
 * 
 * @author Derek
 */
public class InstanceGlobalParser extends AbstractBeanDefinitionParser {

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
		throw new RuntimeException("Unknown instance global element: " + child.getLocalName());
	    }
	    switch (type) {
	    case MongoDatastore: {
		parseMongoDatasource(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse a MongoDB datasource configuration and create beans needed to
     * realize it.
     * 
     * @param element
     * @param context
     */
    protected void parseMongoDatasource(Element element, ParserContext context) {
    }
}