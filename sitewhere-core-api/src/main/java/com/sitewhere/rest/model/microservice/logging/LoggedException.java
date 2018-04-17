/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.logging;

import java.util.List;

import com.sitewhere.spi.microservice.logging.ILoggedException;
import com.sitewhere.spi.microservice.logging.ILoggedStackTraceElement;

/**
 * Exception included as part of a log message.
 * 
 * @author Derek
 */
public class LoggedException implements ILoggedException {

    /** Exception message text */
    private String messageText;

    /** Stack trace elements */
    private List<ILoggedStackTraceElement> stackTraceElements;

    /*
     * @see com.sitewhere.spi.microservice.logging.ILoggedException#getMessageText()
     */
    @Override
    public String getMessageText() {
	return messageText;
    }

    public void setMessageText(String messageText) {
	this.messageText = messageText;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.ILoggedException#getStackTraceElements
     * ()
     */
    @Override
    public List<ILoggedStackTraceElement> getStackTraceElements() {
	return stackTraceElements;
    }

    public void setStackTraceElements(List<ILoggedStackTraceElement> stackTraceElements) {
	this.stackTraceElements = stackTraceElements;
    }
}
