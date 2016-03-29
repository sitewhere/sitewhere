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

import com.sitewhere.server.SiteWhereServerBeans;
import com.sitewhere.version.VersionChecker;

/**
 * Parses the top-level element for SiteWhere Spring configuration.
 * 
 * @author Derek
 */
public class ConfigurationParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		// Add version checker bean if enabled.
		Attr enableVersionCheck = element.getAttributeNode("enableVersionCheck");
		if ((enableVersionCheck != null) && ("true".equals(enableVersionCheck.getValue()))) {
			BeanDefinitionBuilder vc = BeanDefinitionBuilder.rootBeanDefinition(VersionChecker.class);
			context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_VERSION_CHECK,
					vc.getBeanDefinition());
		}

		List<Element> children = DomUtils.getChildElements(element);
		for (Element child : children) {
			if (!IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())) {
				NamespaceHandler nested =
						context.getReaderContext().getNamespaceHandlerResolver().resolve(
								child.getNamespaceURI());
				if (nested != null) {
					nested.parse(child, context);
					continue;
				} else {
					throw new RuntimeException(
							"Invalid nested element found in 'configuration' section: " + child.toString());
				}
			}
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown configuration element: " + child.getLocalName());
			}
			switch (type) {
			case Globals: {
				parseGlobals(child, context);
				break;
			}
			case Datastore: {
				parseDatastore(child, context);
				break;
			}
			}
		}
		return null;
	}

	/**
	 * Parse global elements.
	 * 
	 * @param child
	 * @param context
	 */
	protected void parseGlobals(Element child, ParserContext context) {
		new GlobalsParser().parse(child, context);
	}

	/**
	 * Parse datastore elements.
	 * 
	 * @param child
	 * @param context
	 */
	protected void parseDatastore(Element child, ParserContext context) {
		new DatastoreParser().parse(child, context);
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Globals */
		Globals("globals"),

		/** Datastore */
		Datastore("datastore");

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