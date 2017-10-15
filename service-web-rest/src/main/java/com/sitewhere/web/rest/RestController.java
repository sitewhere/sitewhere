/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sitewhere.rest.ISiteWhereWebConstants;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.error.ResourceExistsException;
import com.sitewhere.spi.tenant.TenantNotAvailableException;
import com.sitewhere.spi.user.SiteWhereAuthority;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Base class for common REST controller functionality.
 * 
 * @author Derek Adams
 */
public class RestController {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    @Autowired
    private IWebRestMicroservice microservice;

    /**
     * Get handle to microservice.
     * 
     * @return
     */
    public IWebRestMicroservice getMicroservice() {
	return microservice;
    }

    /**
     * Handles exception thrown when a tenant operation is requested on an
     * unavailable tenant.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler
    protected void handleTenantNotAvailable(TenantNotAvailableException e, HttpServletResponse response) {
	LOGGER.error("Operation invoked on unavailable tenant.", e);
	try {
	    response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "The requested tenant is not available.");
	} catch (IOException e1) {
	    LOGGER.error(e1);
	}
    }

    /**
     * Handles exceptions where a new resource is to be created, but an existing
     * resource exists with the given key.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler
    protected void handleResourceExists(ResourceExistsException e, HttpServletResponse response) {
	try {
	    sendErrorResponse(e, e.getCode(), HttpServletResponse.SC_CONFLICT, response);
	    LOGGER.error("Resource with same key already exists.", e);
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
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

    /**
     * Handles situations where user does not pass exprected content for a POST.
     * 
     * @param e
     * @param response
     */
    @ExceptionHandler
    protected void handleMissingContent(HttpMessageNotReadableException e, HttpServletResponse response) {
	try {
	    LOGGER.error("Error handling REST request..", e);
	    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No body content passed for POST request.");
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }

    /**
     * Verifies that requestor has all of the given authorities or throws a
     * "forbidden" error.
     * 
     * @param request
     * @param response
     * @param roles
     * @throws SiteWhereException
     */
    public static void checkAuthForAll(HttpServletRequest request, HttpServletResponse response,
	    SiteWhereAuthority... auths) throws SiteWhereException {
	for (SiteWhereAuthority auth : auths) {
	    checkAuthFor(request, response, auth, true);
	}
    }

    /**
     * Verifies that requestor has the given authority and can throws a
     * "forbidden" error if not.
     * 
     * @param request
     * @param response
     * @param auth
     * @param throwException
     * @return
     * @throws SiteWhereException
     */
    public static boolean checkAuthFor(HttpServletRequest request, HttpServletResponse response,
	    SiteWhereAuthority auth, boolean throwException) throws SiteWhereException {
	if (!request.isUserInRole(auth.getRoleName())) {
	    if (throwException) {
		throw new SiteWhereSystemException(ErrorCode.OperationNotPermitted, ErrorLevel.ERROR,
			HttpServletResponse.SC_FORBIDDEN);
	    }
	    return false;
	}
	return true;
    }
}