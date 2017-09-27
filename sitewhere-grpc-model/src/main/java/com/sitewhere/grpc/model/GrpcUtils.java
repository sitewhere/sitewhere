package com.sitewhere.grpc.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;

import io.grpc.MethodDescriptor;

public class GrpcUtils {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public static void logClientMethodEntry(MethodDescriptor<?, ?> method) {
	LOGGER.debug("Client received call to  " + method.getFullMethodName() + ".");
    }

    public static void logServerMethodEntry(MethodDescriptor<?, ?> method) {
	LOGGER.debug("Server received call to  " + method.getFullMethodName() + ".");
    }

    public static void logClientMethodResponse(MethodDescriptor<?, ?> method, Object o) throws SiteWhereException {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(
		    "Response to " + method.getFullMethodName() + ":\n\n" + MarshalUtils.marshalJsonAsPrettyString(o));
	}
    }

    public static SiteWhereException handleClientMethodException(MethodDescriptor<?, ?> method, Throwable t)
	    throws SiteWhereException {
	return new SiteWhereException("Client exception in call to " + method.getFullMethodName() + ".", t);
    }

    public static void logServerMethodException(MethodDescriptor<?, ?> method, Throwable t) {
	LOGGER.error("Server exception in call to " + method.getFullMethodName() + ".", t);
    }
}