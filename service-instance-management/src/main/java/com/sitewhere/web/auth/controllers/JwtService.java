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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.security.SiteWhereAuthentication;
import com.sitewhere.microservice.security.UserContext;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.web.ISiteWhereWebConstants;

/**
 * Controller for security operations.
 */
@RestController
@RequestMapping("/authapi")
public class JwtService {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(JwtService.class);

    @Autowired
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
    @GetMapping("/jwt")
    public ResponseEntity<?> jwtWithUserDetail() throws SiteWhereException {
	SiteWhereAuthentication auth = UserContext.getCurrentUser();
	if (auth != null) {
	    AccessTokenResponse accessToken = MarshalUtils.unmarshalJson(auth.getJwt().getBytes(),
		    AccessTokenResponse.class);
	    IUser user = getMicroservice().getUserManagement().getUserByUsername(auth.getUsername());
	    HttpHeaders headers = new HttpHeaders();
	    headers.add(ISiteWhereWebConstants.HEADER_JWT, accessToken.getToken());
	    return ResponseEntity.ok().headers(headers).body(user);
	}
	LOGGER.warn("No user context found for current thread.");
	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Get access token for the authenticated user.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/token")
    public ResponseEntity<?> accessToken() throws SiteWhereException {
	SiteWhereAuthentication auth = UserContext.getCurrentUser();
	if (auth != null) {
	    return ResponseEntity.ok(auth.getJwt());
	}
	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Get public key used to decode JWT.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/key")
    public ResponseEntity<?> publicKey() throws SiteWhereException {
	SiteWhereAuthentication auth = UserContext.getCurrentUser();
	if (auth != null) {
	    return ResponseEntity.ok(getMicroservice().getUserManagement().getPublicKey());
	}
	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}