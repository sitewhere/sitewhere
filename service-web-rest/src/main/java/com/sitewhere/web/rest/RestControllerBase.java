/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.microservice.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.SiteWhereAuthority;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Base class for common REST controller functionality.
 * 
 * @author Derek Adams
 */
public class RestControllerBase {

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
     * Verifies that requestor has all of the given authorities or throws a
     * "forbidden" error.
     * 
     * @param auths
     * @throws SiteWhereException
     */
    public static void checkAuthForAll(SiteWhereAuthority... auths) throws SiteWhereException {
	for (SiteWhereAuthority auth : auths) {
	    checkAuthFor(auth, true);
	}
    }

    /**
     * Verifies that requestor has the given authority and can throws a "forbidden"
     * error if not.
     * 
     * @param auth
     * @param throwException
     * @return
     * @throws SiteWhereException
     */
    public static boolean checkAuthFor(SiteWhereAuthority auth, boolean throwException) throws SiteWhereException {
	IUser user = UserContextManager.getCurrentlyLoggedInUser();
	if (!user.getAuthorities().contains(auth.getName())) {
	    LOGGER.warn("User not authorized for role " + auth.getRoleName() + ".");
	    if (throwException) {
		throw operationNotPermitted();
	    }
	    return false;
	}
	return true;
    }

    /**
     * Throw exception indicating operation is not permitted.
     * 
     * @throws SiteWhereException
     */
    public static SiteWhereSystemException operationNotPermitted() throws SiteWhereException {
	return new SiteWhereSystemException(ErrorCode.OperationNotPermitted, ErrorLevel.ERROR,
		HttpServletResponse.SC_FORBIDDEN);
    }
}