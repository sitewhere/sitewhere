/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.exception;

import com.sitewhere.spi.SiteWhereException;

/**
 * Signals condition where an update is attempted, but another requests has
 * updated the same object in the meantime.
 */
public class ConcurrentK8sUpdateException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 1412398944204806395L;

    public ConcurrentK8sUpdateException() {
    }

    public ConcurrentK8sUpdateException(String message) {
	super(message);
    }

    public ConcurrentK8sUpdateException(Throwable cause) {
	super(cause);
    }

    public ConcurrentK8sUpdateException(String message, Throwable cause) {
	super(message, cause);
    }
}
