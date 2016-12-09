/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.server.SiteWhereServerBeans;
import com.sitewhere.server.search.SearchProviderManager;
import com.sitewhere.solr.SiteWhereSolrConfiguration;
import com.sitewhere.solr.search.SolrSearchProvider;

/**
 * Parses configuration information for the 'search-providers' section.
 * 
 * @author Derek
 */
public class SearchProvidersParser extends AbstractBeanDefinitionParser {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(SearchProviderManager.class);

	ManagedList<?> providers = parseSearchProviders(element, context);
	manager.addPropertyValue("searchProviders", providers);

	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_SEARCH_PROVIDER_MANAGER,
		manager.getBeanDefinition());
	return null;
    }

    /**
     * Parse the list of serach providers from nested elements.
     * 
     * @param element
     * @param context
     * @return
     */
    protected ManagedList<?> parseSearchProviders(Element element, ParserContext context) {
	ManagedList<Object> result = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    if (!IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())) {
		NamespaceHandler nested = context.getReaderContext().getNamespaceHandlerResolver()
			.resolve(child.getNamespaceURI());
		if (nested != null) {
		    nested.parse(child, context);
		    continue;
		} else {
		    throw new RuntimeException(
			    "Invalid nested element found in 'search-providers' section: " + child.toString());
		}
	    }
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown event source element: " + child.getLocalName());
	    }
	    switch (type) {
	    case SolrSearchProvider: {
		result.add(parseSolrSearchProvider(child, context));
		break;
	    }
	    }
	}
	return result;
    }

    /**
     * Parse configuration for the Apache Solr search provider.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseSolrSearchProvider(Element element, ParserContext context) {
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

	// Reference to global configuration.
	provider.addPropertyReference("solr", SiteWhereSolrConfiguration.SOLR_CONFIGURATION_BEAN);

	return provider.getBeanDefinition();
    }

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Solr search provider */
	SolrSearchProvider("solr-search-provider");

	/** Event code */
	private String localName;

	private Elements(String localName) {
	    this.localName = localName;
	}

	public static Elements getByLocalName(String localName) {
	    for (Elements value : Elements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }
}