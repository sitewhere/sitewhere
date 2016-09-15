/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.mvc;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import com.sitewhere.SiteWhere;
import com.sitewhere.security.LoginManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.version.VersionHelper;

/**
 * Base class for SiteWhere Spring MVC controllers.
 * 
 * @author dadams
 */
public class MvcController {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Version information sent in request */
    public static final String DATA_VERSION = "version";

    /** Current user information sent in request */
    public static final String DATA_CURRENT_USER = "currentUser";

    /** Granted authorities sent in request */
    public static final String DATA_AUTHORITIES = "authorities";

    /** Tenant information sent in request */
    public static final String DATA_TENANT = "tenant";

    /** Redirect URL for tenant selection page */
    public static final String DATA_REDIRECT = "redirect";

    /** Encoded basic auth header information */
    public static final String DATA_BASIC_AUTH = "basicAuth";

    /**
     * Show error message for exception.
     * 
     * @param e
     * @return
     */
    protected ModelAndView showError(Exception e) {
	LOGGER.error("Error in MVC controller.", e);
	return showError(e.getMessage());
    }

    /**
     * Returns a {@link ModelAndView} that will display an error message.
     * 
     * @param message
     * @return
     */
    protected ModelAndView showError(String message) {
	try {
	    Map<String, Object> data = new HashMap<String, Object>();
	    data.put(DATA_VERSION, VersionHelper.getVersion());
	    data.put(DATA_CURRENT_USER, LoginManager.getCurrentlyLoggedInUser());
	    data.put("message", message);
	    return new ModelAndView("error", data);
	} catch (SiteWhereException e) {
	    Map<String, Object> data = new HashMap<String, Object>();
	    data.put(DATA_VERSION, VersionHelper.getVersion());
	    data.put("message", e.getMessage());
	    return new ModelAndView("error", data);
	}
    }

    /**
     * Create data structure and common objects passed to pages. Require a
     * tenant is chosen for the user.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    protected Map<String, Object> createTenantPageBaseData(String tenantId, HttpServletRequest request)
	    throws SiteWhereException {
	Map<String, Object> data = createBaseData(request);
	IUser user = LoginManager.getCurrentlyLoggedInUser();
	if (user == null) {
	    throw new SiteWhereSystemException(ErrorCode.NotLoggedIn, ErrorLevel.ERROR);
	}
	List<ITenant> authed = SiteWhere.getServer().getAuthorizedTenants(user.getUsername(), true);
	for (ITenant tenant : authed) {
	    if (tenant.getId().equals(tenantId)) {
		data.put(DATA_TENANT, tenant);
		break;
	    }
	}
	return data;
    }

    /**
     * Create data structure and common objects passed to pages.
     * 
     * @param request
     * @param requireTenant
     * @return
     * @throws SiteWhereException
     */
    protected Map<String, Object> createBaseData(HttpServletRequest request) throws SiteWhereException {
	IUser user = LoginManager.getCurrentlyLoggedInUser();

	Map<String, Object> data = new HashMap<String, Object>();
	data.put(DATA_VERSION, VersionHelper.getVersion());
	data.put(DATA_CURRENT_USER, user);
	data.put(DATA_AUTHORITIES, new AuthoritiesHelper(LoginManager.getCurrentlyLoggedInUser()));

	if (user != null) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String creds = user.getUsername() + ":" + auth.getCredentials();
	    String basicAuth = new String(Base64.getEncoder().encodeToString(creds.getBytes()));
	    data.put(DATA_BASIC_AUTH, basicAuth);
	}

	return data;
    }

    /**
     * Get URL from servlet request.
     * 
     * @param request
     * @return
     */
    protected static String getUrl(HttpServletRequest request) {
	String reqUrl = request.getRequestURL().toString();
	String queryString = request.getQueryString();
	if (queryString != null) {
	    reqUrl += "?" + queryString;
	}
	return reqUrl;
    }
}