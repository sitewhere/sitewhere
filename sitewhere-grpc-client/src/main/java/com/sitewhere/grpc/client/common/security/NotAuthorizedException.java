/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.common.security;

import com.sitewhere.spi.SiteWhereException;

public class NotAuthorizedException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = -5159906742506934294L;

    public NotAuthorizedException() {
    }

    public NotAuthorizedException(String message, Throwable cause) {
	super(message, cause);
    }

    public NotAuthorizedException(String message) {
	super(message);
    }

    public NotAuthorizedException(Throwable cause) {
	super(cause);
    }
}