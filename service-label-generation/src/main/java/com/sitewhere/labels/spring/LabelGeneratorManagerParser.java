/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.spring;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.parser.ILabelGenerationParser.LabelGeneratorElements;
import com.sitewhere.labels.symbology.LabelGeneratorManager;
import com.sitewhere.labels.symbology.QrCodeGenerator;
import com.sitewhere.spi.microservice.spring.LabelGenerationBeans;

/**
 * Spring parser for label generator manager.
 * 
 * @author Derek
 */
public class LabelGeneratorManagerParser extends AbstractBeanDefinitionParser {

    /*
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal(org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	ManagedList<Object> generators = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    LabelGeneratorElements type = LabelGeneratorElements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown label generator manager element: " + child.getLocalName());
	    }
	    switch (type) {
	    case QrCodeLabelGenerator: {
		generators.add(parseQrCodeLabelGenerator(child, context));
		break;
	    }
	    }
	}

	// Build event label generator manager and inject the list of beans.
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(LabelGeneratorManager.class);
	manager.addPropertyValue("labelGenerators", generators);
	context.getRegistry().registerBeanDefinition(LabelGenerationBeans.BEAN_LABEL_GENERATOR_MANAGER,
		manager.getBeanDefinition());

	return null;
    }

    /**
     * Parse QR-Code label generator.
     * 
     * @param element
     * @param context
     * @return
     */
    protected BeanDefinition parseQrCodeLabelGenerator(Element element, ParserContext context) {
	BeanDefinitionBuilder generator = BeanDefinitionBuilder.rootBeanDefinition(QrCodeGenerator.class);

	Attr id = element.getAttributeNode("id");
	if (id == null) {
	    throw new RuntimeException("QR code symbol generator id is missing.");
	}
	generator.addPropertyValue("id", id.getValue());

	Attr name = element.getAttributeNode("name");
	if (name == null) {
	    throw new RuntimeException("QR code symbol generator name is missing.");
	}
	generator.addPropertyValue("name", name.getValue());

	Attr width = element.getAttributeNode("width");
	if (width != null) {
	    generator.addPropertyValue("width", width.getValue());
	}

	Attr height = element.getAttributeNode("height");
	if (height != null) {
	    generator.addPropertyValue("height", height.getValue());
	}

	Attr backgroundColor = element.getAttributeNode("backgroundColor");
	if (backgroundColor != null) {
	    generator.addPropertyValue("backgroundColor", backgroundColor.getValue());
	}

	Attr foregroundColor = element.getAttributeNode("foregroundColor");
	if (foregroundColor != null) {
	    generator.addPropertyValue("foregroundColor", foregroundColor.getValue());
	}

	return generator.getBeanDefinition();
    }
}
