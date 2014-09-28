/*
 * RoutingParser.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.device.provisioning.SpecificationMappingCommandRouter;

/**
 * Parse elements related to command routing.
 * 
 * @author Derek
 */
public class CommandRoutingParser {

	/**
	 * Parse elements contained in command routing section.
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
				throw new RuntimeException("Unknown command routing element: " + child.getLocalName());
			}
			switch (type) {
			case CommandRouter: {
				return parseCommandRouterReference(child, context);
			}
			case SpecificationMappingRouter: {
				return parseSpecificationMappingRouter(child, context);
			}
			}
		}
		return null;
	}

	/**
	 * Parse a command router reference.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected RuntimeBeanReference parseCommandRouterReference(Element element, ParserContext context) {
		Attr ref = element.getAttributeNode("ref");
		if (ref != null) {
			return new RuntimeBeanReference(ref.getValue());
		}
		throw new RuntimeException("Command router reference does not have ref defined.");
	}

	/**
	 * Parse the configuration for a {@link SpecificationMappingCommandRouter}.
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected BeanDefinition parseSpecificationMappingRouter(Element element, ParserContext context) {
		BeanDefinitionBuilder router =
				BeanDefinitionBuilder.rootBeanDefinition(SpecificationMappingCommandRouter.class);
		ManagedMap<String, String> map = new ManagedMap<String, String>();
		List<Element> mappings = DomUtils.getChildElementsByTagName(element, "mapping");
		for (Element mapping : mappings) {
			Attr token = mapping.getAttributeNode("specification");
			if (token == null) {
				throw new RuntimeException("Specification mapping missing specification token.");
			}
			Attr destination = mapping.getAttributeNode("destination");
			if (destination == null) {
				throw new RuntimeException("Specification mapping missing destination id.");
			}
			map.put(token.getValue(), destination.getValue());
		}
		router.addPropertyValue("mappings", map);
		return router.getBeanDefinition();
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Command router reference */
		CommandRouter("command-router"),

		/** Specification command router */
		SpecificationMappingRouter("specification-mapping-router");

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