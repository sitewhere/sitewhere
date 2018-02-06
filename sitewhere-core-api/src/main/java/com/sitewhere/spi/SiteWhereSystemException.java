/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi;

import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * System-level exception. Usually non-recoverable.
 * 
 * @author Derek Adams
 */
public class SiteWhereSystemException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = -7022467595218782867L;

    /** Error code */
    private ErrorCode code;

    /** Error level */
    private ErrorLevel level;

    /** HTTP response code (optional) */
    private int httpResponseCode = -1;

    public SiteWhereSystemException(ErrorCode code, ErrorLevel level) {
	this(code, null, level);
    }

    public SiteWhereSystemException(ErrorCode code, ErrorLevel level, int httpResponseCode) {
	this(code, null, level);
	setHttpResponseCode(httpResponseCode);
    }

    public SiteWhereSystemException(ErrorCode code, Throwable cause, ErrorLevel level) {
	super(code.getMessage(), cause);
	this.code = code;
	this.level = level;
    }

    /**
     * Get the error code.
     * 
     * @return
     */
    public ErrorCode getCode() {
	return code;
    }

    /**
     * Set the error code.
     * 
     * @param code
     */
    public void setCode(ErrorCode code) {
	this.code = code;
    }

    /**
     * Get the error level.
     * 
     * @return
     */
    public ErrorLevel getLevel() {
	return level;
    }

    /**
     * Set the error level.
     * 
     * @param level
     */
    public void setLevel(ErrorLevel level) {
	this.level = level;
    }

    /**
     * Get HTTP response code. (Used in REST services)
     * 
     * @return
     */
    public int getHttpResponseCode() {
	return httpResponseCode;
    }

    /**
     * Set HTTP response code. (Used in REST services)
     * 
     * @param httpResponseCode
     */
    public void setHttpResponseCode(int httpResponseCode) {
	this.httpResponseCode = httpResponseCode;
    }

    /**
     * Check whether an HTTP response code was set.
     * 
     * @return
     */
    public boolean hasHttpResponseCode() {
	return getHttpResponseCode() != -1;
    }
}