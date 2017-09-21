package com.sitewhere.web.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that pulls JWT from authentication header and pushes it into Spring
 * {@link SecurityContextHolder}.
 * 
 * @author Derek
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /** Authentication header */
    private static final String AUTHENTICATION_HEADER = "Authentication";

    /** Token utility methods */
    private TokenUtils tokenUtils = new TokenUtils();

    /** User management implementation */
    private UserDetailsService userDetailsService;

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
	if (jwt != null) {
	    // Get username from token and load user.
	    String username = getTokenUtils().getUsernameFromToken(jwt);
	    UserDetails userDetails = getUserDetailsService().loadUserByUsername(username);

	    // Create authentication
	    JwtAuthenticationToken token = new JwtAuthenticationToken(userDetails, jwt);
	    SecurityContextHolder.getContext().setAuthentication(token);
	}
	chain.doFilter(request, response);
    }

    /**
     * Load JWT from authentication header.
     * 
     * @param request
     * @return
     */
    protected String getJwtFromHeader(HttpServletRequest request) {
	String authHeader = request.getHeader(AUTHENTICATION_HEADER);
	if (authHeader != null && authHeader.startsWith("Bearer ")) {
	    return authHeader.substring(7);
	}
	return null;
    }

    public TokenUtils getTokenUtils() {
	return tokenUtils;
    }

    public void setTokenUtils(TokenUtils tokenUtils) {
	this.tokenUtils = tokenUtils;
    }

    public UserDetailsService getUserDetailsService() {
	return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
	this.userDetailsService = userDetailsService;
    }
}