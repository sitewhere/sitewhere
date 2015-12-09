/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spring.handler.IConfigurationElements;
import com.sitewhere.web.configuration.content.AttributeContent;
import com.sitewhere.web.configuration.content.ElementContent;

/**
 * Used to parse SiteWhere XML configuration into JSON representation.
 * 
 * @author Derek
 */
public class ConfigurationContentParser {

	/**
	 * Parse a String containing SiteWhere XML configuration for JSON representation.
	 * 
	 * @param config
	 * @return
	 * @throws SiteWhereException
	 */
	public static ElementContent parse(String config) throws SiteWhereException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(config)));
			Element element = document.getDocumentElement();
			return parse(element);
		} catch (Exception e) {
			throw new SiteWhereException("Unable to parse configuration content.", e);
		}
	}

	/**
	 * Recursively parse the XML document.
	 * 
	 * @param element
	 * @return
	 * @throws SiteWhereException
	 */
	protected static ElementContent parse(Element element) throws SiteWhereException {
		ElementContent econ = new ElementContent();
		if (!IConfigurationElements.SITEWHERE_CE_TENANT_NS.equals(element.getNamespaceURI())) {
			econ.setNamespace(element.getNamespaceURI());
		}
		econ.setName(element.getLocalName());
		NamedNodeMap attrs = element.getAttributes();
		List<AttributeContent> acons = new ArrayList<AttributeContent>();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			AttributeContent acon = new AttributeContent();
			if (!IConfigurationElements.SITEWHERE_CE_TENANT_NS.equals(attr.getNamespaceURI())) {
				acon.setNamespace(attr.getNamespaceURI());
			}
			acon.setName(attr.getLocalName());
			acon.setValue(attr.getNodeValue());
			acons.add(acon);
		}
		if (!acons.isEmpty()) {
			econ.setAttributes(acons);
		}
		List<Element> children = DomUtils.getChildElements(element);
		List<ElementContent> econs = new ArrayList<ElementContent>();
		for (Element child : children) {
			econs.add(parse(child));
		}
		if (!econs.isEmpty()) {
			econ.setChildren(econs);
		}
		return econ;
	}
}