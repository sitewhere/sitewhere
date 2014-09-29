/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.security;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

/**
 * Maps runtime exceptions to HTTP response codes.
 * 
 * @author Derek
 */
public class SitewhereRuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	public Response toResponse(RuntimeException exception) {
		Response.Status status;
		if (exception instanceof AccessDeniedException) {
			status = Response.Status.FORBIDDEN;
		} else if (exception instanceof AuthenticationException) {
			status = Response.Status.UNAUTHORIZED;
		} else {
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		return Response.status(status).build();
	}
}