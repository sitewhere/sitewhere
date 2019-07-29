package com.sitewhere.spi.microservice.multitenant;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception when an attempt is made to access a non-existent tenant.
 */
public class InvalidTenantException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 2257088664595104672L;

    public InvalidTenantException() {
    }

    public InvalidTenantException(String message) {
	super(message);
    }

    public InvalidTenantException(Throwable cause) {
	super(cause);
    }

    public InvalidTenantException(String message, Throwable cause) {
	super(message, cause);
    }
}