/*
 * TracerFilter.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.sitewhere.spi.server.debug.ITracer;

/**
 * Enables output of messages from {@link ITracer} implementation if a request parameter
 * is passed. Actual output from request is thrown away and tracer stack is returned as
 * HTML.
 * 
 * @author Derek
 */
public class TracerFilter extends OncePerRequestFilter {

	/** Parameter that indicates response should be replaced by tracer output */
	public static final String TRACER_PARAM = "tracer";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		chain.doFilter(request, response);
		String tracer = request.getParameter(TRACER_PARAM);
		if (tracer != null) {
			
		}
	}
}