package com.sitewhere.grpc.model.security;

import com.sitewhere.spi.SiteWhereException;

public class NotAuthorizedException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = -5159906742506934294L;

    public NotAuthorizedException() {
    }

    public NotAuthorizedException(String message, Throwable cause) {
	super(message, cause);
    }

    public NotAuthorizedException(String message) {
	super(message);
    }

    public NotAuthorizedException(Throwable cause) {
	super(cause);
    }
}