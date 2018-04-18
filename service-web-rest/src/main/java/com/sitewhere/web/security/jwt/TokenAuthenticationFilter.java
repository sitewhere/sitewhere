/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sitewhere.rest.ISiteWhereWebConstants;
import com.sitewhere.security.SitewhereGrantedAuthority;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.security.ITenantAwareAuthentication;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

import io.jsonwebtoken.Claims;

/**
 * Filter that pulls JWT and tenant token from authentication header and pushes
 * it into Spring {@link SecurityContextHolder}.
 * 
 * @author Derek
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(TokenAuthenticationFilter.class);

    /** Header containing JWT on authentication */
    public static final String JWT_HEADER = "X-Sitewhere-JWT";

    /** Authentication header */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /** Web/REST microservice */
    private IWebRestMicroservice<?> microservice;

    /** Authentication manager */
    private AuthenticationManager authenticationManager;

    public TokenAuthenticationFilter(IWebRestMicroservice<?> microservice,
	    AuthenticationManager authenticationManager) {
	this.microservice = microservice;
	this.authenticationManager = authenticationManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	    throws IOException, ServletException {

	String jwt = getJwtFromHeader(request);
	String tenantId = getTenantIdFromHeader(request);
	String tenantAuth = getTenantAuthFromHeader(request);
	if (jwt != null) {
	    // Get username from token and load user.
	    Claims claims = getMicroservice().getTokenManagement().getClaimsForToken(jwt);
	    String username = getMicroservice().getTokenManagement().getUsernameFromClaims(claims);
	    LOGGER.debug("JWT decoded for username: " + username);
	    List<IGrantedAuthority> auths = getMicroservice().getTokenManagement()
		    .getGrantedAuthoritiesFromClaims(claims);
	    List<GrantedAuthority> springAuths = new ArrayList<GrantedAuthority>();
	    for (IGrantedAuthority auth : auths) {
		springAuths.add(new SitewhereGrantedAuthority(auth));
	    }

	    // Create authentication object based on JWT and tenant token.
	    JwtAuthenticationToken token = new JwtAuthenticationToken(username, springAuths, jwt);
	    Authentication authenticated = getAuthenticationManager().authenticate(token);
	    if ((!StringUtils.isEmpty(tenantId)) && (StringUtils.isEmpty(tenantAuth))) {
		throw new SiteWhereException("Tenant id passed without corresponding tenant auth token.");
	    }

	    // Add tenant authentication data if provided.
	    addTenantAuthenticationData(authenticated, tenantId, tenantAuth);

	    SecurityContextHolder.getContext().setAuthentication(authenticated);
	    LOGGER.debug("Added authentication to context.");
	    chain.doFilter(request, response);
	} else {
	    LOGGER.debug("No JWT found in header.");
	    chain.doFilter(request, response);
	}
    }

    /**
     * Based on fields passed in HTTP headers, look up tenant and verify that tenant
     * auth token is valid. Store tenant information in Spring authentication data
     * so that it can be passed via GRPC channels for remote microservices.
     * 
     * @param authenticated
     * @param tenantToken
     * @param tenantAuth
     * @throws SiteWhereException
     */
    protected void addTenantAuthenticationData(Authentication authenticated, String tenantToken, String tenantAuth)
	    throws SiteWhereException {
	if ((authenticated instanceof ITenantAwareAuthentication) && (tenantToken != null) && (tenantAuth != null)) {
	    // Load tenant using superuser credentials.
	    Authentication previous = SecurityContextHolder.getContext().getAuthentication();
	    try {
		SecurityContextHolder.getContext()
			.setAuthentication(getMicroservice().getSystemUser().getAuthentication());
		ITenant tenant = getMicroservice().getTenantManagementApiDemux().getApiChannel()
			.getTenantByToken(tenantToken);
		if ((tenant == null) || (!tenant.getAuthenticationToken().equals(tenantAuth))) {
		    throw new SiteWhereException("Auth token passed for tenant id is not correct.");
		}
		((ITenantAwareAuthentication) authenticated).setTenant(tenant);
		LOGGER.debug("Added tenant to authentication: " + tenant.getId());
	    } finally {
		SecurityContextHolder.getContext().setAuthentication(previous);
	    }
	}
    }

    /**
     * Load JWT from authentication header.
     * 
     * @param request
     * @return
     */
    protected String getJwtFromHeader(HttpServletRequest request) {
	String authHeader = request.getHeader(AUTHORIZATION_HEADER);
	LOGGER.debug("Authorization header is: " + authHeader);
	if (authHeader != null && authHeader.startsWith("Bearer ")) {
	    return authHeader.substring(7);
	}
	return null;
    }

    /**
     * Get tenant id from request header.
     * 
     * @param request
     * @return
     */
    protected String getTenantIdFromHeader(HttpServletRequest request) {
	return request.getHeader(ISiteWhereWebConstants.HEADER_TENANT_ID);
    }

    /**
     * Get tenant auth token from request header.
     * 
     * @param request
     * @return
     */
    protected String getTenantAuthFromHeader(HttpServletRequest request) {
	return request.getHeader(ISiteWhereWebConstants.HEADER_TENANT_AUTH);
    }

    public IWebRestMicroservice<?> getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IWebRestMicroservice<?> microservice) {
	this.microservice = microservice;
    }

    public AuthenticationManager getAuthenticationManager() {
	return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
    }
}