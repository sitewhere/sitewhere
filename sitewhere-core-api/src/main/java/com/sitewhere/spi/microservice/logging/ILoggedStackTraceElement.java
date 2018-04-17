/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.logging;

/**
 * Element in a logged stack trace.
 * 
 * @author Derek
 */
public interface ILoggedStackTraceElement {

    /**
     * Get class name.
     * 
     * @return
     */
    public String getClassname();

    /**
     * Get method.
     * 
     * @return
     */
    public String getMethod();

    /**
     * Get file name.
     * 
     * @return
     */
    public String getFile();

    /**
     * Get line number.
     * 
     * @return
     */
    public long getLineNumber();
}