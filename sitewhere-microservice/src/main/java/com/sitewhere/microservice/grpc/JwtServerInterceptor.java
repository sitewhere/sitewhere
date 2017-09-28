package com.sitewhere.microservice.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.client.JwtClientInterceptor;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.SiteWhereException;

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
    private static Logger LOGGER = LogManager.getLogger();

    /** Parent microservice */
    private IMicroservice microservice;

    /** Service implementation */
    private BindableService implementation;

    public JwtServerInterceptor(IMicroservice microservice, BindableService implementation) {
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
	LOGGER.debug("MADE IT TO SERVER INTERCEPTOR! " + call.getMethodDescriptor().getFullMethodName() + " IMPL: "
		+ getImplementation().getClass().getName());
	if (headers.containsKey(JwtClientInterceptor.JWT_KEY)) {
	    String jwt = headers.get(JwtClientInterceptor.JWT_KEY);
	    try {
		Claims claims = getMicroservice().getTokenManagement().getClaimsForToken(jwt);
		LOGGER.info("AUTHENTICATED USERNAME: "
			+ getMicroservice().getTokenManagement().getUsernameFromClaims(claims));
		return next.startCall(call, headers);
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

    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }

    public BindableService getImplementation() {
	return implementation;
    }

    public void setImplementation(BindableService implementation) {
	this.implementation = implementation;
    }
}