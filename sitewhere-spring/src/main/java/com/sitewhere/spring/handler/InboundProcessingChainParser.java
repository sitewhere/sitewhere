/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.device.event.processor.DefaultEventStorageProcessor;
import com.sitewhere.device.event.processor.DefaultInboundEventProcessorChain;
import com.sitewhere.device.event.processor.DeviceStreamProcessor;
import com.sitewhere.device.event.processor.RegistrationProcessor;
import com.sitewhere.hazelcast.HazelcastQueueSender;
import com.sitewhere.spring.handler.IInboundProcessingChainParser.Elements;

/**
 * Parses configuration data from SiteWhere inbound processing chain section.
 * 
 * @author Derek
 */
public class InboundProcessingChainParser {

    /**
     * Parse elements for the inbound processing chain.
     * 
     * @param element
     * @param context
     * @return
     */
    protected Object parse(Element element, ParserContext context) {
	BeanDefinitionBuilder chain = BeanDefinitionBuilder.rootBeanDefinition(DefaultInboundEventProcessorChain.class);
	List<Element> dsChildren = DomUtils.getChildElements(element);
	List<Object> processors = new ManagedList<Object>();
	for (Element child : dsChildren) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown inbound processing chain element: " + child.getLocalName());
	    }
	    switch (type) {
	    case InboundEventProcessor: {
		processors.add(parseInboundEventProcessor(child, context));
		break;
	    }
	    case EventStorageProcessor: {
		processors.add(parseEventStorageProcessor(element, context));
		break;
	    }
	    case RegistrationProcessor: {
		processors.add(parseRegistrationProcessor(element, context));
		break;
	    }
	    case DeviceStreamProcessor: {
		processors.add(parseDeviceStreamProcessor(element, context));
		break;
	    }
	    case HazelcastQueueProcessor: {
		processors.add(parseHazelcastQueueProcessor(element, context));
		break;
	    }
	    }
	}
	chain.addPropertyValue("processors", processors);
	return chain.getBeanDefinition();
    }

    /**
     * Parse configuration for custom inbound event processor.
     * 
     * @param element
     * @param context
     * @return
     */
    protected RuntimeBeanReference parseInboundEventProcessor(Element element, ParserContext context) {
	Attr ref = element.getAttributeNode("ref");
	if (ref != null) {
	    return new RuntimeBeanReference(ref.getValue());
	}
	throw new RuntimeException("Inbound event processor does not have ref defined.");
    }

    /**
     * Parse configuration for event storage processor.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseEventStorageProcessor(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(DefaultEventStorageProcessor.class);
	return processor.getBeanDefinition();
    }

    /**
     * Parse configuration for registration processor.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseRegistrationProcessor(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(RegistrationProcessor.class);
	return processor.getBeanDefinition();
    }

    /**
     * Parse configuration for device stream processor.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseDeviceStreamProcessor(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(DeviceStreamProcessor.class);
	return processor.getBeanDefinition();
    }

    /**
     * Parse configuration for Hazelcast queue processor.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseHazelcastQueueProcessor(Element element, ParserContext context) {
	BeanDefinitionBuilder processor = BeanDefinitionBuilder.rootBeanDefinition(HazelcastQueueSender.class);
	return processor.getBeanDefinition();
    }
}