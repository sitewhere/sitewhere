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
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.hazelcast.SiteWhereHazelcastConfiguration;

public class GlobalsParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		List<Element> dsChildren = DomUtils.getChildElements(element);
		for (Element child : dsChildren) {
			if (!IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())) {
				NamespaceHandler nested =
						context.getReaderContext().getNamespaceHandlerResolver().resolve(
								child.getNamespaceURI());
				if (nested != null) {
					nested.parse(child, context);
					continue;
				} else {
					throw new RuntimeException("Invalid nested element found in 'globals' section: "
							+ child.toString());
				}
			}
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown globals element: " + child.getLocalName());
			}
			switch (type) {
			case HazelcastConfiguration: {
				parseHazelcastConfiguration(child, context);
				break;
			}
			}
		}
		return null;
	}

	/**
	 * Parse the global Hazelcast configuration.
	 * 
	 * @param element
	 * @param context
	 */
	protected void parseHazelcastConfiguration(Element element, ParserContext context) {
		BeanDefinitionBuilder config =
				BeanDefinitionBuilder.rootBeanDefinition(SiteWhereHazelcastConfiguration.class);

		Attr configFileLocation = element.getAttributeNode("configFileLocation");
		if (configFileLocation == null) {
			throw new RuntimeException("Hazelcast configuration missing 'configFileLocation' attribute.");
		}
		config.addPropertyValue("configFileLocation", configFileLocation.getValue());

		context.getRegistry().registerBeanDefinition(
				SiteWhereHazelcastConfiguration.HAZELCAST_CONFIGURATION_BEAN, config.getBeanDefinition());
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Global Hazelcast configuration */
		HazelcastConfiguration("hazelcast-configuration");

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
