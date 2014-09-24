/*
 * ProvisioningParser.java 
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
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sitewhere.device.provisioning.DefaultDeviceProvisioning;
import com.sitewhere.server.SiteWhereServerBeans;

/**
 * Parses configuration data from SiteWhere provisioning section.
 * 
 * @author Derek
 */
public class ProvisioningParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		BeanDefinitionBuilder provisioning =
				BeanDefinitionBuilder.rootBeanDefinition(DefaultDeviceProvisioning.class);
		List<Element> children = DomUtils.getChildElements(element);
		for (Element child : children) {
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown provisioning element: " + child.getLocalName());
			}
			switch (type) {
			case EventSources: {
				ManagedList<?> sources = parseEventSources(child, context);
				provisioning.addPropertyValue("inboundEventSources", sources);
				break;
			}
			case Registration: {
				Object manager = parseRegistration(child, context);
				provisioning.addPropertyValue("registrationManager", manager);
				break;
			}
			case CommandRouting: {
				Object router = parseCommandRouting(child, context);
				provisioning.addPropertyValue("outboundCommandRouter", router);
				break;
			}
			case CommandAgents: {
				ManagedList<?> agents = parseCommandAgents(child, context);
				provisioning.addPropertyValue("outboundCommandAgents", agents);
				break;
			}
			}
		}
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_DEVICE_PROVISIONING,
				provisioning.getBeanDefinition());
		return null;
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
	 * Parse the registration configuration.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected Object parseRegistration(Element element, ParserContext context) {
		return new RegistrationParser().parse(element, context);
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
	 * Parse the list of command agents.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected ManagedList<?> parseCommandAgents(Element element, ParserContext context) {
		return new CommandAgentsParser().parse(element, context);
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Event sources list */
		EventSources("event-sources"),

		/** Device registration */
		Registration("registration"),

		/** Command routing configuration */
		CommandRouting("command-routing"),

		/** Command agents list */
		CommandAgents("command-agents");

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