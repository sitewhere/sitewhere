/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Implementation of {@link NamespaceHandler} for supporting SiteWhere configuration
 * elements.
 * 
 * @author Derek
 */
public class SiteWhereNamespaceHandler extends NamespaceHandlerSupport {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
	 */
	@Override
	public void init() {
		registerBeanDefinitionParser(IConfigurationElements.CONFIGURATION, new ConfigurationParser());
	}
}