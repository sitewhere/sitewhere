/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.common.security.NotAuthorizedException;
import com.sitewhere.grpc.client.common.security.UnauthenticatedException;
import com.sitewhere.grpc.client.common.tracing.DebugParameter;
import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.ServiceNotAvailableException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.IGrantedAuthority;

import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;

public class GrpcUtils {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(GrpcUtils.class);

    /** Hashmap of JWT to decoded claims */
    private static Map<String, Claims> jwtToClaims = new HashMap<String, Claims>();

    public static void handleClientMethodEntry(IApiChannel<?> channel, MethodDescriptor<?, ?> method,
	    DebugParameter... parameters) {
	LOGGER.debug(channel.getClass().getSimpleName() + " connected to '" + channel.getFunctionIdentifier().getPath()
		+ "' sending call to  " + method.getFullMethodName() + ".");
	if (LOGGER.isTraceEnabled()) {
	    for (DebugParameter parameter : parameters) {
		if (parameter.getContent() instanceof String) {
		    LOGGER.trace(parameter.getName() + ":" + parameter.getContent());
		} else {
		    LOGGER.trace(parameter.getName() + ":\n\n"
			    + MarshalUtils.marshalJsonAsPrettyString(parameter.getContent()));
		}
	    }
	}
    }

    /**
     * Log the encoded GRPC request sent from client.
     * 
     * @param request
     * @return
     */
    public static <T> T logGrpcClientRequest(MethodDescriptor<?, ?> method, T request) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace(
		    "Encoded GRPC request being sent to " + method.getFullMethodName() + ":\n\n" + request.toString());
	}
	return request;
    }

    /**
     * Handle entry logic for a gRPC server method.
     * 
     * @param api
     * @param method
     */
    public static void handleServerMethodEntry(IGrpcApiImplementation api, MethodDescriptor<?, ?> method) {
	LOGGER.debug("Server received call to  " + method.getFullMethodName() + ".");
	displaySecurityContext();
	String jwt = GrpcContextKeys.JWT_KEY.get();
	if (jwt == null) {
	    throw new RuntimeException("JWT not found in server request.");
	}
	ITenant tenant = null;
	try {
	    if (api.getMicroservice() instanceof IMultitenantMicroservice) {
		String tenantId = GrpcContextKeys.TENANT_TOKEN_KEY.get();
		if (tenantId != null) {
		    IMicroserviceTenantEngine engine = ((IMultitenantMicroservice<?, ?>) api.getMicroservice())
			    .assureTenantEngineAvailable(tenantId);
		    tenant = engine.getTenant();
		}
	    }
	    Claims claims = getClaimsForJwt(api, jwt);
	    String username = api.getMicroservice().getTokenManagement().getUsernameFromClaims(claims);
	    List<IGrantedAuthority> gauths = api.getMicroservice().getTokenManagement()
		    .getGrantedAuthoritiesFromClaims(claims);
	    List<String> auths = gauths.stream().map(g -> g.getAuthority()).collect(Collectors.toList());
	    establishSecurityContext(jwt, username, gauths, auths, tenant);
	} catch (SiteWhereException e) {
	    LOGGER.error("Error in gRPC server method " + method.getFullMethodName(), e);
	}
    }

    /**
     * Get cached claims for JWT.
     * 
     * @param jwt
     * @return
     * @throws SiteWhereException
     */
    protected static Claims getClaimsForJwt(IGrpcApiImplementation api, String jwt) throws SiteWhereException {
	// TODO: Swap to expiring cache and put limits on number of cached JWTs.
	Claims claims = jwtToClaims.get(jwt);
	if (claims == null) {
	    claims = api.getMicroservice().getTokenManagement().getClaimsForToken(jwt);
	    jwtToClaims.put(jwt, claims);
	}
	return claims;
    }

    /**
     * Indicate that Spring security content was not properly cleared previously.
     */
    protected static void displaySecurityContext() {
	// Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	// if (auth != null) {
	// String user = (String) auth.getPrincipal();
	// LOGGER.warn("!!! Thread already has Spring Security auth info !!! user=" +
	// user);
	// }
    }

    /**
     * Build Spring Security context based on values passed via gRPC interceptors.
     * 
     * @param jwt
     * @param username
     * @param gauths
     * @param auths
     * @param tenantId
     */
    protected static void establishSecurityContext(String jwt, String username, List<IGrantedAuthority> gauths,
	    List<String> auths, ITenant tenant) {
	User user = new User();
	user.setUsername(username);
	user.setAuthorities(auths);
	// SitewhereUserDetails details = new SitewhereUserDetails(user, gauths);
	// SitewhereAuthentication auth = new SitewhereAuthentication(details, jwt);
	// if (tenant != null) {
	// auth.setTenant(tenant);
	// }
	// SecurityContextHolder.getContext().setAuthentication(auth);
	LOGGER.trace("Set security context: username=" + username + " jwt=" + jwt);
    }

    public static void logServerApiResult(MethodDescriptor<?, ?> method, Object result) throws SiteWhereException {
	if (result != null) {
	    if (LOGGER.isTraceEnabled()) {
		LOGGER.trace("API result for " + method.getFullMethodName() + ":\n\n"
			+ MarshalUtils.marshalJsonAsPrettyString(result));
	    }
	} else {
	    LOGGER.trace("Response to " + method.getFullMethodName() + " was NULL");
	}
    }

    public static void handleServerMethodExit(MethodDescriptor<?, ?> method) {
	LOGGER.debug("Server finished call to  " + method.getFullMethodName() + ".");
	// SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * Log response returned by client method invocation.
     * 
     * @param method
     * @param o
     * @throws SiteWhereException
     */
    public static void logClientMethodResponse(MethodDescriptor<?, ?> method, Object o) throws SiteWhereException {
	if (o != null) {
	    if (LOGGER.isTraceEnabled()) {
		LOGGER.trace("Response to " + method.getFullMethodName() + ":\n\n"
			+ MarshalUtils.marshalJsonAsPrettyString(o));
	    }
	} else {
	    LOGGER.trace("Response to " + method.getFullMethodName() + " was NULL");
	}
    }

    /**
     * Handle an exception encountered during a call from a GRPC client.
     * 
     * @param method
     * @param t
     * @return
     */
    public static SiteWhereException handleClientMethodException(MethodDescriptor<?, ?> method, Throwable t) {
	if (t instanceof StatusRuntimeException) {
	    StatusRuntimeException sre = (StatusRuntimeException) t;
	    switch (sre.getStatus().getCode()) {
	    case PERMISSION_DENIED: {
		return new NotAuthorizedException("Not authorized for operation.", sre);
	    }
	    case UNAUTHENTICATED: {
		return new UnauthenticatedException(sre.getStatus().getDescription(), sre);
	    }
	    case UNAVAILABLE: {
		return new ServiceNotAvailableException(
			String.format("The requested service is not available [%s]", sre.getMessage()), sre);
	    }
	    case FAILED_PRECONDITION: {
		String delimited = sre.getStatus().getDescription();
		String[] parts = delimited.split(":");
		ErrorCode code = ErrorCode.fromCode(Long.parseLong(parts[0]));
		if (ErrorCode.Error == code) {
		    return new SiteWhereException(parts[1]);
		} else {
		    return new SiteWhereSystemException(code, ErrorLevel.ERROR);
		}
	    }
	    default: {
	    }
	    }
	}
	return new SiteWhereException("Client exception in call to " + method.getFullMethodName() + ".", t);
    }

    /**
     * Handle server exception by logging it, then converting to a format that can
     * be passed back across the wire to a client.
     * 
     * @param method
     * @param t
     * @param observer
     */
    public static void handleServerMethodException(MethodDescriptor<?, ?> method, Throwable t,
	    StreamObserver<?> observer) {
	LOGGER.error("Server exception in call to " + method.getFullMethodName() + ".", t);
	Throwable thrown = convertServerException(t);
	observer.onError(thrown);
    }

    /**
     * Convert server exception to one that can be passed back via GRPC.
     * 
     * @param t
     * @return
     */
    public static StatusException convertServerException(Throwable t) {
	StatusException thrown = null;
	if (t instanceof SiteWhereSystemException) {
	    SiteWhereSystemException sysex = (SiteWhereSystemException) t;
	    Status status = Status.fromCode(Code.FAILED_PRECONDITION)
		    .withDescription(sysex.getCode().getCode() + ":" + sysex.getCode().getMessage());
	    thrown = status.asException();
	} else if (t instanceof TenantEngineNotAvailableException) {
	    TenantEngineNotAvailableException sw = (TenantEngineNotAvailableException) t;
	    Status status = Status.fromCode(Code.UNAVAILABLE).withDescription(sw.getMessage());
	    thrown = status.asException();
	} else if (t instanceof SiteWhereException) {
	    SiteWhereException sw = (SiteWhereException) t;
	    Status status = Status.fromCode(Code.FAILED_PRECONDITION)
		    .withDescription(ErrorCode.Error.getCode() + ":" + sw.getMessage());
	    thrown = status.asException();
	} else {
	    thrown = Status.fromThrowable(t).asException();
	}
	return thrown;
    }
}