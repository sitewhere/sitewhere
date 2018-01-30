/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.grpc.model.security.NotAuthorizedException;
import com.sitewhere.grpc.model.security.UnauthenticatedException;
import com.sitewhere.grpc.model.tracing.DebugParameter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.ServiceNotAvailableException;

import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;

public class GrpcUtils {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public static void logClientMethodEntry(IApiChannel<?> channel, MethodDescriptor<?, ?> method,
	    DebugParameter... parameters) throws SiteWhereException {
	LOGGER.debug(channel.getClass().getSimpleName() + " connected to '" + channel.getHostname()
		+ "' received call to  " + method.getFullMethodName() + ".");
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

    public static void logServerMethodEntry(MethodDescriptor<?, ?> method) {
	LOGGER.debug("Server received call to  " + method.getFullMethodName() + ".");
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
		return new ServiceNotAvailableException("The requested service is not available.", sre);
	    }
	    default: {
	    }
	    }
	}
	return new SiteWhereException("Client exception in call to " + method.getFullMethodName() + ".", t);
    }

    public static void logServerMethodException(MethodDescriptor<?, ?> method, Throwable t) {
	LOGGER.error("Server exception in call to " + method.getFullMethodName() + ".", t);
    }
}