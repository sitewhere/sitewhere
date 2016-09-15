/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
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
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.server.batch.BatchOperationManager;

/**
 * Parse elements related to batch operations.
 * 
 * @author Derek
 */
public class BatchOperationsParser {

    /**
     * Parse elements in the device registration section.
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
		throw new RuntimeException("Unknown registration element: " + child.getLocalName());
	    }
	    switch (type) {
	    case DefaultBatchOperationManager: {
		return parseDefaultBatchOperationManager(child, context);
	    }
	    case BatchOperationManager: {
		return parseBatchOperationManager(child, context);
	    }
	    }
	}
	return null;
    }

    /**
     * Parse information for the default batch operation manager.
     * 
     * @param element
     * @param context
     * @return
     */
    protected BeanDefinition parseDefaultBatchOperationManager(Element element, ParserContext context) {
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(BatchOperationManager.class);

	Attr throttleDelayMs = element.getAttributeNode("throttleDelayMs");
	if (throttleDelayMs != null) {
	    manager.addPropertyValue("throttleDelayMs", throttleDelayMs.getValue());
	}

	return manager.getBeanDefinition();
    }

    /**
     * Parse a batch operation manager reference.
     * 
     * @param element
     * @param context
     * @return
     */
    protected RuntimeBeanReference parseBatchOperationManager(Element element, ParserContext context) {
	Attr ref = element.getAttributeNode("ref");
	if (ref != null) {
	    return new RuntimeBeanReference(ref.getValue());
	}
	throw new RuntimeException("Batch operation manager reference does not have ref defined.");
    }

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Default batch operation manager */
	DefaultBatchOperationManager("default-batch-operation-manager"),

	/** Batch operation manager reference */
	BatchOperationManager("batch-operation-manager");

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