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
package com.sitewhere.web.rest;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.instance.microservice.InstanceManagementMicroservice;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.web.ISiteWhereWebConstants;

/**
 * Handles JWT authentication for API requests.
 */
@Provider
public class JwtAuthForApi implements ContainerRequestFilter {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(JwtAuthForApi.class);

    /** Authorization header */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Inject
    InstanceManagementMicroservice microservice;

    /** JWT token management */
    @Inject
    ITokenManagement tokenManagement;

    /** Instance settings */
    @Inject
    IInstanceSettings instanceSettings;

    /*
     * @see
     * javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.
     * ContainerRequestContext)
     */
    @Override
    public void filter(ContainerRequestContext context) throws IOException {
	// Only handle calls to the 'api' subpath.
	List<PathSegment> paths = context.getUriInfo().getPathSegments();
	if (paths.size() == 0 || !paths.get(0).getPath().equals("api")) {
	    return;
	}

	String jwt = context.getHeaderString(AUTHORIZATION_HEADER);
	if (jwt == null) {
	    LOGGER.debug("API request attempted without JWT passed.");
	    context.abortWith(Response.status(Status.UNAUTHORIZED).build());
	    return;
	}
	jwt = jwt.substring(7);

	String tenantId = context.getHeaderString(ISiteWhereWebConstants.HEADER_TENANT_ID);
	try {
	    SiteWhereAuthentication authenticated = getTokenManagement().getAuthenticationFromToken(jwt);
	    authenticated.setTenantToken(tenantId);
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format("Authentication:\n%s\n\n",
			MarshalUtils.marshalJsonAsPrettyString(authenticated)));
	    }
	    UserContext.setContext(authenticated);
	} catch (SiteWhereException e) {
	    LOGGER.debug(String.format("Unable to process JWT: %s", e.getMessage()), e);
	    context.abortWith(Response.status(Status.FORBIDDEN).build());
	    return;
	}
    }

    protected InstanceManagementMicroservice getMicroservice() {
	return microservice;
    }

    protected ITokenManagement getTokenManagement() {
	return tokenManagement;
    }

    protected IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    protected IUserManagement getUserManagement() {
	return getMicroservice().getUserManagement();
    }
}
