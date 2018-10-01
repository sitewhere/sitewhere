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
import com.sitewhere.registration.DeviceRegistrationManager;
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
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(DeviceRegistrationManager.class);

	Attr allowNewDevices = element.getAttributeNode("allowNewDevices");
	if (allowNewDevices != null) {
	    manager.addPropertyValue("allowNewDevices", allowNewDevices.getValue());
	}

	Attr useDefaultDeviceType = element.getAttributeNode("useDefaultDeviceType");
	if (useDefaultDeviceType != null) {
	    manager.addPropertyValue("useDefaultDeviceType", useDefaultDeviceType.getValue());
	}

	Attr defaultDeviceTypeToken = element.getAttributeNode("defaultDeviceTypeToken");
	if (defaultDeviceTypeToken != null) {
	    manager.addPropertyValue("defaultDeviceTypeToken", defaultDeviceTypeToken.getValue());
	}

	Attr useDefaultCustomer = element.getAttributeNode("useDefaultCustomer");
	if (useDefaultCustomer != null) {
	    manager.addPropertyValue("useDefaultCustomer", useDefaultCustomer.getValue());
	}

	Attr defaultCustomerToken = element.getAttributeNode("defaultCustomerToken");
	if (defaultCustomerToken != null) {
	    manager.addPropertyValue("defaultCustomerToken", defaultCustomerToken.getValue());
	}

	Attr useDefaultArea = element.getAttributeNode("useDefaultArea");
	if (useDefaultArea != null) {
	    manager.addPropertyValue("useDefaultArea", useDefaultArea.getValue());
	}

	Attr defaultAreaToken = element.getAttributeNode("defaultAreaToken");
	if (defaultAreaToken != null) {
	    manager.addPropertyValue("defaultAreaToken", defaultAreaToken.getValue());
	}

	context.getRegistry().registerBeanDefinition(DeviceRegistrationBeans.BEAN_REGISTRATION_MANAGER,
		manager.getBeanDefinition());
    }
}