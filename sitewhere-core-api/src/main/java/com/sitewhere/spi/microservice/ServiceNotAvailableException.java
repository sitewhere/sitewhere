/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

/**
 * Indicates that a service is temporarily or permanently unavailable.
 * 
 * @author Derek
 */
public class ServiceNotAvailableException extends RuntimeException {

    /** Serial version UID */
    private static final long serialVersionUID = -1003297732383971503L;

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