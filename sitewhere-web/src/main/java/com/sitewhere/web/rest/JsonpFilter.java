/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Wraps API calls that request a JSONP response.
 * 
 * @author Derek
 */
public class JsonpFilter implements Filter {

	/** Parameter containing function name for callback function */
	private static final String CALLBACK_PARAMETER = "callback";

	/** The default padding to use if the specified padding contains invalid characters */
	public static final String DEFAULT_CALLBACK = "jsonpDataResult";

	/** Regular expression that determines if callback is a valid function name */
	public static final Pattern SAFE_PATTERN = Pattern.compile("[a-zA-Z0-9_\\.]+");

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		Map<String, String[]> parms = httpRequest.getParameterMap();

		if (parms.containsKey(CALLBACK_PARAMETER)) {

			// Make sure that callback value is a function name and not code.
			String callback = parms.get(CALLBACK_PARAMETER)[0];
			if (!SAFE_PATTERN.matcher(callback).matches()) {
				callback = DEFAULT_CALLBACK;
			}
			OutputStream out = httpResponse.getOutputStream();
			GenericResponseWrapper wrapper = new GenericResponseWrapper(httpResponse);
			chain.doFilter(request, wrapper);

			out.write(new String(callback + "(").getBytes());
			out.write(wrapper.getData());
			out.write(new String(");").getBytes());

			wrapper.setContentType("text/javascript;charset=UTF-8");
			out.close();
		} else {
			chain.doFilter(request, response);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}
}