/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.grpc.client.JwtClientInterceptor;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.security.SitewhereAuthentication;
import com.sitewhere.security.SitewhereUserDetails;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.user.IGrantedAuthority;

import io.grpc.BindableService;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.jsonwebtoken.Claims;

/**
 * Interceptor that enforces JWT authentication constraints before invoking
 * service methods.
 * 
 * @author Derek
 */
public class JwtServerInterceptor implements ServerInterceptor {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(JwtServerInterceptor.class);

    /** Parent microservice */
    private IMicroservice<?> microservice;

    /** Service implementation */
    private Class<? extends BindableService> implementation;

    /** Hashmap of JWT to decoded claims */
    private Map<String, Claims> jwtToClaims = new HashMap<String, Claims>();

    public JwtServerInterceptor(IMicroservice<?> microservice, Class<? extends BindableService> implementation) {
	this.microservice = microservice;
	this.implementation = implementation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.grpc.ServerInterceptor#interceptCall(io.grpc.ServerCall,
     * io.grpc.Metadata, io.grpc.ServerCallHandler)
     */
    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
	    ServerCallHandler<ReqT, RespT> next) {
	if (headers.containsKey(JwtClientInterceptor.JWT_KEY)) {
	    String jwt = headers.get(JwtClientInterceptor.JWT_KEY);
	    long start = System.currentTimeMillis();
	    try {
		Claims claims = getClaimsForJwt(jwt);
		String username = getMicroservice().getTokenManagement().getUsernameFromClaims(claims);
		List<IGrantedAuthority> gauths = getMicroservice().getTokenManagement()
			.getGrantedAuthoritiesFromClaims(claims);
		List<String> auths = gauths.stream().map(g -> g.getAuthority()).collect(Collectors.toList());
		establishSecurityContext(jwt, username, gauths, auths);
		return next.startCall(call, headers);
	    } catch (SiteWhereException e) {
		call.close(Status.PERMISSION_DENIED.withDescription(e.getMessage()), headers);
		return new ServerCall.Listener<ReqT>() {
		};
	    } finally {
		LOGGER.trace("GRPC call for " + call.getMethodDescriptor().getFullMethodName() + " took "
			+ (System.currentTimeMillis() - start) + "ms.");
	    }
	} else {
	    call.close(Status.UNAUTHENTICATED.withDescription("JWT not passed in metadata."), headers);
	    return new ServerCall.Listener<ReqT>() {
	    };
	}
    }

    /**
     * Establish mock Spring Security context based on information included in JWT.
     * 
     * @param jwt
     * @param username
     * @param gauths
     * @param auths
     */
    protected void establishSecurityContext(String jwt, String username, List<IGrantedAuthority> gauths,
	    List<String> auths) {
	User mockUser = new User();
	mockUser.setUsername(username);
	mockUser.setAuthorities(auths);
	SitewhereUserDetails details = new SitewhereUserDetails(mockUser, gauths);
	SitewhereAuthentication mockAuth = new SitewhereAuthentication(details, jwt);
	SecurityContextHolder.getContext().setAuthentication(mockAuth);
    }

    /**
     * Get cached claims for JWT.
     * 
     * @param jwt
     * @return
     * @throws SiteWhereException
     */
    protected Claims getClaimsForJwt(String jwt) throws SiteWhereException {
	// TODO: Swap to expiring cache and put limits on number of cached JWTs.
	Claims claims = jwtToClaims.get(jwt);
	if (claims == null) {
	    claims = getMicroservice().getTokenManagement().getClaimsForToken(jwt);
	    jwtToClaims.put(jwt, claims);
	}
	return claims;
    }

    public IMicroservice<?> getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice<?> microservice) {
	this.microservice = microservice;
    }

    public Class<? extends BindableService> getImplementation() {
	return implementation;
    }

    public void setImplementation(Class<? extends BindableService> implementation) {
	this.implementation = implementation;
    }
}