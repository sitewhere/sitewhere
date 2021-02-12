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
package com.sitewhere.web.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.instance.microservice.InstanceManagementMicroservice;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.LifecycleStatus;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.user.IUserManagement;

/**
 * Handles basic authentication for JWT authentication requests.
 */
@Provider
public class BasicAuthForJwt implements ContainerRequestFilter {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(BasicAuthForJwt.class);

    /** Authorization header */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Inject
    InstanceManagementMicroservice microservice;

    /** JWT token management */
    @Inject
    ITokenManagement tokenManagement;

    /*
     * @see
     * javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.
     * ContainerRequestContext)
     */
    @Override
    public void filter(ContainerRequestContext context) throws IOException {
	// Do not service request if microservice is not completely started.
	if (getMicroservice().getLifecycleStatus() != LifecycleStatus.Started) {
	    LOGGER.info("JWT request attempted before service started.");
	    context.abortWith(Response.status(Status.SERVICE_UNAVAILABLE).build());
	    return;
	}
	// Only handle calls to the 'authapi' subpath.
	List<PathSegment> paths = context.getUriInfo().getPathSegments();
	if (paths.size() == 0 || !paths.get(0).getPath().equals("authapi")) {
	    return;
	}

	String encoded = context.getHeaderString(AUTHORIZATION_HEADER);
	if (encoded == null) {
	    LOGGER.info("JWT request attempted without basic auth credentials passed.");
	    context.abortWith(Response.status(Status.UNAUTHORIZED).build());
	    return;
	}
	encoded = encoded.substring(6);
	try {
	    SiteWhereAuthentication authenticated = authenticate(encoded);
	    UserContext.setContext(authenticated);
	} catch (SiteWhereException e) {
	    LOGGER.warn("Error authenticating user for JWT token request.", e);
	    context.abortWith(Response.status(Status.FORBIDDEN).build());
	    return;
	}
    }

    /**
     * Attempt to look up user based on encoded authentication details, then use the
     * information to create a JWT.
     * 
     * @param encoded
     * @return
     * @throws SiteWhereException
     */
    protected SiteWhereAuthentication authenticate(String encoded) throws SiteWhereException {
	String decoded = new String(Base64.decodeBase64(encoded));
	String[] parts = decoded.split(":");
	if (parts.length > 1) {
	    String username = parts[0];
	    String password = parts[1];
	    String token = getUserManagement().getAccessToken(username, password);
	    List<String> auths = new ArrayList<>();
	    return new SiteWhereAuthentication(username, auths, token);
	}
	throw new SiteWhereException(String.format("Invalid basic auth content: %s", decoded));
    }

    protected InstanceManagementMicroservice getMicroservice() {
	return microservice;
    }

    protected ITokenManagement getTokenManagement() {
	return tokenManagement;
    }

    protected IUserManagement getUserManagement() {
	return getMicroservice().getUserManagement();
    }
}