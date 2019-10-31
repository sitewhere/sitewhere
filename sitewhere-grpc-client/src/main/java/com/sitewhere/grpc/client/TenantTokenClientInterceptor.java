/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

/**
 * GRPC interceptor that pushes tenant token into GRPC call metadata.
 * 
 * @author Derek
 */
public class TenantTokenClientInterceptor implements ClientInterceptor {

    /** Tenant token metadata key */
    public static final Metadata.Key<String> TENANT_TOKEN_KEY = Metadata.Key.of("tenant",
	    Metadata.ASCII_STRING_MARSHALLER);

    /*
     * (non-Javadoc)
     * 
     * @see io.grpc.ClientInterceptor#interceptCall(io.grpc.MethodDescriptor,
     * io.grpc.CallOptions, io.grpc.Channel)
     */
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
	    CallOptions callOptions, Channel next) {
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
		// if ((authentication != null) && (authentication instanceof
		// ITenantAwareAuthentication)) {
		// ITenant tenant = ((ITenantAwareAuthentication) authentication).getTenant();
		// if (tenant != null) {
		// headers.put(TENANT_TOKEN_KEY, tenant.getToken().toString());
		// }
		// }
		super.start(responseListener, headers);
	    }
	};
    }
}