/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * GRPC interceptor that pushes JWT from Spring Security credentials into call
 * metadata.
 * 
 * @author Derek
 */
public class JwtClientInterceptor implements ClientInterceptor {

    /** Static logger instance */
    private static Logger LOGGER = LoggerFactory.getLogger(JwtClientInterceptor.class);

    /** JWT metadata key */
    public static final Metadata.Key<String> JWT_KEY = Metadata.Key.of("jwt", Metadata.ASCII_STRING_MARSHALLER);

    /*
     * (non-Javadoc)
     * 
     * @see io.grpc.ClientInterceptor#interceptCall(io.grpc.MethodDescriptor,
     * io.grpc.CallOptions, io.grpc.Channel)
     */
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
	    CallOptions callOptions, Channel next) {
	LOGGER.trace("Intercepting client call...");
	return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

	    /*
	     * (non-Javadoc)
	     * 
	     * @see io.grpc.ForwardingClientCall#start(io.grpc.ClientCall.Listener,
	     * io.grpc.Metadata)
	     */
	    @Override
	    public void start(Listener<RespT> responseListener, Metadata headers) {
		// Authentication authentication =
		// SecurityContextHolder.getContext().getAuthentication();
		// if (authentication == null) {
		// throw new RuntimeException("Attempting to make remote call with no Spring
		// Security context.");
		// }
		// String jwt = (String) authentication.getCredentials();
		// if (jwt == null) {
		// throw new RuntimeException("Attempting to make remote call with no JWT
		// provided.");
		// }
		// LOGGER.trace("Adding JWT into gRPC headers: " + jwt);
		// headers.put(JWT_KEY, jwt);
		super.start(responseListener, headers);
	    }
	};
    }
}