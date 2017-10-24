/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.model.client.JwtClientInterceptor;
import com.sitewhere.microservice.security.annotations.GrpcSecured;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.SiteWhereAuthority;

import io.grpc.BindableService;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
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
    private static Logger LOGGER = LogManager.getLogger();

    /** Parent microservice */
    private IMicroservice microservice;

    /** Service implementation */
    private Class<? extends BindableService> implementation;

    /** Map of implementation methods indexed by full name from descriptor */
    private Map<String, Method> methodsByFullName = new HashMap<String, Method>();

    /** Hashmap of JWT to decoded claims */
    private Map<String, Claims> jwtToClaims = new HashMap<String, Claims>();

    public JwtServerInterceptor(IMicroservice microservice, Class<? extends BindableService> implementation) {
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
	    try {
		Claims claims = getClaimsForJwt(jwt);
		String username = getMicroservice().getTokenManagement().getUsernameFromClaims(claims);
		List<IGrantedAuthority> gauths = getMicroservice().getTokenManagement()
			.getGrantedAuthoritiesFromClaims(claims);
		List<String> auths = gauths.stream().map(g -> g.getAuthority()).collect(Collectors.toList());
		Method implMethod = locateMethod(call.getMethodDescriptor());
		return processAuthAnnotations(call, headers, next, username, auths, implMethod);
	    } catch (SiteWhereException e) {
		call.close(Status.PERMISSION_DENIED.withDescription(e.getMessage()), headers);
		return new ServerCall.Listener<ReqT>() {
		};
	    }
	} else {
	    call.close(Status.UNAUTHENTICATED.withDescription("JWT not passed in metadata."), headers);
	    return new ServerCall.Listener<ReqT>() {
	    };
	}
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

    /**
     * Process authentication annotations on the implementation method.
     * 
     * @param call
     * @param headers
     * @param next
     * @param username
     * @param auths
     * @param method
     * @return
     * @throws SiteWhereException
     */
    protected <ReqT, RespT> Listener<ReqT> processAuthAnnotations(ServerCall<ReqT, RespT> call, Metadata headers,
	    ServerCallHandler<ReqT, RespT> next, String username, List<String> auths, Method method)
	    throws SiteWhereException {
	processGrpcSecured(call, headers, next, username, auths, method);
	return next.startCall(call, headers);
    }

    /**
     * Process {@link GrpcSecured} annotation.
     * 
     * @param call
     * @param headers
     * @param next
     * @param username
     * @param auths
     * @param method
     * @return
     * @throws SiteWhereException
     */
    protected <ReqT, RespT> void processGrpcSecured(ServerCall<ReqT, RespT> call, Metadata headers,
	    ServerCallHandler<ReqT, RespT> next, String username, List<String> auths, Method method)
	    throws SiteWhereException {
	GrpcSecured secured = method.getAnnotation(GrpcSecured.class);
	if (secured != null) {
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Found @GrpcSecured annotation on " + method.toGenericString());
	    }
	    SiteWhereAuthority[] roles = secured.value();
	    for (SiteWhereAuthority role : roles) {
		if (!auths.contains(role.getName())) {
		    throw new SiteWhereException("User '" + username + "' not authenticated for '" + role
			    + "' authority.\n\n" + MarshalUtils.marshalJsonAsPrettyString(auths));
		} else {
		    LOGGER.info("SECURITY CHECK PASSED FOR " + role);
		}
	    }
	}
    }

    /**
     * Locate implementation method based on name from descriptor.
     * 
     * @param descriptor
     * @return
     * @throws SiteWhereException
     */
    protected Method locateMethod(MethodDescriptor<?, ?> descriptor) throws SiteWhereException {
	String fullName = descriptor.getFullMethodName();
	Method match = getMethodsByFullName().get(fullName);
	if (match == null) {
	    String[] parts = fullName.split("/");
	    String camelName = parts[1];

	    // Lowercase first letter.
	    String realName = camelName.substring(0, 1).toLowerCase() + camelName.substring(1);
	    Method[] methods = getImplementation().getDeclaredMethods();
	    for (Method method : methods) {
		if (method.getName().equals(realName)) {
		    getMethodsByFullName().put(fullName, method);
		    return method;
		}
	    }
	    throw new SiteWhereException(
		    "Unable to locate method '" + realName + "' on " + getImplementation().getName() + ".");
	} else {
	    return match;
	}
    }

    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }

    public Class<? extends BindableService> getImplementation() {
	return implementation;
    }

    public void setImplementation(Class<? extends BindableService> implementation) {
	this.implementation = implementation;
    }

    public Map<String, Method> getMethodsByFullName() {
	return methodsByFullName;
    }

    public void setMethodsByFullName(Map<String, Method> methodsByFullName) {
	this.methodsByFullName = methodsByFullName;
    }
}