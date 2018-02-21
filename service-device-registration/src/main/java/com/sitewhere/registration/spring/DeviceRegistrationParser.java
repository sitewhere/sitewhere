/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration.spring;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.parser.IDeviceRegistrationParser.Elements;
import com.sitewhere.registration.DefaultRegistrationManager;
import com.sitewhere.spi.microservice.spring.DeviceRegistrationBeans;

/**
 * Parses elements related to device registration.
 * 
 * @author Derek
 */
public class DeviceRegistrationParser extends AbstractBeanDefinitionParser {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceRegistrationParser.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown device registration element: " + child.getLocalName());
	    }
	    switch (type) {
	    case RegistrationManager: {
		parseRegistrationManager(child, context);
		break;
	    }
	    case DefaultRegistrationManager: {
		parseDefaultRegistrationManager(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse information for the default registration manager.
     * 
     * @param element
     * @param context
     */
    protected void parseDefaultRegistrationManager(Element element, ParserContext context) {
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(DefaultRegistrationManager.class);

	Attr allowNewDevices = element.getAttributeNode("allowNewDevices");
	if (allowNewDevices != null) {
	    manager.addPropertyValue("allowNewDevices", allowNewDevices.getValue());
	}

	Attr autoAssignSite = element.getAttributeNode("autoAssignSite");
	if (autoAssignSite != null) {
	    manager.addPropertyValue("autoAssignSite", autoAssignSite.getValue());
	}

	Attr autoAssignToken = element.getAttributeNode("autoAssignToken");
	if (autoAssignToken != null) {
	    manager.addPropertyValue("autoAssignToken", autoAssignToken.getValue());
	}

	context.getRegistry().registerBeanDefinition(DeviceRegistrationBeans.BEAN_REGISTRATION_MANAGER,
		manager.getBeanDefinition());
    }

    /**
     * Parse a registration manager reference.
     * 
     * @param element
     * @param context
     */
    protected void parseRegistrationManager(Element element, ParserContext context) {
	Attr ref = element.getAttributeNode("ref");
	if (ref == null) {
	    throw new RuntimeException("Registration manager reference does not have ref defined.");
	}

	context.getRegistry().registerAlias(DeviceRegistrationBeans.BEAN_REGISTRATION_MANAGER, ref.getValue());
    }
}