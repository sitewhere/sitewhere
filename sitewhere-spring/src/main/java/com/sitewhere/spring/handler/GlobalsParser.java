/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.solr.SiteWhereSolrConfiguration;
import com.sitewhere.spring.handler.IGlobalsParser.Elements;

/**
 * Parses configuration information for the 'globals' section.
 * 
 * @author Derek
 */
public class GlobalsParser extends AbstractBeanDefinitionParser {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    @SuppressWarnings("deprecation")
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	List<Element> dsChildren = DomUtils.getChildElements(element);
	for (Element child : dsChildren) {
	    if (!SiteWhereDomUtils.hasSiteWhereNamespace(child)) {
		NamespaceHandler nested = context.getReaderContext().getNamespaceHandlerResolver()
			.resolve(child.getNamespaceURI());
		if (nested != null) {
		    nested.parse(child, context);
		    continue;
		} else {
		    throw new RuntimeException(
			    "Invalid nested element found in 'globals' section: " + child.toString());
		}
	    }
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown globals element: " + child.getLocalName());
	    }
	    switch (type) {
	    case HazelcastConfiguration: {
		// Hazelcast is no longer configured globally.
		break;
	    }
	    case SolrConfiguration: {
		parseSolrConfiguration(child, context);
		break;
	    }
	    case GroovyConfiguration: {
		parseGroovyConfiguration(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse the global Solr configuration.
     * 
     * @param element
     * @param context
     */
    protected void parseSolrConfiguration(Element element, ParserContext context) {
	BeanDefinitionBuilder config = BeanDefinitionBuilder.rootBeanDefinition(SiteWhereSolrConfiguration.class);

	Attr solrServerUrl = element.getAttributeNode("solrServerUrl");
	if (solrServerUrl != null) {
	    config.addPropertyValue("solrServerUrl", solrServerUrl.getValue());
	}

	context.getRegistry().registerBeanDefinition(SiteWhereSolrConfiguration.SOLR_CONFIGURATION_BEAN,
		config.getBeanDefinition());
    }

    /**
     * Parse the global Groovy configuration.
     * 
     * @param element
     * @param context
     */
    protected void parseGroovyConfiguration(Element element, ParserContext context) {
	LOGGER.warn("Groovy configuration element has been deprecated.");
    }
}
