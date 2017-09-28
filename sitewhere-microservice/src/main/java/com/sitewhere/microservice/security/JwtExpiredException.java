package com.sitewhere.microservice.security;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception thrown when an expired JWT is used for a REST request.
 * 
 * @author Derek
 */
public class JwtExpiredException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = -5163059734369946339L;

    public JwtExpiredException() {
    }

    public JwtExpiredException(String message, Throwable cause) {
	super(message, cause);
    }

    public JwtExpiredException(String message) {
	super(message);
    }

    public JwtExpiredException(Throwable cause) {
	super(cause);
    }
}