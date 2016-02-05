/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import com.sitewhere.web.mvc.MvcSecurityConfiguration;
import com.sitewhere.web.rest.RestSecurityConfiguration;

/**
 * Web application initializer that bootstraps Spring Security.
 * 
 * @author Derek
 */
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	public SecurityInitializer() {
		super(MvcSecurityConfiguration.class, RestSecurityConfiguration.class);
	}
}