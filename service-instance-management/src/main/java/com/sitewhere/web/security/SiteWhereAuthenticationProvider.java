/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.web.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IRole;
import com.sitewhere.spi.user.IUser;

/**
 * Authenticates credentials against SiteWhere user management and assigns a JWT
 * if authorized.
 */
@Component
public class SiteWhereAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /*
     * @see org.springframework.security.authentication.AuthenticationProvider#
     * authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	try {
	    String username = authentication.getName();
	    String password = authentication.getCredentials().toString();
	    IUser user = getMicroservice().getUserManagement().getUserByUsername(username);
	    List<String> auths = new ArrayList<>();
	    for (IRole role : user.getRoles()) {
		for (IGrantedAuthority auth : role.getAuthorities()) {
		    if (!auths.contains(auth.getAuthority())) {
			auths.add(auth.getAuthority());
		    }
		}
	    }

	    String token = getMicroservice().getUserManagement().getAccessToken(username, password);
	    return new SiteWhereAuthentication(username, auths, token);
	} catch (SiteWhereException e) {
	    throw new BadCredentialsException("Unable to authenticate user.", e);
	}
    }

    /*
     * @see
     * org.springframework.security.authentication.AuthenticationProvider#supports(
     * java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> authentication) {
	return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}