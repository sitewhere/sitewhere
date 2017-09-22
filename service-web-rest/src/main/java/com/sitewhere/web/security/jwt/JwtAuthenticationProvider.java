package com.sitewhere.web.security.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.authentication.AuthenticationProvider#
     * authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.authentication.AuthenticationProvider#
     * supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
	return (JwtAuthenticationToken.class.isAssignableFrom(clazz));
    }
}