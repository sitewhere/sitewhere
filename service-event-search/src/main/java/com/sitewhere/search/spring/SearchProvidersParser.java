/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search.spring;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.instance.solr.SolrConfigurationChoice;
import com.sitewhere.configuration.instance.solr.SolrConfigurationParser;
import com.sitewhere.configuration.parser.IConnectorCommonParser.Solr;
import com.sitewhere.configuration.parser.IEventSearchParser.SearchProvidersElements;
import com.sitewhere.search.SearchProviderManager;
import com.sitewhere.search.solr.SolrSearchProvider;
import com.sitewhere.spi.microservice.spring.EventSearchBeans;

/**
 * Spring parser for label generator manager.
 * 
 * @author Derek
 */
public class SearchProvidersParser extends AbstractBeanDefinitionParser {

    /*
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal(org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	ManagedList<Object> searchProviders = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    SearchProvidersElements type = SearchProvidersElements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown seaerch providers element: " + child.getLocalName());
	    }
	    switch (type) {
	    case SolrSearchProvider: {
		searchProviders.add(parseSolrSearchProvider(child, context));
		break;
	    }
	    }
	}

	// Build search provider manager and inject the list of beans.
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(SearchProviderManager.class);
	manager.addPropertyValue("searchProviders", searchProviders);
	context.getRegistry().registerBeanDefinition(EventSearchBeans.BEAN_SEARCH_PROVIDERS_MANAGER,
		manager.getBeanDefinition());

	return null;
    }

    /**
     * Parse QR-Code label generator.
     * 
     * @param element
     * @param context
     * @return
     */
    protected BeanDefinition parseSolrSearchProvider(Element element, ParserContext context) {
	BeanDefinitionBuilder provider = BeanDefinitionBuilder.rootBeanDefinition(SolrSearchProvider.class);

	// Provider id.
	Attr id = element.getAttributeNode("id");
	if (id == null) {
	    throw new RuntimeException("No unique id provided for Solr search provider.");
	}
	provider.addPropertyValue("id", id.getValue());

	// Provider name
	Attr name = element.getAttributeNode("name");
	if (name == null) {
	    throw new RuntimeException("No name provided for Solr search provider.");
	}
	provider.addPropertyValue("name", id.getValue());

	// Parse Solr configuration.
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Solr type = Solr.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown solr search provider element: " + child.getLocalName());
	    }
	    switch (type) {
	    case SolrConfigurationChoice: {
		SolrConfigurationChoice config = SolrConfigurationParser.parseSolrConfigurationChoice(child, context);
		switch (config.getType()) {
		case SolrConfiguration: {
		    provider.addPropertyValue("solrConfiguration", config.getConfiguration());
		    break;
		}
		case SolrConfigurationReference: {
		    provider.addPropertyReference("solrConfiguration", (String) config.getConfiguration());
		    break;
		}
		default: {
		    throw new RuntimeException("Invalid Solr configuration specified: " + config.getType());
		}
		}
		break;
	    }
	    }
	}

	return provider.getBeanDefinition();
    }
}
