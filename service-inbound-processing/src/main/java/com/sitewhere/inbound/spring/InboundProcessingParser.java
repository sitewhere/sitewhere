/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.inbound.processing.InboundProcessingConfiguration;
import com.sitewhere.spi.microservice.spring.InboundProcessingBeans;

/**
 * Parses elements related to inbound event processing.
 * 
 * @author Derek
 */
public class InboundProcessingParser extends AbstractBeanDefinitionParser {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(InboundProcessingParser.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {

	// Build event sources manager and inject the list of beans.
	BeanDefinitionBuilder config = BeanDefinitionBuilder.rootBeanDefinition(InboundProcessingConfiguration.class);

	Attr processingThreadCount = element.getAttributeNode("processingThreadCount");
	if (processingThreadCount != null) {
	    config.addPropertyValue("processingThreadCount", processingThreadCount.getValue());
	}

	context.getRegistry().registerBeanDefinition(InboundProcessingBeans.BEAN_INBOUND_PROCESSING_CONFIGURATION,
		config.getBeanDefinition());

	return null;
    }
}