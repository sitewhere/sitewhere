/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.device.communication.BlockingQueueOutboundProcessingStrategy;
import com.sitewhere.spring.handler.IOutboundProcessingStrategyParser.Elements;

/**
 * Parse elements related to outbound processing strategy.
 * 
 * @author Derek
 */
public class OutboundProcessingStrategyParser {

    /**
     * Parse elements in the inbound processing strategy section.
     * 
     * @param element
     * @param context
     * @return
     */
    protected Object parse(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown outbound processing strategy element: " + child.getLocalName());
	    }
	    switch (type) {
	    case BlockingQueueOutboundProcessingStrategy:
	    case DefaultOutboundProcessingStrategy: {
		return parseDefaultOutboundProcessingStrategy(child, context);
	    }
	    }
	}
	return null;
    }

    /**
     * Parse information for the default outbound processing strategy.
     * 
     * @param element
     * @param context
     * @return
     */
    protected BeanDefinition parseDefaultOutboundProcessingStrategy(Element element, ParserContext context) {
	BeanDefinitionBuilder manager = BeanDefinitionBuilder
		.rootBeanDefinition(BlockingQueueOutboundProcessingStrategy.class);

	Attr maxQueueSize = element.getAttributeNode("maxQueueSize");
	if (maxQueueSize != null) {
	    manager.addPropertyValue("maxQueueSize", maxQueueSize.getValue());
	}

	Attr numEventProcessorThreads = element.getAttributeNode("numEventProcessorThreads");
	if (numEventProcessorThreads != null) {
	    manager.addPropertyValue("eventProcessorThreadCount", numEventProcessorThreads.getValue());
	}

	return manager.getBeanDefinition();
    }
}