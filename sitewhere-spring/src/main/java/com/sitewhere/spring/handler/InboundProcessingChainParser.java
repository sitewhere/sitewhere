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
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.device.event.processor.DefaultEventStorageProcessor;
import com.sitewhere.device.event.processor.DefaultInboundEventProcessorChain;
import com.sitewhere.server.SiteWhereServerBeans;

/**
 * Parses configuration data from SiteWhere inbound processing chain section.
 * 
 * @author Derek
 */
public class InboundProcessingChainParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		BeanDefinitionBuilder chain =
				BeanDefinitionBuilder.rootBeanDefinition(DefaultInboundEventProcessorChain.class);
		List<Element> dsChildren = DomUtils.getChildElements(element);
		List<Object> processors = new ManagedList<Object>();
		for (Element child : dsChildren) {
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown inbound processing chain element: "
						+ child.getLocalName());
			}
			switch (type) {
			case InboundEventProcessor: {
				processors.add(parseInboundEventProcessor(child, context));
				break;
			}
			case DefaultEventStorageProcessor: {
				processors.add(parseDefaultEventStorageProcessor(element, context));
				break;
			}
			}
		}
		chain.addPropertyValue("processors", processors);
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_INBOUND_PROCESSOR_CHAIN,
				chain.getBeanDefinition());
		return null;
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
	 * Parse configuration for custom inbound event processor.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected AbstractBeanDefinition parseDefaultEventStorageProcessor(Element element, ParserContext context) {
		BeanDefinitionBuilder processor =
				BeanDefinitionBuilder.rootBeanDefinition(DefaultEventStorageProcessor.class);
		return processor.getBeanDefinition();
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Reference to custom inbound event processor */
		InboundEventProcessor("inbound-event-processor"),

		/** Reference to custom inbound event processor */
		DefaultEventStorageProcessor("default-event-storage-processor");

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