/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import com.sitewhere.spi.SiteWhereException;

/**
 * Indicates no API channel is available to complete an operation.
 * 
 * @author Derek
 */
public class ApiChannelNotAvailableException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = -6971589967178711085L;

    public ApiChannelNotAvailableException() {
    }

    public ApiChannelNotAvailableException(String message) {
	super(message);
    }

    public ApiChannelNotAvailableException(Throwable cause) {
	super(cause);
    }

    public ApiChannelNotAvailableException(String message, Throwable cause) {
	super(message, cause);
    }
}