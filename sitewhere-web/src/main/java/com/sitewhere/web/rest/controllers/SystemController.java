/*
 * SystemController.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.version.IVersion;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Controller for system operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/system")
@Api(value = "", description = "Operations related to SiteWhere system management.")
public class SystemController extends SiteWhereController {

	@RequestMapping(value = "/version", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get version information")
	public IVersion getVersion() throws SiteWhereException {
		return SiteWhereServer.getInstance().getVersion();
	}
}