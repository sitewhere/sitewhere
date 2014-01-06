/*
 * LoginManager.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.IUser;

/**
 * Provides helper methods for dealing with currently logged in user.
 * 
 * @author Derek
 */
public class LoginManager {

	/**
	 * Get the currently logged in user from Spring Security.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static IUser getCurrentlyLoggedInUser() throws SiteWhereException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			throw new SiteWhereSystemException(ErrorCode.NotLoggedIn, ErrorLevel.ERROR,
					HttpServletResponse.SC_FORBIDDEN);
		}
		if (!(auth instanceof SitewhereAuthentication)) {
			throw new SiteWhereException("Authentication was not of expected type: "
					+ SitewhereAuthentication.class.getName());
		}
		return (IUser) ((SitewhereAuthentication) auth).getPrincipal();
	}
}