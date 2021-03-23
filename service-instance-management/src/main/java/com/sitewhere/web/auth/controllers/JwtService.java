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
package com.sitewhere.web.auth.controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.keycloak.representations.AccessTokenResponse;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.web.ISiteWhereWebConstants;

import io.swagger.annotations.ApiOperation;

/**
 * Controller for security operations.
 */
@Path("/authapi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "JWT Authentication", description = "Supports authentication via JSON Web Token.")
@SecurityRequirement(name = "basicAuth", scopes = {})
public class JwtService {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(JwtService.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Use basic authentication information to generate a JWT access token and
     * return it as a header in the servlet response. This is the only method that
     * allows basic authentication. All others expect the JWT in the Authorization
     * header.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/jwt")
    @ApiOperation(value = "Authenticate and receive an access token")
    public Response jwtWithUserDetail() throws SiteWhereException {
	SiteWhereAuthentication auth = UserContext.getCurrentUser();
	if (auth != null) {
	    AccessTokenResponse accessToken = MarshalUtils.unmarshalJson(auth.getJwt().getBytes(),
		    AccessTokenResponse.class);
	    IUser user = getMicroservice().getUserManagement().getUserByUsername(auth.getUsername());
	    return Response.ok(user).header(ISiteWhereWebConstants.HEADER_JWT, accessToken.getToken()).build();
	}
	LOGGER.warn("No user context found for current thread.");
	return Response.status(Status.UNAUTHORIZED).build();
    }

    /**
     * Get access token for the authenticated user.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/token")
    @ApiOperation(value = "Get access token for authenticated user.")
    public Response accessToken() throws SiteWhereException {
	SiteWhereAuthentication auth = UserContext.getCurrentUser();
	if (auth != null) {
	    return Response.ok(auth.getJwt()).build();
	}
	return Response.status(Status.UNAUTHORIZED).build();
    }

    /**
     * Get public key used to decode JWT.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/key")
    @ApiOperation(value = "Get public key for decoding access token")
    public Response publicKey() throws SiteWhereException {
	SiteWhereAuthentication auth = UserContext.getCurrentUser();
	if (auth != null) {
	    return Response.ok(getMicroservice().getUserManagement().getPublicKey()).build();
	}
	return Response.status(Status.UNAUTHORIZED).build();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}