/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ExceptionDetail {

    /** Error message */
    private String message;

    /** Error code */
    private Long errorCode;

    /** Error description */
    private String errorDescription;

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public Long getErrorCode() {
	return errorCode;
    }

    public void setErrorCode(Long errorCode) {
	this.errorCode = errorCode;
    }

    public String getErrorDescription() {
	return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
	this.errorDescription = errorDescription;
    }
}
