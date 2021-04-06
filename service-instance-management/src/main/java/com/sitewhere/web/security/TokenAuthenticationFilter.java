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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.web.ISiteWhereWebConstants;

/**
 * Spring request filter used to establish a JWT based on basic auth
 * information.
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(TokenAuthenticationFilter.class);

    /** Authorization header */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private IInstanceManagementMicroservice microservice;

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

	String jwt = request.getHeader(AUTHORIZATION_HEADER);
	if (StringUtils.isBlank(jwt) || !jwt.startsWith("Bearer ")) {
	    chain.doFilter(request, response);
	    return;
	}

	jwt = jwt.substring(7);
	String tenantId = request.getHeader(ISiteWhereWebConstants.HEADER_TENANT_ID);

	try {
	    SiteWhereAuthentication authenticated = getMicroservice().getTokenManagement()
		    .getAuthenticationFromToken(jwt);
	    authenticated.setTenantToken(tenantId);
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format("Authentication:\n%s\n\n",
			MarshalUtils.marshalJsonAsPrettyString(authenticated)));
	    }
	    UserContext.setContext(authenticated);
	} catch (SiteWhereException e) {
	    LOGGER.debug(String.format("Unable to set user context from JWT: %s", e.getMessage()), e);
	}
	chain.doFilter(request, response);
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}