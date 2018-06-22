/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.instance.solr;

import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.parser.IConnectorCommonParser.SolrConnectorElements;
import com.sitewhere.spi.microservice.spring.InstanceManagementBeans;

/**
 * Parses common Solr configuration elements.
 * 
 * @author Derek
 */
public class SolrConfigurationParser {

    /**
     * Parse potential configuration options for a Solr connectors and return
     * configuration details.
     * 
     * @param element
     * @param context
     * @return
     */
    public static SolrConfigurationChoice parseSolrConfigurationChoice(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    SolrConnectorElements type = SolrConnectorElements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown Solr configuration element: " + child.getLocalName());
	    }
	    switch (type) {
	    case SolrConfiguration: {
		return parseSolrConfiguration(child, context);
	    }
	    case SolrConfigurationReference: {
		return parseMongoDbReference(child, context);
	    }
	    }
	}
	return null;
    }

    /**
     * Parse a Solr configuration element.
     * 
     * @param element
     * @param context
     * @return
     */
    protected static SolrConfigurationChoice parseSolrConfiguration(Element element, ParserContext context) {
	BeanDefinitionBuilder solr = BeanDefinitionBuilder.rootBeanDefinition(SolrConfiguration.class);

	Attr solrServerUrl = element.getAttributeNode("solrServerUrl");
	if (solrServerUrl != null) {
	    solr.addPropertyValue("solrServerUrl", solrServerUrl.getValue());
	}

	return new SolrConfigurationChoice(SolrConfigurationType.SolrConfiguration, solr.getBeanDefinition());
    }

    /**
     * Parse reference to global Solr configuration.
     * 
     * @param element
     * @param context
     * @return
     */
    protected static SolrConfigurationChoice parseMongoDbReference(Element element, ParserContext context) {
	Attr id = element.getAttributeNode("id");
	if (id == null) {
	    throw new RuntimeException("No id specified for Solr configuration.");
	}
	String reference = InstanceManagementBeans.BEAN_SOLR_CONFIGURATION_BASE + id.getValue();
	return new SolrConfigurationChoice(SolrConfigurationType.SolrConfigurationReference, reference);
    }
}
