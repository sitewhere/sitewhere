/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.error;

import com.sitewhere.spi.SiteWhereException;

/**
 * Indicates that a "create" operation resulted in a duplicate key.
 * 
 * @author Derek
 */
public class ResourceExistsException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 997625714231990638L;

    /** SiteWhere error code */
    private ErrorCode code;

    public ResourceExistsException(ErrorCode code) {
	this.code = code;
    }

    public ErrorCode getCode() {
	return code;
    }

    public void setCode(ErrorCode code) {
	this.code = code;
    }
}