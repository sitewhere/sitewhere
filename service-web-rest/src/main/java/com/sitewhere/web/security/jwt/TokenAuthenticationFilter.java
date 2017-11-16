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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sitewhere.grpc.model.spi.security.ITenantAwareAuthentication;
import com.sitewhere.microservice.security.SitewhereGrantedAuthority;
import com.sitewhere.microservice.security.TokenManagement;
import com.sitewhere.rest.ISiteWhereWebConstants;
import com.sitewhere.spi.user.IGrantedAuthority;

/**
 * Filter that pulls JWT and tenant token from authentication header and pushes
 * it into Spring {@link SecurityContextHolder}.
 * 
 * @author Derek
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Header containing JWT on authentication */
    public static final String JWT_HEADER = "X-Sitewhere-JWT";

    /** Authentication header */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /** Token utility methods */
    private TokenManagement tokenUtils = new TokenManagement();

    /** Authentication manager */
    private AuthenticationManager authenticationManager;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(
     * javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	    throws IOException, ServletException {

	String jwt = getJwtFromHeader(request);
	String tenant = getTenantTokenFromHeader(request);
	if (jwt != null) {
	    // Get username from token and load user.
	    String username = getTokenUtils().getUsernameFromToken(jwt);
	    LOGGER.debug("JWT decoded for username: " + username);
	    List<IGrantedAuthority> auths = getTokenUtils().getGrantedAuthoritiesFromToken(jwt);
	    List<GrantedAuthority> springAuths = new ArrayList<GrantedAuthority>();
	    for (IGrantedAuthority auth : auths) {
		springAuths.add(new SitewhereGrantedAuthority(auth));
	    }

	    // Create authentication object based on JWT and tenant token.
	    JwtAuthenticationToken token = new JwtAuthenticationToken(username, springAuths, jwt);
	    Authentication authenticated = getAuthenticationManager().authenticate(token);
	    if (authenticated instanceof ITenantAwareAuthentication) {
		((ITenantAwareAuthentication) authenticated).setTenantToken(tenant);
		LOGGER.info("Added tenant token to authentication: " + tenant);
	    }
	    SecurityContextHolder.getContext().setAuthentication(authenticated);
	    LOGGER.debug("Added authentication to context.");
	    chain.doFilter(request, response);
	} else {
	    LOGGER.debug("No JWT found in header.");
	    chain.doFilter(request, response);
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
     * Get tenant token from request header.
     * 
     * @param request
     * @return
     */
    protected String getTenantTokenFromHeader(HttpServletRequest request) {
	return request.getHeader(ISiteWhereWebConstants.HEADER_TENANT_TOKEN);
    }

    public TokenManagement getTokenUtils() {
	return tokenUtils;
    }

    public void setTokenUtils(TokenManagement tokenUtils) {
	this.tokenUtils = tokenUtils;
    }

    public AuthenticationManager getAuthenticationManager() {
	return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
    }
}