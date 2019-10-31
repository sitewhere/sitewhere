/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exeception indicating a remote service has become unavailable.
 */
public class ServiceNotAvailableException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 6410813964583998335L;

    public ServiceNotAvailableException() {
    }

    public ServiceNotAvailableException(String message, Throwable cause) {
	super(message, cause);
    }

    public ServiceNotAvailableException(String message) {
	super(message);
    }

    public ServiceNotAvailableException(Throwable cause) {
	super(cause);
    }
}