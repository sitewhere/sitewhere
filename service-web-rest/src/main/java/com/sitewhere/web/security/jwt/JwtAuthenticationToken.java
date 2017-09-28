package com.sitewhere.web.security.jwt;

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Extends {@link AbstractAuthenticationToken} with JWT details.
 * 
 * @author Derek
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    /** Serial version UID */
    private static final long serialVersionUID = 246330914120864789L;

    /** Username */
    private String username;

    /** Token */
    private String token;

    public JwtAuthenticationToken(String username, List<GrantedAuthority> authorities, String token) {
	super(authorities);
	this.username = username;
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
	return false;
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
    public Object getPrincipal() {
	return username;
    }
}