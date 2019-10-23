/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.microservice;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sitewhere.grpc.client.ApiChannelNotAvailableException;
import com.sitewhere.grpc.client.common.security.NotAuthorizedException;
import com.sitewhere.grpc.client.common.security.UnauthenticatedException;
import com.sitewhere.microservice.security.JwtExpiredException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ResourceExistsException;
import com.sitewhere.spi.tenant.TenantNotAvailableException;
import com.sitewhere.spi.web.ISiteWhereWebConstants;

/**
 * Common handler for exceptions generated while processing REST requests.
 */
@ControllerAdvice
public class WebRestExceptionHandling extends ResponseEntityExceptionHandler {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(WebRestExceptionHandling.class);

    /**
     * Handles exception thrown when a tenant operation is requested on an
     * unavailable tenant.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler(value = { TenantNotAvailableException.class })
    protected void handleTenantNotAvailable(TenantNotAvailableException e, HttpServletResponse response) {
	LOGGER.error("Operation invoked on unavailable tenant.", e);
	try {
	    response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "The requested tenant is not available.");
	} catch (IOException e1) {
	    LOGGER.error(e1);
	}
    }

    /**
     * Handle exception where microservice for API is not available.
     * 
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(value = { ApiChannelNotAvailableException.class })
    protected ResponseEntity<Object> handleApiNotAvailable(ApiChannelNotAvailableException e, WebRequest request) {
	return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    /**
     * Handles exceptions where a new resource is to be created, but an existing
     * resource exists with the given key.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler(value = { ResourceExistsException.class })
    protected void handleResourceExists(ResourceExistsException e, HttpServletResponse response) {
	try {
	    sendErrorResponse(e, e.getCode(), HttpServletResponse.SC_CONFLICT, response);
	    LOGGER.error("Resource with same key already exists.", e);
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }

    /**
     * Handle unauthorized requests.
     * 
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(value = { NotAuthorizedException.class })
    protected ResponseEntity<Object> handleNotAuthorized(NotAuthorizedException e, WebRequest request) {
	return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /**
     * Handles unauthenticated access.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler(value = { UnauthenticatedException.class })
    protected ResponseEntity<Object> handleUnauthenticated(UnauthenticatedException e, WebRequest request) {
	return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Handles expired JWT credentials.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler(value = { JwtExpiredException.class })
    protected ResponseEntity<Object> handleJwtExpired(JwtExpiredException e, WebRequest request) {
	return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Handles a system exception by setting the HTML response code and response
     * headers.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler(value = { SiteWhereSystemException.class })
    protected ResponseEntity<Object> handleSystemException(SiteWhereSystemException e, WebRequest request) {
	String combined = e.getCode() + ":" + e.getMessage();
	HttpHeaders headers = new HttpHeaders();
	headers.add(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR, e.getMessage());
	headers.add(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR_CODE, String.valueOf(e.getCode()));
	HttpStatus responseCode = (e.hasHttpResponseCode()) ? HttpStatus.valueOf(e.getHttpResponseCode())
		: HttpStatus.BAD_REQUEST;
	return handleExceptionInternal(e, combined, headers, responseCode, request);
    }

    /**
     * Handles uncaught runtime exceptions such as null pointers.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException e, WebRequest request) {
	LOGGER.error("Showing internal server error due to unhandled runtime exception.", e);
	return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Send error response including SiteWhere headers.
     * 
     * @param e
     * @param errorCode
     * @param responseCode
     * @param response
     * @throws IOException
     */
    protected void sendErrorResponse(Exception e, ErrorCode errorCode, int responseCode, HttpServletResponse response)
	    throws IOException {
	response.setHeader(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR, errorCode.getMessage());
	response.setHeader(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR_CODE, String.valueOf(errorCode.getCode()));
	response.sendError(responseCode, errorCode.getMessage());
    }
}