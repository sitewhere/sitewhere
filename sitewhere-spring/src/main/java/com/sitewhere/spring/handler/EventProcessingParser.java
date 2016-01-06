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
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.device.event.EventProcessing;
import com.sitewhere.server.SiteWhereServerBeans;

/**
 * Parses configuration data from SiteWhere event processing subsystem.
 * 
 * @author Derek
 */
public class EventProcessingParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		BeanDefinitionBuilder processing = createBuilder();
		List<Element> children = DomUtils.getChildElements(element);
		for (Element child : children) {
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown event processing element: " + child.getLocalName());
			}
			switch (type) {
			case InboundProcessingStrategy: {
				Object strategy = parseInboundProcessingStrategy(child, context);
				processing.addPropertyValue("inboundProcessingStrategy", strategy);
				break;
			}
			case OutboundProcessingStrategy: {
				Object strategy = parseOutboundProcessingStrategy(child, context);
				processing.addPropertyValue("outboundProcessingStrategy", strategy);
				break;
			}
			case InboundProcessingChain: {
				Object ichain = new InboundProcessingChainParser().parse(child, context);
				processing.addPropertyValue("inboundEventProcessorChain", ichain);
				break;
			}
			case OutboundProcessingChain: {
				Object ochain = new OutboundProcessingChainParser().parse(child, context);
				processing.addPropertyValue("outboundEventProcessorChain", ochain);
				break;
			}
			}
		}
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_EVENT_PROCESSING,
				processing.getBeanDefinition());
		return null;
	}

	/**
	 * Creates the {@link BeanDefinitionBuilder} that will be populated with nested
	 * communication subsystem elements.
	 * 
	 * @return
	 */
	protected BeanDefinitionBuilder createBuilder() {
		return BeanDefinitionBuilder.rootBeanDefinition(EventProcessing.class);
	}

	/**
	 * Parse the inbound processing strategy configuration.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected Object parseInboundProcessingStrategy(Element element, ParserContext context) {
		return new InboundProcessingStrategyParser().parse(element, context);
	}

	/**
	 * Parse the outbound processing strategy configuration.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected Object parseOutboundProcessingStrategy(Element element, ParserContext context) {
		return new OutboundProcessingStrategyParser().parse(element, context);
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Inbound processing strategy */
		InboundProcessingStrategy("inbound-processing-strategy"),

		/** Outbound processing strategy */
		OutboundProcessingStrategy("outbound-processing-strategy"),

		/** Inbound processing chain */
		InboundProcessingChain("inbound-processing-chain"),

		/** Outbound processing chain */
		OutboundProcessingChain("outbound-processing-chain");

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