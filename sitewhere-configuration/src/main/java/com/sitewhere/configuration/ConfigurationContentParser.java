/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration;

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

import com.sitewhere.configuration.content.AttributeContent;
import com.sitewhere.configuration.content.ElementContent;
import com.sitewhere.core.DataUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;

/**
 * Used to parse SiteWhere XML configuration into JSON representation.
 * 
 * @author Derek
 */
public class ConfigurationContentParser {

    /**
     * Parse a byte[] containing SiteWhere XML configuration for JSON
     * representation.
     * 
     * @param config
     * @param configurationModel
     * @return
     * @throws SiteWhereException
     */
    public static ElementContent parse(byte[] config, IConfigurationModel configurationModel)
	    throws SiteWhereException {
	try {
	    DocumentBuilderFactory factory = DataUtils.getDocumentBuilderFactory();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(new InputSource(new ByteArrayInputStream(config)));
	    Element element = document.getDocumentElement();
	    ElementContent content = parse(element, configurationModel);
	    return content;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to parse configuration content.", e);
	}
    }

    /**
     * Recursively parse the XML document.
     * 
     * @param element
     * @param configurationModel
     * @return
     * @throws SiteWhereException
     */
    protected static ElementContent parse(Element element, IConfigurationModel configurationModel)
	    throws SiteWhereException {
	ElementContent econ = new ElementContent();
	if (!configurationModel.getDefaultXmlNamespace().equals(element.getNamespaceURI())) {
	    econ.setNamespace(element.getNamespaceURI());
	}
	econ.setName(element.getLocalName());
	NamedNodeMap attrs = element.getAttributes();
	List<AttributeContent> acons = new ArrayList<AttributeContent>();
	for (int i = 0; i < attrs.getLength(); i++) {
	    Node attr = attrs.item(i);
	    AttributeContent acon = new AttributeContent();
	    if (!configurationModel.getDefaultXmlNamespace().equals(attr.getNamespaceURI())) {
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
	    econs.add(parse(child, configurationModel));
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
     * @param configurationModel
     * @return
     * @throws SiteWhereException
     */
    public static Document buildXml(ElementContent content, IConfigurationModel configurationModel)
	    throws SiteWhereException {
	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.newDocument();
	    buildXml(document, content, configurationModel);
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
     * @param configurationModel
     * @throws SiteWhereException
     */
    protected static void buildXml(Document document, ElementContent content, IConfigurationModel configurationModel)
	    throws SiteWhereException {
	Element created = document.createElementNS(content.getNamespace(), content.getName());
	document.appendChild(created);
	if (content.getAttributes() != null) {
	    for (AttributeContent attribute : content.getAttributes()) {
		if (attribute.getName().equals("schemaLocation")) {
		    created.setAttributeNS(attribute.getNamespace(), "xsi:schemaLocation", attribute.getValue());
		}
	    }
	}
	if (content.getChildren() != null) {
	    for (ElementContent childContent : content.getChildren()) {
		buildXml(document, document.getDocumentElement(), childContent, configurationModel);
	    }
	}
    }

    /**
     * Recursively create DOM from JSON model.
     * 
     * @param document
     * @param parent
     * @param content
     * @param configurationModel
     * @throws SiteWhereException
     */
    protected static void buildXml(Document document, Element parent, ElementContent content,
	    IConfigurationModel configurationModel) throws SiteWhereException {
	String namespace = (content.getNamespace() != null) ? content.getNamespace()
		: configurationModel.getDefaultXmlNamespace();
	String prefix = getModelNamespacePrefix(namespace) + ":";
	Element created = document.createElementNS(namespace, prefix + content.getName());
	parent.appendChild(created);
	if (content.getAttributes() != null) {
	    for (AttributeContent attribute : content.getAttributes()) {
		if (!"http://www.w3.org/2000/xmlns/".equals(attribute.getNamespace())) {
		    created.setAttributeNS(attribute.getNamespace(), attribute.getName(), attribute.getValue());
		}
	    }
	}
	if (content.getChildren() != null) {
	    for (ElementContent childContent : content.getChildren()) {
		buildXml(document, created, childContent, configurationModel);
	    }
	}
    }

    /**
     * Generates a prefix based on the last entry in the namespace URL.
     * 
     * @param model
     * @return
     */
    protected static String getModelNamespacePrefix(String namespace) {
	String[] urlParts = namespace.split("/");
	String name = urlParts[urlParts.length - 1];
	String[] nameParts = name.split("-");
	String id = "";
	for (String part : nameParts) {
	    id += part.substring(0, 3);
	}
	return id;
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