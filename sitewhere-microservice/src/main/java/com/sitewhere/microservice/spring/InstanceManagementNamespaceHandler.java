/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.sitewhere.spi.microservice.IMicroserviceIdentifiers;

/**
 * Registers parsers for elements in the instance management configuration
 * schema.
 * 
 * @author Derek
 */
public class InstanceManagementNamespaceHandler extends NamespaceHandlerSupport {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    @Override
    public void init() {
	registerBeanDefinitionParser(IMicroserviceIdentifiers.INSTANCE_MANAGEMENT, new InstanceManagementParser());
    }
}