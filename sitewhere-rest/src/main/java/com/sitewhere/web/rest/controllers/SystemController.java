/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.ISiteWhereServerRuntime;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.SystemInfo;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Controller for system operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/system")
@Api(value = "system", description = "Operations related to SiteWhere CE system management.")
@DocumentedController(name = "System Information")
public class SystemController extends RestController {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Get version information about the server.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get version information")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = SystemInfo.GetVersionResponse.class, description = "getVersionResponse.md") })
    public IVersion getVersion() throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getVersion", LOGGER);
	try {
	    return SiteWhere.getServer().getVersion();
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Get runtime information about the server.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/runtime", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get server runtime information")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = SystemInfo.GetServerRuntimeResponse.class, description = "getServerStateResponse.md") })
    public ISiteWhereServerRuntime getServerRuntimeInformation() throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "getServerRuntimeInformation", LOGGER);
	try {
	    return SiteWhere.getServer().getServerRuntimeInformation(true);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }
}