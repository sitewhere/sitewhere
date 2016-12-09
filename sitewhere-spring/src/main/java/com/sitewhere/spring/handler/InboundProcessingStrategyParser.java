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
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.device.communication.BlockingQueueInboundProcessingStrategy;

/**
 * Parse elements related to inbound processing strategy.
 * 
 * @author Derek
 */
public class InboundProcessingStrategyParser {

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
		throw new RuntimeException("Unknown inbound processing strategy element: " + child.getLocalName());
	    }
	    switch (type) {
	    case BlockingQueueInboundProcessingStrategy:
	    case DefaultInboundProcessingStrategy: {
		return parseDefaultInboundProcessingStrategy(child, context);
	    }
	    }
	}
	return null;
    }

    /**
     * Parse information for the default inbound processing strategy.
     * 
     * @param element
     * @param context
     * @return
     */
    protected BeanDefinition parseDefaultInboundProcessingStrategy(Element element, ParserContext context) {
	BeanDefinitionBuilder manager = BeanDefinitionBuilder
		.rootBeanDefinition(BlockingQueueInboundProcessingStrategy.class);

	Attr maxQueueSize = element.getAttributeNode("maxQueueSize");
	if (maxQueueSize != null) {
	    manager.addPropertyValue("maxQueueSize", maxQueueSize.getValue());
	}

	Attr numEventProcessorThreads = element.getAttributeNode("numEventProcessorThreads");
	if (numEventProcessorThreads != null) {
	    manager.addPropertyValue("eventProcessorThreadCount", numEventProcessorThreads.getValue());
	}

	Attr enableMonitoring = element.getAttributeNode("enableMonitoring");
	if (enableMonitoring != null) {
	    manager.addPropertyValue("enableMonitoring", enableMonitoring.getValue());
	}

	Attr monitoringIntervalSec = element.getAttributeNode("monitoringIntervalSec");
	if (monitoringIntervalSec != null) {
	    manager.addPropertyValue("monitoringIntervalSec", monitoringIntervalSec.getValue());
	}

	return manager.getBeanDefinition();
    }

    /**
     * Parse a registration manager reference.
     * 
     * @param element
     * @param context
     * @return
     */
    protected RuntimeBeanReference parseRegistrationManager(Element element, ParserContext context) {
	Attr ref = element.getAttributeNode("ref");
	if (ref != null) {
	    return new RuntimeBeanReference(ref.getValue());
	}
	throw new RuntimeException("Registration manager reference does not have ref defined.");
    }

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Blocking queue inbound processing strategy */
	BlockingQueueInboundProcessingStrategy("blocking-queue-inbound-processing-strategy"),

	/** Default inbound processing strategy */
	DefaultInboundProcessingStrategy("default-inbound-processing-strategy");

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