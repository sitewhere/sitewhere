/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.security;

import javax.servlet.http.HttpServletRequest;

import com.sitewhere.rest.ISiteWhereWebConstants;

/**
 * HTTP header utility methods.
 * 
 * @author Derek
 */
public class SiteWhereHttpHeaders {

    /** Authentication header */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Load JWT from authentication header.
     * 
     * @param request
     * @return
     */
    public static String getJwtFromHeader(HttpServletRequest request) {
	String authHeader = request.getHeader(AUTHORIZATION_HEADER);
	if (authHeader != null && authHeader.startsWith("Bearer ")) {
	    return authHeader.substring(7);
	}
	return null;
    }

    /**
     * Get tenant id from request header.
     * 
     * @param request
     * @return
     */
    public static String getTenantIdFromHeader(HttpServletRequest request) {
	return request.getHeader(ISiteWhereWebConstants.HEADER_TENANT_ID);
    }

    /**
     * Get tenant auth token from request header.
     * 
     * @param request
     * @return
     */
    public static String getTenantAuthFromHeader(HttpServletRequest request) {
	return request.getHeader(ISiteWhereWebConstants.HEADER_TENANT_AUTH);
    }
}