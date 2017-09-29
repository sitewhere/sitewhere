package com.sitewhere.grpc.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.grpc.model.security.NotAuthorizedException;
import com.sitewhere.grpc.model.security.UnauthenticatedException;
import com.sitewhere.spi.SiteWhereException;

import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;

public class GrpcUtils {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public static void logClientMethodEntry(MethodDescriptor<?, ?> method) {
	LOGGER.debug("Client received call to  " + method.getFullMethodName() + ".");
    }

    public static void logServerMethodEntry(MethodDescriptor<?, ?> method) {
	LOGGER.debug("Server received call to  " + method.getFullMethodName() + ".");
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
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Response to " + method.getFullMethodName() + ":\n\n"
			+ MarshalUtils.marshalJsonAsPrettyString(o));
	    }
	} else {
	    LOGGER.debug("Response to " + method.getFullMethodName() + " was NULL");
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
		return new UnauthenticatedException("No credentials passed for authentication.", sre);
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