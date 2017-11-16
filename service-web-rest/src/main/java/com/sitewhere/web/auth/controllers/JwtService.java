/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.auth.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.web.rest.RestControllerBase;
import com.sitewhere.web.security.jwt.TokenAuthenticationFilter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Controller for security operations.
 * 
 * @author Derek Adams
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code", TokenAuthenticationFilter.JWT_HEADER })
@RequestMapping(value = "/jwt")
@Api(value = "jwt")
public class JwtService extends RestControllerBase {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Number of minutes a token remains valid */
    private static final int TOKEN_EXPIRATION_IN_MINUTES = 60;

    /** Injected reference to token management */
    @Autowired
    ITokenManagement tokenManagement;

    /**
     * Use basic authentication information to generate a JWT and return it as a
     * header in the servlet response. This is the only method that allows basic
     * authentication. All others expect the JWT in the Authorization header.
     * 
     * @param servletRequest
     * @param servletResponse
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Authenticate and receive a JWT")
    public ResponseEntity<?> jwt(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
	    throws SiteWhereException {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	if (auth == null) {
	    LOGGER.info("No credentials passsed when requesting JWT.");
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	} else {
	    IUser user = (IUser) auth.getPrincipal();
	    String jwt = getTokenManagement().generateToken(user, TOKEN_EXPIRATION_IN_MINUTES);
	    return ResponseEntity.ok().header(TokenAuthenticationFilter.JWT_HEADER, jwt).build();
	}
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