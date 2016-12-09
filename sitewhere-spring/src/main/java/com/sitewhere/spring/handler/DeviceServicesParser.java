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
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.device.communication.RegistrationManager;
import com.sitewhere.device.communication.symbology.QrCodeSymbolGenerator;
import com.sitewhere.device.communication.symbology.SymbolGeneratorManager;
import com.sitewhere.device.presence.DevicePresenceManager;

/**
 * Parse elements related to device registration.
 * 
 * @author Derek
 */
public class DeviceServicesParser {

    /**
     * Parse elements in the device service section.
     * 
     * @param dcomm
     * @param element
     * @param context
     * @return
     */
    protected void parse(BeanDefinitionBuilder services, Element element, ParserContext context) {
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown registration element: " + child.getLocalName());
	    }
	    switch (type) {
	    case DefaultRegistrationManager: {
		services.addPropertyValue("registrationManager", parseDefaultRegistrationManager(child, context));
		break;
	    }
	    case RegistrationManager: {
		services.addPropertyValue("registrationManager", parseRegistrationManager(child, context));
		break;
	    }
	    case SymbolGeneratorManager: {
		services.addPropertyValue("symbolGeneratorManager", parseSymbolGeneratorManager(child, context));
		break;
	    }
	    case DefaultPresenceManager: {
		services.addPropertyValue("devicePresenceManager", parseDefaultPresenceManager(child, context));
		break;
	    }
	    }
	}
    }

    /**
     * Parse information for the default registration manager.
     * 
     * @param element
     * @param context
     * @return
     */
    protected BeanDefinition parseDefaultRegistrationManager(Element element, ParserContext context) {
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(RegistrationManager.class);

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

	return manager.getBeanDefinition();
    }

    /**
     * Parse a registration manager reference.
     * 
     * @param element
     * @param context
     * @return
     */
    protected RuntimeBeanReference parseRegistrationManager(Element element, ParserContext context) {
	Attr ref = element.getAttributeNode("ref");
	if (ref != null) {
	    return new RuntimeBeanReference(ref.getValue());
	}
	throw new RuntimeException("Registration manager reference does not have ref defined.");
    }

    /**
     * Parse information for the symbol generator manager.
     * 
     * @param element
     * @param context
     * @return
     */
    protected BeanDefinition parseSymbolGeneratorManager(Element element, ParserContext context) {
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(SymbolGeneratorManager.class);

	ManagedList<Object> result = new ManagedList<Object>();
	List<Element> children = DomUtils.getChildElements(element);
	for (Element child : children) {
	    SymbolGenerators type = SymbolGenerators.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown symbol generator element: " + child.getLocalName());
	    }
	    switch (type) {
	    case QRCodeSymbolGenerator: {
		result.add(parseQrCodeSymbolGenerator(child, context));
		break;
	    }
	    }
	}
	manager.addPropertyValue("symbolGenerators", result);

	return manager.getBeanDefinition();
    }

    /**
     * Parse QR-Code symbol generator.
     * 
     * @param element
     * @param context
     * @return
     */
    protected BeanDefinition parseQrCodeSymbolGenerator(Element element, ParserContext context) {
	BeanDefinitionBuilder generator = BeanDefinitionBuilder.rootBeanDefinition(QrCodeSymbolGenerator.class);

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

    /**
     * Parse information for the default presence manager.
     * 
     * @param element
     * @param context
     * @return
     */
    protected BeanDefinition parseDefaultPresenceManager(Element element, ParserContext context) {
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(DevicePresenceManager.class);

	Attr checkInterval = element.getAttributeNode("checkInterval");
	if (checkInterval != null) {
	    manager.addPropertyValue("presenceCheckInterval", checkInterval.getValue());
	}

	Attr presenceMissingInterval = element.getAttributeNode("presenceMissingInterval");
	if (presenceMissingInterval != null) {
	    manager.addPropertyValue("presenceMissingInterval", presenceMissingInterval.getValue());
	}

	return manager.getBeanDefinition();
    }

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Default registration manager */
	DefaultRegistrationManager("default-registration-manager"),

	/** Registration manager reference */
	RegistrationManager("registration-manager"),

	/** Symbol generator manager reference */
	SymbolGeneratorManager("symbol-generator-manager"),

	/** Default presence manager */
	DefaultPresenceManager("default-presence-manager");

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

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum SymbolGenerators {

	/** QR-Code symbol generator */
	QRCodeSymbolGenerator("qr-code-symbol-generator");

	/** Event code */
	private String localName;

	private SymbolGenerators(String localName) {
	    this.localName = localName;
	}

	public static SymbolGenerators getByLocalName(String localName) {
	    for (SymbolGenerators value : SymbolGenerators.values()) {
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