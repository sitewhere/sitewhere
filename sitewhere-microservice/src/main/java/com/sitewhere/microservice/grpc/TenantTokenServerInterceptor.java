/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.client.TenantTokenClientInterceptor;
import com.sitewhere.spi.microservice.IMicroservice;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * Pulls tenant token from call metadata and adds it to context for use in
 * implementation class.
 * 
 * @author Derek
 */
public class TenantTokenServerInterceptor implements ServerInterceptor {

    /** Key for accessing requested tenant id */
    public static final Context.Key<String> TENANT_TOKEN_KEY = Context.key("tenant");

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Parent microservice */
    private IMicroservice microservice;

    public TenantTokenServerInterceptor(IMicroservice microservice) {
	this.microservice = microservice;
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
	if (headers.containsKey(TenantTokenClientInterceptor.TENANT_TOKEN_KEY)) {
	    String tenant = headers.get(TenantTokenClientInterceptor.TENANT_TOKEN_KEY);
	    LOGGER.debug("Received tenant token key: " + tenant);
	    Context ctx = Context.current().withValue(TENANT_TOKEN_KEY, tenant);
	    return Contexts.interceptCall(ctx, call, headers, next);
	} else {
	    call.close(Status.UNAUTHENTICATED.withDescription("No tenant token passed in metadata."), headers);
	    return new ServerCall.Listener<ReqT>() {
	    };
	}
    }

    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }
}