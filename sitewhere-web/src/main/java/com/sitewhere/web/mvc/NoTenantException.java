/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.mvc;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception thrown when a page is accessed without a tenant in session.
 * 
 * @author Derek
 */
public class NoTenantException extends SiteWhereException {

	/** Serial version UID */
	private static final long serialVersionUID = 5632584510266953920L;

	public NoTenantException() {
	}

	public NoTenantException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoTenantException(String message) {
		super(message);
	}

	public NoTenantException(Throwable cause) {
		super(cause);
	}
}