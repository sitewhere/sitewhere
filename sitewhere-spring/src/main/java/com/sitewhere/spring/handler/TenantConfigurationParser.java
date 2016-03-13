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
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Parses the top-level element for SiteWhere tenant Spring configuration.
 * 
 * @author Derek
 */
public class TenantConfigurationParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		List<Element> children = DomUtils.getChildElements(element);
		for (Element child : children) {
			if (!IConfigurationElements.SITEWHERE_CE_TENANT_NS.equals(child.getNamespaceURI())) {
				NamespaceHandler nested =
						context.getReaderContext().getNamespaceHandlerResolver().resolve(
								child.getNamespaceURI());
				if (nested != null) {
					nested.parse(child, context);
					continue;
				} else {
					throw new RuntimeException(
							"Invalid nested element found in 'tenant-configuration' section: "
									+ child.toString());
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
			case TenantDatastore: {
				parseTenantDatastore(child, context);
				break;
			}
			case Provisioning: {
				parseDeviceCommunication(child, context);
				break;
			}
			case DeviceCommunication: {
				parseDeviceCommunication(child, context);
				break;
			}
			case EventProcessing: {
				parseEventProcessing(child, context);
				break;
			}
			case AssetManagement: {
				parseAssetManagement(child, context);
				break;
			}
			case SearchProviders: {
				parseSearchProviders(child, context);
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
	 * Parse tenant datastore elements.
	 * 
	 * @param child
	 * @param context
	 */
	protected void parseTenantDatastore(Element child, ParserContext context) {
		new TenantDatastoreParser().parse(child, context);
	}

	/**
	 * Parse device communication elements.
	 * 
	 * @param child
	 * @param context
	 */
	protected void parseDeviceCommunication(Element child, ParserContext context) {
		new DeviceCommunicationParser().parse(child, context);
	}

	/**
	 * Parse event processing elements.
	 * 
	 * @param child
	 * @param context
	 */
	protected void parseEventProcessing(Element child, ParserContext context) {
		new EventProcessingParser().parse(child, context);
	}

	/**
	 * Parse asset management elements.
	 * 
	 * @param child
	 * @param context
	 */
	protected void parseAssetManagement(Element child, ParserContext context) {
		new AssetManagementParser().parse(child, context);
	}

	/**
	 * Parse search provider elements.
	 * 
	 * @param child
	 * @param context
	 */
	protected void parseSearchProviders(Element child, ParserContext context) {
		new SearchProvidersParser().parse(child, context);
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Globals */
		Globals("globals"),

		/** Tenant datastore */
		TenantDatastore("tenant-datastore"),

		/** Provisioning (DEPRECATED) */
		@Deprecated Provisioning("provisioning"),

		/** Device Communication Subsystem */
		DeviceCommunication("device-communication"),

		/** Event processing Subsystem */
		EventProcessing("event-processing"),

		/** Asset management */
		AssetManagement("asset-management"),

		/** Search providers */
		SearchProviders("search-providers");

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