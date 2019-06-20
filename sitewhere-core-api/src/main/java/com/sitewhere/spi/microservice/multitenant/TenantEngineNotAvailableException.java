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
 * Exception thrown when a tenant engine is expected to be available but is not
 * created and/or started.
 */
public class TenantEngineNotAvailableException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = -441640534300277367L;

    public TenantEngineNotAvailableException() {
    }

    public TenantEngineNotAvailableException(String message) {
	super(message);
    }

    public TenantEngineNotAvailableException(Throwable cause) {
	super(cause);
    }

    public TenantEngineNotAvailableException(String message, Throwable cause) {
	super(message, cause);
    }
}