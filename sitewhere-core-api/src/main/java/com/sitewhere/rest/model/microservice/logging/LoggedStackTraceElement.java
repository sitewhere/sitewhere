/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.logging;

import com.sitewhere.spi.microservice.logging.ILoggedStackTraceElement;

/**
 * Element in a logged stack trace.
 * 
 * @author Derek
 */
public class LoggedStackTraceElement implements ILoggedStackTraceElement {

    /** Class name */
    private String classname;

    /** Method */
    private String method;

    /** File */
    private String file;

    /** Line number */
    private long lineNumber;

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.ILoggedStackTraceElement#getClassname(
     * )
     */
    @Override
    public String getClassname() {
	return classname;
    }

    public void setClassname(String classname) {
	this.classname = classname;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.ILoggedStackTraceElement#getMethod()
     */
    @Override
    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.ILoggedStackTraceElement#getFile()
     */
    @Override
    public String getFile() {
	return file;
    }

    public void setFile(String file) {
	this.file = file;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.ILoggedStackTraceElement#getLineNumber
     * ()
     */
    @Override
    public long getLineNumber() {
	return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
	this.lineNumber = lineNumber;
    }
}
