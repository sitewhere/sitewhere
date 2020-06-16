/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    /*
     * @see
     * javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.container.
     * ContainerRequestContext, javax.ws.rs.container.ContainerResponseContext)
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
	    throws IOException {
	responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
	responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
	responseContext.getHeaders().add("Access-Control-Allow-Headers",
		"origin, content-type, accept, authorization, x-sitewhere-tenant-id, x-sitewhere-tenant-auth");
	responseContext.getHeaders().add("Access-Control-Expose-Headers",
		"x-sitewhere-jwt, x-sitewhere-error, x-sitewhere-error-code");
	responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}