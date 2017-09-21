package com.sitewhere.web.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Extends {@link AbstractAuthenticationToken} with JWT details.
 * 
 * @author Derek
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    /** Serial version UID */
    private static final long serialVersionUID = 246330914120864789L;

    /** Wrapped principle */
    private final UserDetails principle;

    /** Token */
    private String token;

    public JwtAuthenticationToken(UserDetails principle, String token) {
	super(principle.getAuthorities());
	this.principle = principle;
	this.token = token;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.authentication.AbstractAuthenticationToken#
     * isAuthenticated()
     */
    @Override
    public boolean isAuthenticated() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.Authentication#getCredentials()
     */
    @Override
    public Object getCredentials() {
	return token;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.Authentication#getPrincipal()
     */
    @Override
    public UserDetails getPrincipal() {
	return principle;
    }
}