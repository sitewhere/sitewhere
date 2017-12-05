/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.spring;

import java.util.List;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.asset.modules.AssetModuleManager;
import com.sitewhere.asset.modules.wso2.scim.Wso2ScimAssetModule;
import com.sitewhere.configuration.parser.IAssetManagementParser.IAssetModulesParser.Elements;
import com.sitewhere.spi.microservice.spring.AssetManagementBeans;

/**
 * Parses asset module Spring beans.
 * 
 * @author Derek
 */
public class AssetModulesParser extends AbstractBeanDefinitionParser {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(AssetModuleManager.class);
	List<Element> children = DomUtils.getChildElements(element);
	List<Object> modules = new ManagedList<Object>();
	for (Element child : children) {
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown asset management element: " + child.getLocalName());
	    }
	    switch (type) {
	    case AssetModuleReference: {
		modules.add(parseAssetModuleReference(child, context));
		break;
	    }
	    case Wso2IdentityAssetModule: {
		modules.add(parseWso2IdentityAssetModule(child, context));
		break;
	    }
	    }
	}
	manager.addPropertyValue("modules", modules);
	context.getRegistry().registerBeanDefinition(AssetManagementBeans.BEAN_ASSET_MODULE_MANAGER,
		manager.getBeanDefinition());
	return null;
    }

    /**
     * Parse an asset module reference.
     * 
     * @param element
     * @param context
     * @return
     */
    protected RuntimeBeanReference parseAssetModuleReference(Element element, ParserContext context) {
	Attr ref = element.getAttributeNode("ref");
	if (ref != null) {
	    return new RuntimeBeanReference(ref.getValue());
	}
	throw new RuntimeException("Asset module reference does not have ref defined.");
    }

    /**
     * Parse a fileystem device asset module configuration.
     * 
     * @param element
     * @param context
     * @return
     */
    protected AbstractBeanDefinition parseWso2IdentityAssetModule(Element element, ParserContext context) {
	BeanDefinitionBuilder module = BeanDefinitionBuilder.rootBeanDefinition(Wso2ScimAssetModule.class);

	Attr moduleId = element.getAttributeNode("moduleId");
	if (moduleId != null) {
	    module.addPropertyValue("moduleId", moduleId.getValue());
	}

	Attr scimUsersUrl = element.getAttributeNode("scimUsersUrl");
	if (scimUsersUrl != null) {
	    module.addPropertyValue("scimUsersUrl", scimUsersUrl.getValue());
	}

	Attr username = element.getAttributeNode("username");
	if (username != null) {
	    module.addPropertyValue("username", username.getValue());
	}

	Attr password = element.getAttributeNode("password");
	if (password != null) {
	    module.addPropertyValue("password", password.getValue());
	}

	Attr ignoreBadCertificate = element.getAttributeNode("ignoreBadCertificate");
	if (ignoreBadCertificate != null) {
	    module.addPropertyValue("ignoreBadCertificate", ignoreBadCertificate.getValue());
	}

	return module.getBeanDefinition();
    }
}
