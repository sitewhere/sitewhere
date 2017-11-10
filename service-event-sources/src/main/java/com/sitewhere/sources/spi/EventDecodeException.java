/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.spi;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception thrown when an event payload can not be decoded.
 * 
 * @author Derek
 */
public class EventDecodeException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 1994831211720002160L;

    public EventDecodeException() {
    }

    public EventDecodeException(String message, Throwable cause) {
	super(message, cause);
    }

    public EventDecodeException(String message) {
	super(message);
    }

    public EventDecodeException(Throwable cause) {
	super(cause);
    }
}