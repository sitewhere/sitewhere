/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.security;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

/**
 * Spring authentication provider backed by Atlas.
 * 
 * @author Derek
 */
public class SitewhereAuthenticationProvider implements AuthenticationProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.springframework.security.providers.AuthenticationProvider#authenticate(org.
	 * springframework.security. Authentication)
	 */
	public Authentication authenticate(Authentication input) throws AuthenticationException {
		try {
			if (input instanceof UsernamePasswordAuthenticationToken) {
				String username = (String) input.getPrincipal();
				String password = (String) input.getCredentials();
				IUser user = SiteWhere.getServer().getUserManagement().authenticate(username, password);
				List<IGrantedAuthority> auths =
						SiteWhere.getServer().getUserManagement().getGrantedAuthorities(user.getUsername());
				SitewhereUserDetails details = new SitewhereUserDetails(user, auths);
				return new SitewhereAuthentication(details, password);
			} else if (input instanceof SitewhereAuthentication) {
				return input;
			} else {
				throw new AuthenticationServiceException("Unknown authentication: "
						+ input.getClass().getName());
			}
		} catch (SiteWhereException e) {
			throw new BadCredentialsException("Unable to authenticate.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.providers.AuthenticationProvider#supports(java.lang
	 * .Class)
	 */
	@SuppressWarnings("rawtypes")
	public boolean supports(Class clazz) {
		return true;
	}
}