/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.security.basic;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Hack so that only the authentication URL uses basic authentication.
 * Everything else requires JWT.
 * 
 * @author Derek
 */
public class LimitedBasicAuthFilter extends BasicAuthenticationFilter {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** URL that provides authentication */
    private static final String AUTHENTICATION_PATH = "/authapi/jwt";

    public LimitedBasicAuthFilter(AuthenticationManager authenticationManager) {
	super(authenticationManager);
    }

    public LimitedBasicAuthFilter(AuthenticationManager authenticationManager,
	    AuthenticationEntryPoint authenticationEntryPoint) {
	super(authenticationManager, authenticationEntryPoint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.web.authentication.www.
     * BasicAuthenticationFilter#doFilterInternal(javax.servlet.http.
     * HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * javax.servlet.FilterChain)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	    throws IOException, ServletException {
	String matching = request.getServletPath() + request.getPathInfo();
	if (!AUTHENTICATION_PATH.equals(matching)) {
	    chain.doFilter(request, response);
	} else {
	    super.doFilterInternal(request, response, chain);
	}
    }
}