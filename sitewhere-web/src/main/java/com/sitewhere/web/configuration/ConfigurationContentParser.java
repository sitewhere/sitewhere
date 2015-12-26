/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
	 * Parse a byte[] containing SiteWhere XML configuration for JSON representation.
	 * 
	 * @param config
	 * @return
	 * @throws SiteWhereException
	 */
	public static ElementContent parse(byte[] config) throws SiteWhereException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new ByteArrayInputStream(config)));
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

	/**
	 * Build an XML configuration file from the JSON content.
	 * 
	 * @param content
	 * @return
	 * @throws SiteWhereException
	 */
	public static Document buildXml(ElementContent content) throws SiteWhereException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			buildXml(document, content);
			return document;
		} catch (Exception e) {
			throw new SiteWhereException("Unable to parse configuration content.", e);
		}
	}

	/**
	 * Create top-level element, then pass off to recursive function.
	 * 
	 * @param document
	 * @param content
	 * @throws SiteWhereException
	 */
	protected static void buildXml(Document document, ElementContent content) throws SiteWhereException {
		Element created = document.createElementNS(content.getNamespace(), content.getName());
		document.appendChild(created);
		if (content.getChildren() != null) {
			for (ElementContent childContent : content.getChildren()) {
				buildXml(document, document.getDocumentElement(), childContent);
			}
		}
	}

	/**
	 * Recursively create DOM from JSON model.
	 * 
	 * @param document
	 * @param parent
	 * @param content
	 * @throws SiteWhereException
	 */
	protected static void buildXml(Document document, Element parent, ElementContent content)
			throws SiteWhereException {
		Element created = document.createElementNS(content.getNamespace(), content.getName());
		parent.appendChild(created);
		if (content.getAttributes() != null) {
			for (AttributeContent attribute : content.getAttributes()) {
				created.setAttributeNS(attribute.getNamespace(), attribute.getName(), attribute.getValue());
			}
		}
		if (content.getChildren() != null) {
			for (ElementContent childContent : content.getChildren()) {
				buildXml(document, created, childContent);
			}
		}
	}

	/**
	 * Format XML.
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static String format(Document xml) throws SiteWhereException {
		try {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			Writer out = new StringWriter();
			tf.transform(new DOMSource(xml), new StreamResult(out));
			return out.toString();
		} catch (Exception e) {
			throw new SiteWhereException("Unable to format XML document.", e);
		}
	}
}