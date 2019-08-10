/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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