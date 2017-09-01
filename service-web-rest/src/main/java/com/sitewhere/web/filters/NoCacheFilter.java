/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Adds header to prevent caching of REST calls in crappy browsers.
 * 
 * @author Derek
 */
public class NoCacheFilter extends OncePerRequestFilter {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(
     * javax.servlet.http.HttpServletRequest ,
     * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	    throws ServletException, IOException {
	response.addHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
	response.addHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
	chain.doFilter(request, response);
    }
}