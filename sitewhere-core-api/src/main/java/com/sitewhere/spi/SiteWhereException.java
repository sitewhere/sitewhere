/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi;

import java.io.IOException;

/**
 * Base class for SiteWhere exceptions.
 * 
 * @author Derek Adams
 */
public class SiteWhereException extends IOException {

    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    public SiteWhereException() {
	super();
    }

    public SiteWhereException(String message, Throwable cause) {
	super(message, cause);
    }

    public SiteWhereException(String message) {
	super(message);
    }

    public SiteWhereException(Throwable cause) {
	super(cause);
    }
}