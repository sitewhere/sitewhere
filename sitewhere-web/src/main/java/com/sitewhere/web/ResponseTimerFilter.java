/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that prints the response time out to the console.
 * 
 * @author Derek
 */
public class ResponseTimerFilter extends OncePerRequestFilter {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ResponseTimerFilter.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		chain.doFilter(request, response);
		long time = System.currentTimeMillis() - start;
		LOGGER.info("Call for " + request.getMethod() + " to '" + request.getRequestURL() + "' returned in "
				+ time + " ms.");
	}
}