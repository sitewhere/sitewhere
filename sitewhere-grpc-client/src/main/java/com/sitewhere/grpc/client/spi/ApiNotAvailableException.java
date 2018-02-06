/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception indicating that a SiteWhere API is not available.
 * 
 * @author Derek
 */
public class ApiNotAvailableException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 9168984351535124935L;

    public ApiNotAvailableException() {
    }

    public ApiNotAvailableException(String message, Throwable cause) {
	super(message, cause);
    }

    public ApiNotAvailableException(String message) {
	super(message);
    }

    public ApiNotAvailableException(Throwable cause) {
	super(cause);
    }
}