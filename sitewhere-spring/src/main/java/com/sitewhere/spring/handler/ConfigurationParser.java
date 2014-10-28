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
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

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
		List<Element> children = DomUtils.getChildElements(element);
		for (Element child : children) {
			if (!IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())) {
				continue;
			}
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown configuration element: " + child.getLocalName());
			}
			switch (type) {
			case Datastore: {
				new DatastoreParser().parse(child, context);
				break;
			}
			case InboundProcessingChain: {
				new InboundProcessingChainParser().parse(child, context);
				break;
			}
			case OutboundProcessingChain: {
				new OutboundProcessingChainParser().parse(child, context);
				break;
			}
			case Provisioning: {
				new ProvisioningParser().parse(child, context);
				break;
			}
			case AssetManagement: {
				new AssetManagementParser().parse(child, context);
				break;
			}
			}
		}
		return null;
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Datastore */
		Datastore("datastore"),

		/** Inbound processing chain */
		InboundProcessingChain("inbound-processing-chain"),

		/** Outbound processing chain */
		OutboundProcessingChain("outbound-processing-chain"),

		/** Provisioning */
		Provisioning("provisioning"),

		/** Asset management */
		AssetManagement("asset-management");

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