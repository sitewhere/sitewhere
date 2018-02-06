/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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