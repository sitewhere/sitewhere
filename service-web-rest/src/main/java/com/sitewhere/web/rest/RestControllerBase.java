/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.SiteWhereAuthority;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Base class for common REST controller functionality.
 * 
 * @author Derek Adams
 */
public class RestControllerBase {

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