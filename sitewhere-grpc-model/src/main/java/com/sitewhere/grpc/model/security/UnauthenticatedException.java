package com.sitewhere.grpc.model.security;

import com.sitewhere.spi.SiteWhereException;

public class UnauthenticatedException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 1427582585306645204L;

    public UnauthenticatedException() {
    }

    public UnauthenticatedException(String message, Throwable cause) {
	super(message, cause);
    }

    public UnauthenticatedException(String message) {
	super(message);
    }

    public UnauthenticatedException(Throwable cause) {
	super(cause);
    }
}