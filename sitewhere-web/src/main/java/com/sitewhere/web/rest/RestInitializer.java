/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest;

import javax.servlet.Filter;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.sitewhere.web.JsonpFilter;
import com.sitewhere.web.MethodOverrideFilter;
import com.sitewhere.web.NoCacheFilter;
import com.sitewhere.web.ResponseTimerFilter;
import com.sitewhere.web.WebConfiguration;
import com.sitewhere.web.swagger.SiteWhereSwaggerConfig;

/**
 * Initializes REST servlet configuration.
 * 
 * @author Derek
 */
public class RestInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.support.AbstractDispatcherServletInitializer#
	 * getServletName()
	 */
	@Override
	protected String getServletName() {
		return "rest";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.support.
	 * AbstractAnnotationConfigDispatcherServletInitializer#getRootConfigClasses()
	 */
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.support.
	 * AbstractAnnotationConfigDispatcherServletInitializer#getServletConfigClasses()
	 */
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { WebConfiguration.RestConfiguration.class, SiteWhereSwaggerConfig.class };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.support.AbstractDispatcherServletInitializer#
	 * getServletMappings()
	 */
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/api/*" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.support.AbstractDispatcherServletInitializer#
	 * getServletFilters()
	 */
	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] {
				new MethodOverrideFilter(),
				new ResponseTimerFilter(),
				new NoCacheFilter(),
				new JsonpFilter() };
	}
}