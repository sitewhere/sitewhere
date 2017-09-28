package com.sitewhere.microservice.security;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception thrown when an invalid JWT is used for a REST request.
 * 
 * @author Derek
 */
public class InvalidJwtException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 3310371658590158663L;

    public InvalidJwtException() {
    }

    public InvalidJwtException(String message, Throwable cause) {
	super(message, cause);
    }

    public InvalidJwtException(String message) {
	super(message);
    }

    public InvalidJwtException(Throwable cause) {
	super(cause);
    }
}