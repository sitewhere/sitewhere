/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.swagger;

import javax.servlet.ServletContext;

import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider;

/**
 * Determines path used to reference SiteWhere APIs when creating Swagger data.
 * 
 * @author Derek
 */
public class SiteWherePathProvider extends RelativeSwaggerPathProvider {

    public SiteWherePathProvider(ServletContext servletContext) {
	super(servletContext);
    }

    @Override
    protected String applicationPath() {
	return super.applicationPath() + "/api";
    }
}