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
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.device.communication.DefaultDeviceCommunication;
import com.sitewhere.server.SiteWhereServerBeans;

/**
 * Parses configuration data from SiteWhere device communication subsystem.
 * 
 * @author Derek
 */
public class DeviceCommunicationParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		BeanDefinitionBuilder communication = createBuilder();
		List<Element> children = DomUtils.getChildElements(element);
		for (Element child : children) {
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown communication subsystem element: " + child.getLocalName());
			}
			switch (type) {
			case EventSources: {
				ManagedList<?> sources = parseEventSources(child, context);
				communication.addPropertyValue("inboundEventSources", sources);
				break;
			}
			case InboundProcessingStrategy: {
				Object strategy = parseInboundProcessingStrategy(child, context);
				communication.addPropertyValue("inboundProcessingStrategy", strategy);
				break;
			}
			case OutboundProcessingStrategy: {
				Object strategy = parseOutboundProcessingStrategy(child, context);
				communication.addPropertyValue("outboundProcessingStrategy", strategy);
				break;
			}
			case Registration: {
				parseRegistration(communication, child, context);
				break;
			}
			case BatchOperations: {
				Object manager = parseBatchOperations(child, context);
				communication.addPropertyValue("batchOperationManager", manager);
				break;
			}
			case CommandRouting: {
				Object router = parseCommandRouting(child, context);
				communication.addPropertyValue("outboundCommandRouter", router);
				break;
			}
			case CommandDestinations: {
				ManagedList<?> destinations = parseCommandDestinations(child, context);
				communication.addPropertyValue("commandDestinations", destinations);
				break;
			}
			}
		}
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_COMMUNICATION,
				communication.getBeanDefinition());
		return null;
	}

	/**
	 * Creates the {@link BeanDefinitionBuilder} that will be populated with nested
	 * communication subsystem elements.
	 * 
	 * @return
	 */
	protected BeanDefinitionBuilder createBuilder() {
		return BeanDefinitionBuilder.rootBeanDefinition(DefaultDeviceCommunication.class);
	}

	/**
	 * Parse the list of event sources.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected ManagedList<?> parseEventSources(Element element, ParserContext context) {
		return new EventSourcesParser().parse(element, context);
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
	 * Parse the registration configuration.
	 * 
	 * @param dcomm
	 * @param element
	 * @param context
	 */
	protected void parseRegistration(BeanDefinitionBuilder dcomm, Element element, ParserContext context) {
		new RegistrationParser().parse(dcomm, element, context);
	}

	/**
	 * Parse the batch operations configuration.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected Object parseBatchOperations(Element element, ParserContext context) {
		return new BatchOperationsParser().parse(element, context);
	}

	/**
	 * Parse the command routing configuration.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected Object parseCommandRouting(Element element, ParserContext context) {
		return new CommandRoutingParser().parse(element, context);
	}

	/**
	 * Parse the list of command destinations.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected ManagedList<?> parseCommandDestinations(Element element, ParserContext context) {
		return new CommandDestinationsParser().parse(element, context);
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Event sources list */
		EventSources("event-sources"),

		/** Inbound processing strategy */
		@Deprecated
		InboundProcessingStrategy("inbound-processing-strategy"),

		/** Outbound processing strategy */
		@Deprecated
		OutboundProcessingStrategy("outbound-processing-strategy"),

		/** Device registration */
		Registration("registration"),

		/** Batch operations */
		BatchOperations("batch-operations"),

		/** Command routing configuration */
		CommandRouting("command-routing"),

		/** Command destinations list */
		CommandDestinations("command-destinations");

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