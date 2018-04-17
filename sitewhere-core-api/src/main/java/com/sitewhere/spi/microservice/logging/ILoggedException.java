/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.logging;

import java.util.List;

/**
 * Exception included as part of a log message.
 * 
 * @author Derek
 */
public interface ILoggedException {

    /**
     * Get exception message text.
     * 
     * @return
     */
    public String getMessageText();

    /**
     * Get list of stack trace elements.
     * 
     * @return
     */
    public List<ILoggedStackTraceElement> getStackTraceElements();
}
