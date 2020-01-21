/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.auth.controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.web.ISiteWhereWebConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Controller for security operations.
 */
@Path("/authapi/jwt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "jwt")
public class JwtService {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(JwtService.class);

    /** Injected reference to token management */
    @Inject
    ITokenManagement tokenManagement;

    /**
     * Use basic authentication information to generate a JWT and return it as a
     * header in the servlet response. This is the only method that allows basic
     * authentication. All others expect the JWT in the Authorization header.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @ApiOperation(value = "Authenticate and receive a JWT")
    public Response jwt() throws SiteWhereException {
	SiteWhereAuthentication auth = UserContext.getCurrentUser();
	if (auth != null) {
	    return Response.ok().header(ISiteWhereWebConstants.HEADER_JWT, auth.getJwt()).build();
	}
	LOGGER.warn("No user context found for current thread.");
	return Response.status(Status.UNAUTHORIZED).build();
    }

    /**
     * Get {@link ITokenManagement} implementation.
     * 
     * @return
     */
    protected ITokenManagement getTokenManagement() {
	return tokenManagement;
    }
}