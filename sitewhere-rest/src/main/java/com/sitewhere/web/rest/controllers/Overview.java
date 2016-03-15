/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Used purely for global documentation.
 * 
 * @author Derek
 */
@RequestMapping(value = "/overview")
@Api(value = "overview", description = "REST Services Overview")
@DocumentedController(name = "Overview", global = true)
public class Overview {

	@RequestMapping(value = "/overview/calling", method = RequestMethod.GET)
	@ApiOperation(value = "Calling SiteWhere REST Services")
	@Documented
	public void calling() {
	}

	@RequestMapping(value = "/overview/paging", method = RequestMethod.GET)
	@ApiOperation(value = "Paged Results")
	@Documented
	public void paging() {
	}
}