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

import com.sitewhere.configuration.instance.solr.SolrConfiguration;
import com.sitewhere.configuration.parser.IInstanceManagementParser.SolrElements;
import com.sitewhere.spi.microservice.spring.InstanceManagementBeans;

/**
 * Parses data for global Solr configurations that may be used by tenants.
 * 
 * @author Derek
 */
public class SolrConfigurationsParser extends AbstractBeanDefinitionParser {

    /*
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal(org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    SolrElements type = SolrElements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown Solr configuration element: " + child.getLocalName());
	    }
	    switch (type) {
	    case DefaultSolrConfiguration: {
		parseDefaultSolrConfiguration(child, context);
		break;
	    }
	    case AlternateSolrConfiguration: {
		parseAlternateSolrConfiguration(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse the default Solr configuration element.
     * 
     * @param element
     * @param context
     */
    protected void parseDefaultSolrConfiguration(Element element, ParserContext context) {
	BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(SolrConfiguration.class);
	parseSolrAttributes(element, context, configuration);
	context.getRegistry().registerBeanDefinition(InstanceManagementBeans.BEAN_SOLR_CONFIGURATION_DEFAULT,
		configuration.getBeanDefinition());
    }

    /**
     * Parse an alternate Solr configuration element.
     * 
     * @param element
     * @param context
     */
    protected void parseAlternateSolrConfiguration(Element element, ParserContext context) {
	BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(SolrConfiguration.class);
	parseSolrAttributes(element, context, configuration);

	Attr id = element.getAttributeNode("id");
	if (id == null) {
	    throw new RuntimeException("No id specified for Solr alternate configuation.");
	}

	// Register bean using id as part of name.
	String beanName = InstanceManagementBeans.BEAN_SOLR_CONFIGURATION_BASE + id.getValue();
	context.getRegistry().registerBeanDefinition(beanName, configuration.getBeanDefinition());
    }

    /**
     * Common parser logic for Solr attributes.
     * 
     * @param element
     * @param context
     * @param client
     */
    public static void parseSolrAttributes(Element element, ParserContext context, BeanDefinitionBuilder config) {
	Attr solrServerUrl = element.getAttributeNode("solrServerUrl");
	if (solrServerUrl != null) {
	    config.addPropertyValue("solrServerUrl", solrServerUrl.getValue());
	}
    }
}