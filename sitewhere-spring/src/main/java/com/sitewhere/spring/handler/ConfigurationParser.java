/*
 * ConfigurationParser.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Parses the top-level element for SiteWhere Spring configuration.
 * 
 * @author Derek
 */
public class ConfigurationParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		List<Element> sections = DomUtils.getChildElements(element);
		for (Element section : sections) {
			if (!IConfigurationElements.SITEWHERE_NS.equals(section.getNamespaceURI())) {
				continue;
			}
			if (IConfigurationElements.DATASTORE.equals(section.getLocalName())) {
				new DatastoreParser().parse(section, context);
			}
			if (IConfigurationElements.INBOUND_PROCESSING_CHAIN.equals(section.getLocalName())) {
				new InboundProcessingChainParser().parse(section, context);
			}
			if (IConfigurationElements.OUTBOUND_PROCESSING_CHAIN.equals(section.getLocalName())) {
				new OutboundProcessingChainParser().parse(section, context);
			}
		}
		return null;
	}
}