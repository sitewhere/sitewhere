/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Implementation of {@link NamespaceHandler} for supporting SiteWhere tenant
 * configuration elements.
 * 
 * @author Derek
 */
public class SiteWhereTenantNamespaceHandler extends NamespaceHandlerSupport {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    @Override
    public void init() {
	registerBeanDefinitionParser(IConfigurationElements.TENANT_CONFIGURATION, getTenantBeanDefinitionParser());
    }

    /**
     * Create {@link BeanDefinitionParser} from class specified by server.
     * 
     * @return
     */
    protected BeanDefinitionParser getTenantBeanDefinitionParser() {
	try {
	    Class<?> clazz = Class.forName(SiteWhere.getServer().getTenantConfigurationParserClassname());
	    return (BeanDefinitionParser) clazz.newInstance();
	} catch (ClassNotFoundException e) {
	    throw new RuntimeException("Unable to find tenant configuration parser class.");
	} catch (InstantiationException e) {
	    throw new RuntimeException("Could not create tenant configuration parser class.");
	} catch (IllegalAccessException e) {
	    throw new RuntimeException("Could not access tenant configuration parser class.");
	}
    }
}