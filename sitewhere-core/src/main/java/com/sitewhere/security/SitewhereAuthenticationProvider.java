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

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Spring authentication provider backed by Atlas.
 * 
 * @author Derek
 */
public class SitewhereAuthenticationProvider implements AuthenticationProvider {

	/** User management implementation */
	private IUserManagement userManagement;

	public SitewhereAuthenticationProvider(IUserManagement userManagement) {
		this.userManagement = userManagement;
	}

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
				if (getUserManagement() == null) {
					throw new AuthenticationServiceException(
							"User management not available. Check logs for details.");
				}
				IUser user = getUserManagement().authenticate(username, password);
				List<IGrantedAuthority> auths = getUserManagement().getGrantedAuthorities(user.getUsername());
				SitewhereUserDetails details = new SitewhereUserDetails(user, auths);
				return new SitewhereAuthentication(details, password);
			} else if (input instanceof SitewhereAuthentication) {
				return input;
			} else {
				throw new AuthenticationServiceException(
						"Unknown authentication: " + input.getClass().getName());
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

	public IUserManagement getUserManagement() {
		return userManagement;
	}

	public void setUserManagement(IUserManagement userManagement) {
		this.userManagement = userManagement;
	}
}