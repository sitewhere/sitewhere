/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.IUser;

/**
 * Provides helper methods for dealing with authenticated user and related
 * contexts.
 * 
 * @author Derek
 */
public class UserContextManager {

    /**
     * Get the currently logged in user from Spring Security.
     * 
     * @return
     * @throws SiteWhereException
     */
    public static IUser getCurrentlyLoggedInUser() throws SiteWhereException {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	if (auth == null) {
	    throw new SiteWhereSystemException(ErrorCode.NotLoggedIn, ErrorLevel.ERROR, 401);
	}
	if (!(auth instanceof SitewhereAuthentication)) {
	    throw new SiteWhereException("Authentication was not of expected type: "
		    + SitewhereAuthentication.class.getName() + " found " + auth.getClass().getName() + " instead.");
	}
	return (IUser) ((SitewhereAuthentication) auth).getPrincipal();
    }

    /**
     * Get tenant context associated with user.
     * 
     * @param require
     * @return
     * @throws SiteWhereException
     */
    public static ITenant getCurrentTenant(boolean require) throws SiteWhereException {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	if (auth == null) {
	    if (require) {
		throw new SiteWhereSystemException(ErrorCode.NotLoggedIn, ErrorLevel.ERROR, 401);
	    } else {
		return null;
	    }
	}
	if (!(auth instanceof SitewhereAuthentication)) {
	    throw new SiteWhereException("Authentication was not of expected type: "
		    + SitewhereAuthentication.class.getName() + " found " + auth.getClass().getName() + " instead.");
	}
	return (ITenant) ((SitewhereAuthentication) auth).getTenant();
    }

    /**
     * Set tenant for current context (creating a placeholder if necessary).
     * 
     * @param tenant
     * @throws SiteWhereException
     */
    public static void setCurrentTenant(ITenant tenant) throws SiteWhereException {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	if (auth == null) {
	    SitewhereAuthentication sw = new SitewhereAuthentication(null, null);
	    sw.setTenant(tenant);
	    SecurityContextHolder.getContext().setAuthentication(sw);
	    return;
	} else if (auth instanceof SitewhereAuthentication) {
	    ((SitewhereAuthentication) auth).setTenant(tenant);
	}
    }
}