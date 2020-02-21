/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.instance.microservice.InstanceManagementMicroservice;
import com.sitewhere.microservice.api.user.IUserManagement;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.web.ISiteWhereWebConstants;

import io.jsonwebtoken.Claims;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;

/**
 * Handles JWT authentication for API requests.
 */
@Provider
public class JwtAuthForApi implements ContainerRequestFilter {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(JwtAuthForApi.class);

    /** Authorization header */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Inject
    InstanceManagementMicroservice microservice;

    /** JWT token management */
    @Inject
    ITokenManagement tokenManagement;

    /** Instance settings */
    @Inject
    IInstanceSettings instanceSettings;

    /*
     * @see
     * javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.
     * ContainerRequestContext)
     */
    @Override
    public void filter(ContainerRequestContext context) throws IOException {
	// Only handle calls to the 'api' subpath.
	List<PathSegment> paths = context.getUriInfo().getPathSegments();
	if (paths.size() == 0 || !paths.get(0).getPath().equals("api")) {
	    return;
	}

	String jwt = context.getHeaderString(AUTHORIZATION_HEADER);
	if (jwt == null) {
	    LOGGER.info("API request attempted without JWT passed.");
	    context.abortWith(Response.status(Status.UNAUTHORIZED).build());
	    return;
	}
	jwt = jwt.substring(7);

	String tenantId = context.getHeaderString(ISiteWhereWebConstants.HEADER_TENANT_ID);
	SiteWhereTenant tenant = null;
	if (tenantId != null) {
	    tenant = getMicroservice().getSiteWhereKubernetesClient().getTenants()
		    .inNamespace(getInstanceSettings().getKubernetesNamespace()).withName(tenantId).get();
	}

	try {
	    SiteWhereAuthentication authenticated = authenticate(jwt, tenant);
	    authenticated.setTenant(tenant);
	    UserContext.setContext(authenticated);
	} catch (SiteWhereException e) {
	    LOGGER.warn("Error locating user for JWT token request.", e);
	    context.abortWith(Response.status(Status.FORBIDDEN).build());
	    return;
	}
    }

    /**
     * Attempt to establish authentication based on JWT information.
     * 
     * @param jwt
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    protected SiteWhereAuthentication authenticate(String jwt, SiteWhereTenant tenant) throws SiteWhereException {
	Claims claims = getTokenManagement().getClaimsForToken(jwt);
	String username = getTokenManagement().getUsernameFromClaims(claims);
	IUser user = getUserManagement().getUserByUsername(username);
	if (user != null) {
	    List<String> auths = getTokenManagement().getGrantedAuthoritiesFromClaims(claims);
	    return new SiteWhereAuthentication(username, auths, jwt);
	}
	throw new SiteWhereException("User associated with JWT not found.");
    }

    protected InstanceManagementMicroservice getMicroservice() {
	return microservice;
    }

    protected ITokenManagement getTokenManagement() {
	return tokenManagement;
    }

    protected IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    protected IUserManagement getUserManagement() {
	return getMicroservice().getUserManagement();
    }
}
