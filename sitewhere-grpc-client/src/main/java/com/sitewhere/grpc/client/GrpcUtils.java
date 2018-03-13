/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.grpc.model.security.NotAuthorizedException;
import com.sitewhere.grpc.model.security.UnauthenticatedException;
import com.sitewhere.grpc.model.tracing.DebugParameter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.ServiceNotAvailableException;

import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class GrpcUtils {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GrpcUtils.class);

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

	Throwable thrown = t;
	if (t instanceof SiteWhereSystemException) {
	    SiteWhereSystemException sysex = (SiteWhereSystemException) t;
	    Status status = Status.fromCode(Code.FAILED_PRECONDITION)
		    .withDescription(sysex.getCode().getCode() + ":" + sysex.getCode().getMessage());
	    thrown = status.asException();
	} else if (t instanceof SiteWhereException) {
	    SiteWhereException sw = (SiteWhereException) t;
	    Status status = Status.fromCode(Code.FAILED_PRECONDITION)
		    .withDescription(ErrorCode.Error.getCode() + ":" + sw.getMessage());
	    thrown = status.asException();
	}

	observer.onError(thrown);
    }
}