/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.common.security;

import com.sitewhere.spi.SiteWhereException;

public class UnauthenticatedException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 1427582585306645204L;

    public UnauthenticatedException() {
    }

    public UnauthenticatedException(String message, Throwable cause) {
	super(message, cause);
    }

    public UnauthenticatedException(String message) {
	super(message);
    }

    public UnauthenticatedException(Throwable cause) {
	super(cause);
    }
}